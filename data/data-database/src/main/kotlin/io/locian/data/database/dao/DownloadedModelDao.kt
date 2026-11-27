// SPDX-License-Identifier: Apache-2.0
// Copyright (c) [YEAR] [COPYRIGHT HOLDER]. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
