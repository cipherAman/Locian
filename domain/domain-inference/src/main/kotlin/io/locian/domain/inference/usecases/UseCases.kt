package io.locian.domain.inference.usecases

import io.locian.core.common.AppResult
import io.locian.core.model.ChatMessage
import io.locian.core.model.ChatRole
import io.locian.core.model.Conversation
import io.locian.core.model.DownloadProgress
import io.locian.core.model.InferenceConfig
import io.locian.core.model.ModelInfo
import io.locian.domain.inference.repository.ChatRepository
import io.locian.domain.inference.repository.InferenceRepository
import io.locian.domain.inference.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class StreamChatResponseUseCase @Inject constructor(
    private val inferenceRepository: InferenceRepository,
    private val chatRepository: ChatRepository
) {
    operator fun invoke(prompt: String): Flow<String> {
        return inferenceRepository.streamResponse(prompt)
    }

    fun stopGeneration() {
        inferenceRepository.stopGeneration()
    }

    fun clearSession() {
        inferenceRepository.clearSessionMessages()
    }

    suspend fun saveMessage(message: ChatMessage) {
        chatRepository.saveMessage(message)
    }

    fun getTelemetry(): Triple<Float, Long, Int> {
        return Triple(
            inferenceRepository.getTokensPerSecond(),
            inferenceRepository.getTimeToFirstTokenMs(),
            inferenceRepository.getContextSizeUsed()
        )
    }
}

class LoadModelUseCase @Inject constructor(
    private val inferenceRepository: InferenceRepository
) {
    suspend operator fun invoke(modelPath: String, config: InferenceConfig): AppResult<Boolean> {
        return inferenceRepository.loadModel(modelPath, config)
    }

    fun isModelLoaded(): Boolean = inferenceRepository.isModelLoaded()

    fun unloadModel() = inferenceRepository.unloadModel()
}

class DownloadModelUseCase @Inject constructor(
    private val modelRepository: ModelRepository
) {
    operator fun invoke(model: ModelInfo, destinationDir: File): Flow<DownloadProgress> {
        return modelRepository.downloadModel(model, destinationDir)
    }
}

class ManageConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    fun getAllConversations(): Flow<List<Conversation>> = chatRepository.getAllConversations()

    fun getMessages(conversationId: Long): Flow<List<ChatMessage>> = chatRepository.getMessages(conversationId)

    suspend fun getConversation(id: Long): Conversation? = chatRepository.getConversation(id)

    suspend fun createConversation(title: String, modelId: String, systemPrompt: String? = null): Long {
        return chatRepository.createConversation(title, modelId, systemPrompt)
    }

    suspend fun updateTitle(id: Long, title: String) {
        chatRepository.updateConversationTitle(id, title)
    }

    suspend fun deleteConversation(id: Long) {
        chatRepository.deleteConversation(id)
    }
}
