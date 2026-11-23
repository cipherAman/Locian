package io.locian.core.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class ChatRole {
    USER,
    ASSISTANT,
    SYSTEM
}

@Serializable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: Long = 0L,
    val role: ChatRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tokensPerSecond: Float? = null,
    val timeToFirstTokenMs: Long? = null,
    val totalTokens: Int? = null,
    val isStreaming: Boolean = false
)
