package io.locian.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.locian.core.model.ChatMessage
import io.locian.core.model.ChatRole
import io.locian.data.datastore.AppPreferences
import io.locian.domain.inference.usecases.ManageConversationUseCase
import io.locian.domain.inference.usecases.StreamChatResponseUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val conversationId: Long? = null,
    val conversationTitle: String = "New Chat",
    val messages: List<ChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val streamingText: String = "",
    val activeModelName: String? = null,
    val isModelLoaded: Boolean = false,
    val latestSpeed: Float? = null,
    val latestTtft: Long? = null,
    val latestTokens: Int? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val streamChatResponseUseCase: StreamChatResponseUseCase,
    private val manageConversationUseCase: ManageConversationUseCase,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var streamJob: Job? = null

    init {
        appPreferences.activeModelName.onEach { modelName ->
            _uiState.update { it.copy(activeModelName = modelName) }
        }.launchIn(viewModelScope)

        appPreferences.activeModelPath.onEach { path ->
            _uiState.update { it.copy(isModelLoaded = !path.isNullOrEmpty()) }
        }.launchIn(viewModelScope)
    }

    fun setConversation(id: Long) {
        _uiState.update { it.copy(conversationId = id) }
        manageConversationUseCase.getMessages(id).onEach { msgs ->
            _uiState.update { it.copy(messages = msgs) }
        }.launchIn(viewModelScope)
    }

    fun startNewConversation() {
        _uiState.update {
            it.copy(
                conversationId = null,
                conversationTitle = "New Chat",
                messages = emptyList(),
                streamingText = "",
                isGenerating = false,
                errorMessage = null
            )
        }
    }

    fun sendMessage(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() || _uiState.value.isGenerating) return

        if (!_uiState.value.isModelLoaded) {
            _uiState.update { it.copy(errorMessage = "Please download and load a model from the Models tab first.") }
            return
        }

        viewModelScope.launch {
            var convId = _uiState.value.conversationId
            if (convId == null) {
                val title = if (trimmed.length > 30) trimmed.take(30) + "..." else trimmed
                convId = manageConversationUseCase.createConversation(
                    title = title,
                    modelId = _uiState.value.activeModelName ?: "model"
                )
                _uiState.update { it.copy(conversationId = convId, conversationTitle = title) }
                // Start observing messages for the new conversation
                manageConversationUseCase.getMessages(convId).onEach { msgs ->
                    _uiState.update { it.copy(messages = msgs) }
                }.launchIn(viewModelScope)
            }

            val userMsg = ChatMessage(
                conversationId = convId,
                role = ChatRole.USER,
                content = trimmed
            )
            streamChatResponseUseCase.saveMessage(userMsg)

            _uiState.update {
                it.copy(
                    isGenerating = true,
                    streamingText = "",
                    errorMessage = null
                )
            }

            val accumulated = StringBuilder()
            var tokenCount = 0

            streamJob = streamChatResponseUseCase(trimmed)
                .onEach { token ->
                    accumulated.append(token)
                    tokenCount++
                    _uiState.update { it.copy(streamingText = accumulated.toString()) }
                }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            errorMessage = error.localizedMessage ?: "Inference error"
                        )
                    }
                }
                .onEach { }
                .launchIn(viewModelScope)

            streamJob?.invokeOnCompletion { cause ->
                val fullResponse = accumulated.toString()
                val (speed, ttft, _) = streamChatResponseUseCase.getTelemetry()

                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        streamingText = "",
                        latestSpeed = if (speed > 0f) speed else null,
                        latestTtft = if (ttft > 0) ttft else null,
                        latestTokens = if (tokenCount > 0) tokenCount else null
                    )
                }

                if (fullResponse.isNotBlank() && convId != null) {
                    viewModelScope.launch {
                        val assistantMsg = ChatMessage(
                            conversationId = convId,
                            role = ChatRole.ASSISTANT,
                            content = fullResponse,
                            tokensPerSecond = speed,
                            timeToFirstTokenMs = ttft,
                            totalTokens = tokenCount
                        )
                        streamChatResponseUseCase.saveMessage(assistantMsg)
                    }
                }
            }
        }
    }

    fun stopGeneration() {
        streamJob?.cancel()
        streamChatResponseUseCase.stopGeneration()
        _uiState.update { it.copy(isGenerating = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
