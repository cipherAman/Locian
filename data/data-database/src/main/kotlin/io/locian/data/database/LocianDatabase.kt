package io.locian.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.locian.data.database.dao.ConversationDao
import io.locian.data.database.dao.DownloadedModelDao
import io.locian.data.database.dao.MessageDao
import io.locian.data.database.entities.ConversationEntity
import io.locian.data.database.entities.DownloadedModelEntity
import io.locian.data.database.entities.MessageEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        DownloadedModelEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LocianDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun downloadedModelDao(): DownloadedModelDao
}
