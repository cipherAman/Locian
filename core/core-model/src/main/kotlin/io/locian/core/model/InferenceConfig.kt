package io.locian.core.model

import kotlinx.serialization.Serializable

@Serializable
data class InferenceConfig(
    val temperature: Float = 0.7f,
    val minP: Float = 0.05f,
    val topP: Float = 0.9f,
    val repeatPenalty: Float = 1.10f,
    val contextLength: Int = 2048,
    val nThreads: Int = 4,
    val useMmap: Boolean = true,
    val useMlock: Boolean = false,
    val nGpuLayers: Int = 0,
    val systemPrompt: String = "You are Locian, a helpful and precise offline AI assistant running locally on the user's Android device. Provide direct, concise, and accurate answers. Keep internal reasoning concise."
)
