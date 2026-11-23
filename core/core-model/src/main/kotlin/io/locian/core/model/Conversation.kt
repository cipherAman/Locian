package io.locian.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val id: Long = 0L,
    val title: String,
    val modelId: String = "",
    val systemPrompt: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
