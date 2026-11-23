package io.locian.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.locian.data.database.entities.DownloadedModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedModelDao {
    @Query("SELECT * FROM downloaded_models ORDER BY downloadedAt DESC")
    fun getAllDownloadedModels(): Flow<List<DownloadedModelEntity>>

    @Query("SELECT * FROM downloaded_models WHERE id = :id")
    suspend fun getDownloadedModelById(id: String): DownloadedModelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedModel(model: DownloadedModelEntity)

    @Query("DELETE FROM downloaded_models WHERE id = :id")
    suspend fun deleteDownloadedModelById(id: String)
}
