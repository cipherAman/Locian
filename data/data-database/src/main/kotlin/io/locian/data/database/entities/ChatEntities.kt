package io.locian.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.locian.core.model.ChatMessage
import io.locian.core.model.ChatRole
import io.locian.core.model.Conversation

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val modelId: String,
    val systemPrompt: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Conversation = Conversation(
        id = id,
        title = title,
        modelId = modelId,
        systemPrompt = systemPrompt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(conv: Conversation): ConversationEntity = ConversationEntity(
            id = conv.id,
            title = conv.title,
            modelId = conv.modelId,
            systemPrompt = conv.systemPrompt,
            createdAt = conv.createdAt,
            updatedAt = conv.updatedAt
        )
    }
}

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: Long,
    val role: String,
    val content: String,
    val timestamp: Long,
    val tokensPerSecond: Float?,
    val timeToFirstTokenMs: Long?,
    val totalTokens: Int?
) {
    fun toDomain(): ChatMessage = ChatMessage(
        id = id,
        conversationId = conversationId,
        role = try { ChatRole.valueOf(role) } catch (e: Exception) { ChatRole.USER },
        content = content,
        timestamp = timestamp,
        tokensPerSecond = tokensPerSecond,
        timeToFirstTokenMs = timeToFirstTokenMs,
        totalTokens = totalTokens
    )

    companion object {
        fun fromDomain(msg: ChatMessage): MessageEntity = MessageEntity(
            id = msg.id,
            conversationId = msg.conversationId,
            role = msg.role.name,
            content = msg.content,
            timestamp = msg.timestamp,
            tokensPerSecond = msg.tokensPerSecond,
            timeToFirstTokenMs = msg.timeToFirstTokenMs,
            totalTokens = msg.totalTokens
        )
    }
}
