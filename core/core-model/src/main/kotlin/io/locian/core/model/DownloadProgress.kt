package io.locian.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class DownloadStatus {
    IDLE,
    DOWNLOADING,
    PAUSED,
    VERIFYING,
    COMPLETED,
    FAILED,
    CANCELLED
}

@Serializable
data class DownloadProgress(
    val modelId: String,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val progressPercentage: Int = 0,
    val speedBytesPerSec: Long = 0L,
    val status: DownloadStatus = DownloadStatus.IDLE,
    val errorMessage: String? = null
)
