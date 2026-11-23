package io.locian.feature.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.locian.core.model.Conversation
import io.locian.domain.inference.usecases.ManageConversationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationsUiState(
    val conversations: List<Conversation> = emptyList()
)

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val manageConversationUseCase: ManageConversationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationsUiState())
    val uiState: StateFlow<ConversationsUiState> = _uiState.asStateFlow()

    init {
        manageConversationUseCase.getAllConversations().onEach { list ->
            _uiState.update { it.copy(conversations = list) }
        }.launchIn(viewModelScope)
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            manageConversationUseCase.deleteConversation(id)
        }
    }

    fun renameConversation(id: Long, title: String) {
        viewModelScope.launch {
            manageConversationUseCase.updateTitle(id, title)
        }
    }
}
