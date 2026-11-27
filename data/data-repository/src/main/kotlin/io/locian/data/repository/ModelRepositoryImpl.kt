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

package io.locian.data.repository

import io.locian.core.common.AppResult
import io.locian.core.model.DownloadProgress
import io.locian.core.model.DownloadStatus
import io.locian.core.model.ModelInfo
import io.locian.data.database.dao.DownloadedModelDao
import io.locian.data.database.entities.DownloadedModelEntity
import io.locian.data.network.ModelCatalog
import io.locian.data.network.ModelDownloader
import io.locian.domain.inference.repository.ModelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelRepositoryImpl @Inject constructor(
    private val downloadedModelDao: DownloadedModelDao,
    private val modelDownloader: ModelDownloader
) : ModelRepository {

    override fun getCuratedModels(): List<ModelInfo> {
        return ModelCatalog.curatedModels
    }

    override fun getDownloadedModels(): Flow<List<ModelInfo>> {
        return downloadedModelDao.getAllDownloadedModels().map { entities ->
            entities.map { entity ->
                val file = File(entity.localPath)
                ModelInfo(
                    id = entity.id,
                    name = entity.name,
                    description = "Downloaded local model",
                    repoId = "",
                    filename = file.name,
                    downloadUrl = "",
                    sizeBytes = entity.sizeBytes,
                    isDownloaded = true,
                    localPath = entity.localPath
                )
            }
        }
    }

    override fun downloadModel(model: ModelInfo, destinationDir: File): Flow<DownloadProgress> {
        val targetFile = File(destinationDir, model.filename)
        return modelDownloader.downloadModel(
            modelId = model.id,
            downloadUrl = model.downloadUrl,
            destinationFile = targetFile,
            expectedSha256 = model.sha256
        ).onEach { progress ->
            if (progress.status == DownloadStatus.COMPLETED) {
                downloadedModelDao.insertDownloadedModel(
                    DownloadedModelEntity(
                        id = model.id,
                        name = model.name,
                        localPath = targetFile.absolutePath,
                        sizeBytes = targetFile.length()
                    )
                )
            }
        }
    }

    override suspend fun deleteModel(modelId: String, localPath: String): Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(localPath)
            if (file.exists()) {
                file.delete()
            }
            downloadedModelDao.deleteDownloadedModelById(modelId)
            true
        }
    }

    override suspend fun registerLocalModel(file: File): AppResult<ModelInfo> {
        return withContext(Dispatchers.IO) {
            if (!file.exists() || !file.name.endsWith(".gguf", ignoreCase = true)) {
                return@withContext AppResult.Error(IllegalArgumentException("File must be a valid .gguf file"))
            }

            val modelId = "local-${file.nameWithoutExtension}"
            val modelName = file.nameWithoutExtension.replace("-", " ").replace("_", " ")

            downloadedModelDao.insertDownloadedModel(
                DownloadedModelEntity(
                    id = modelId,
                    name = modelName,
                    localPath = file.absolutePath,
                    sizeBytes = file.length()
                )
            )

            AppResult.Success(
                ModelInfo(
                    id = modelId,
                    name = modelName,
                    description = "Custom imported GGUF model",
                    repoId = "",
                    filename = file.name,
                    downloadUrl = "",
                    sizeBytes = file.length(),
                    isDownloaded = true,
                    localPath = file.absolutePath
                )
            )
        }
    }
}
