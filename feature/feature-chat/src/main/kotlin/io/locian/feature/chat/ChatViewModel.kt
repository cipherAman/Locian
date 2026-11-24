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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    val showThinking: Boolean = true,
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
    private var messagesJob: Job? = null

    init {
        appPreferences.activeModelName.onEach { modelName ->
            _uiState.update { it.copy(activeModelName = modelName) }
        }.launchIn(viewModelScope)

        appPreferences.activeModelPath.onEach { path ->
            _uiState.update { it.copy(isModelLoaded = !path.isNullOrEmpty()) }
        }.launchIn(viewModelScope)

        appPreferences.showThinking.onEach { show ->
            _uiState.update { it.copy(showThinking = show) }
        }.launchIn(viewModelScope)
    }

    fun setConversation(id: Long) {
        // Stop any active generation and reset native engine session memory
        stopGeneration()
        streamChatResponseUseCase.clearSession()

        // Cancel previous message observer to avoid leaking or overwriting messages
        messagesJob?.cancel()

        viewModelScope.launch {
            val conv = manageConversationUseCase.getConversation(id)
            val title = conv?.title ?: "Chat"

            _uiState.update {
                it.copy(
                    conversationId = id,
                    conversationTitle = title,
                    messages = emptyList(),
                    streamingText = "",
                    isGenerating = false,
                    errorMessage = null
                )
            }

            messagesJob = manageConversationUseCase.getMessages(id).onEach { msgs ->
                _uiState.update { it.copy(messages = msgs) }
            }.launchIn(viewModelScope)
        }
    }

    fun startNewConversation() {
        // 1. Immediately abort active inference
        stopGeneration()

        // 2. Clear native LLM context & session cache so past conversation context is not retained
        streamChatResponseUseCase.clearSession()

        // 3. Cancel the message stream listener for the previous conversation
        messagesJob?.cancel()
        messagesJob = null

        // 4. Reset UI state to a clean slate
        _uiState.update {
            it.copy(
                conversationId = null,
                conversationTitle = "New Chat",
                messages = emptyList(),
                streamingText = "",
                isGenerating = false,
                errorMessage = null,
                latestSpeed = null,
                latestTtft = null,
                latestTokens = null
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

                // Cancel previous and start observing messages for the new conversation
                messagesJob?.cancel()
                messagesJob = manageConversationUseCase.getMessages(convId).onEach { msgs ->
                    _uiState.update { it.copy(messages = msgs) }
                }.launchIn(viewModelScope)
            }

            val userMsg = ChatMessage(
                conversationId = convId,
                role = ChatRole.USER,
                content = trimmed
            )

            // Optimistically append user message so it shows immediately in the UI
            _uiState.update {
                it.copy(
                    messages = it.messages + userMsg,
                    isGenerating = true,
                    streamingText = "",
                    errorMessage = null
                )
            }

            streamChatResponseUseCase.saveMessage(userMsg)

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
        streamJob = null
        streamChatResponseUseCase.stopGeneration()
        _uiState.update { it.copy(isGenerating = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
