package io.locian.data.repository

import io.locian.core.model.ChatMessage
import io.locian.core.model.Conversation
import io.locian.data.database.dao.ConversationDao
import io.locian.data.database.dao.MessageDao
import io.locian.data.database.entities.ConversationEntity
import io.locian.data.database.entities.MessageEntity
import io.locian.domain.inference.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ChatRepository {

    override fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getConversation(id: Long): Conversation? {
        return withContext(Dispatchers.IO) {
            conversationDao.getConversationById(id)?.toDomain()
        }
    }

    override suspend fun createConversation(title: String, modelId: String, systemPrompt: String?): Long {
        return withContext(Dispatchers.IO) {
            val entity = ConversationEntity(
                title = title,
                modelId = modelId,
                systemPrompt = systemPrompt,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            conversationDao.insertConversation(entity)
        }
    }

    override suspend fun updateConversationTitle(id: Long, title: String) {
        withContext(Dispatchers.IO) {
            conversationDao.updateTitle(id, title)
        }
    }

    override suspend fun deleteConversation(id: Long) {
        withContext(Dispatchers.IO) {
            conversationDao.deleteConversationById(id)
        }
    }

    override fun getMessages(conversationId: Long): Flow<List<ChatMessage>> {
        return messageDao.getMessagesForConversation(conversationId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveMessage(message: ChatMessage) {
        withContext(Dispatchers.IO) {
            messageDao.insertMessage(MessageEntity.fromDomain(message))
            conversationDao.updateTitle(
                id = message.conversationId,
                title = if (message.role.name == "USER" && message.content.length <= 40) {
                    message.content.take(40)
                } else {
                    conversationDao.getConversationById(message.conversationId)?.title ?: "Conversation"
                },
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    override suspend fun clearMessagesForConversation(conversationId: Long) {
        withContext(Dispatchers.IO) {
            messageDao.deleteMessagesForConversation(conversationId)
        }
    }
}
