package io.locian.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ModelInfo(
    val id: String,
    val name: String,
    val description: String,
    val repoId: String,
    val filename: String,
    val downloadUrl: String,
    val sizeBytes: Long,
    val quantization: String = "Q4_K_M",
    val parameterCount: String = "1.5B",
    val minRamGb: Int = 4,
    val isDownloaded: Boolean = false,
    val localPath: String? = null,
    val sha256: String? = null,
    val defaultChatTemplate: String? = null
)
