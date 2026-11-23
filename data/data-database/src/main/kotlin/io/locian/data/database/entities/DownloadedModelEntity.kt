package io.locian.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_models")
data class DownloadedModelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val localPath: String,
    val sizeBytes: Long,
    val downloadedAt: Long = System.currentTimeMillis()
)
