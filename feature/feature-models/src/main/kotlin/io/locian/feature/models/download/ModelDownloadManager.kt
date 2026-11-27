// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Aman Kumar. All rights reserved.
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

package io.locian.feature.models.download

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.locian.core.model.DownloadProgress
import io.locian.core.model.DownloadStatus
import io.locian.core.model.ModelInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _downloadProgressMap = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    val downloadProgressMap: StateFlow<Map<String, DownloadProgress>> = _downloadProgressMap.asStateFlow()

    fun updateProgress(modelId: String, progress: DownloadProgress) {
        _downloadProgressMap.update { current ->
            val updated = current.toMutableMap()
            updated[modelId] = progress
            updated
        }
    }

    fun startDownload(model: ModelInfo) {
        val intent = Intent(context, ModelDownloadService::class.java).apply {
            action = ModelDownloadService.ACTION_START_DOWNLOAD
            putExtra(ModelDownloadService.EXTRA_MODEL_ID, model.id)
            putExtra(ModelDownloadService.EXTRA_MODEL_NAME, model.name)
            putExtra(ModelDownloadService.EXTRA_DOWNLOAD_URL, model.downloadUrl)
            putExtra(ModelDownloadService.EXTRA_FILENAME, model.filename)
            putExtra(ModelDownloadService.EXTRA_SIZE_BYTES, model.sizeBytes)
            putExtra(ModelDownloadService.EXTRA_SHA256, model.sha256)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
    }

    fun cancelDownload(modelId: String) {
        val intent = Intent(context, ModelDownloadService::class.java).apply {
            action = ModelDownloadService.ACTION_CANCEL_DOWNLOAD
            putExtra(ModelDownloadService.EXTRA_MODEL_ID, modelId)
        }
        context.startService(intent)

        _downloadProgressMap.update { current ->
            val updated = current.toMutableMap()
            updated[modelId] = DownloadProgress(
                modelId = modelId,
                status = DownloadStatus.PAUSED
            )
            updated
        }
    }
}
