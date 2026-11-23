package io.locian.domain.inference.repository

import io.locian.core.common.AppResult
import io.locian.core.model.ChatMessage
import io.locian.core.model.Conversation
import io.locian.core.model.DownloadProgress
import io.locian.core.model.InferenceConfig
import io.locian.core.model.ModelInfo
import kotlinx.coroutines.flow.Flow
import java.io.File

interface InferenceRepository {
    fun isModelLoaded(): Boolean
    suspend fun loadModel(modelPath: String, config: InferenceConfig): AppResult<Boolean>
    fun streamResponse(prompt: String): Flow<String>
    fun stopGeneration()
    fun clearSessionMessages()
    fun getTokensPerSecond(): Float
    fun getTimeToFirstTokenMs(): Long
    fun getContextSizeUsed(): Int
    fun unloadModel()
}

interface ChatRepository {
    fun getAllConversations(): Flow<List<Conversation>>
    suspend fun getConversation(id: Long): Conversation?
    suspend fun createConversation(title: String, modelId: String, systemPrompt: String? = null): Long
    suspend fun updateConversationTitle(id: Long, title: String)
    suspend fun deleteConversation(id: Long)
    fun getMessages(conversationId: Long): Flow<List<ChatMessage>>
    suspend fun saveMessage(message: ChatMessage)
    suspend fun clearMessagesForConversation(conversationId: Long)
}

interface ModelRepository {
    fun getCuratedModels(): List<ModelInfo>
    fun getDownloadedModels(): Flow<List<ModelInfo>>
    fun downloadModel(model: ModelInfo, destinationDir: File): Flow<DownloadProgress>
    suspend fun deleteModel(modelId: String, localPath: String): Boolean
    suspend fun registerLocalModel(file: File): AppResult<ModelInfo>
}
