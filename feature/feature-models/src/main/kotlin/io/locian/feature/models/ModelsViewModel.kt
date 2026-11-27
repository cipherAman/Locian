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

package io.locian.feature.models

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.locian.core.common.AppResult
import io.locian.core.model.DeviceHardwareInfo
import io.locian.core.model.DownloadProgress
import io.locian.core.model.ModelInfo
import io.locian.data.datastore.AppPreferences
import io.locian.domain.inference.repository.ModelRepository
import io.locian.domain.inference.usecases.LoadModelUseCase
import io.locian.feature.models.download.ModelDownloadManager
import io.locian.inference.DeviceMemoryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class ModelsUiState(
    val curatedModels: List<ModelInfo> = emptyList(),
    val downloadedModels: List<ModelInfo> = emptyList(),
    val activeModelId: String? = null,
    val activeModelPath: String? = null,
    val downloadProgressMap: Map<String, DownloadProgress> = emptyMap(),
    val isLoadingModel: Boolean = false,
    val hardwareInfo: DeviceHardwareInfo? = null,
    val userMessage: String? = null
)

@HiltViewModel
class ModelsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val modelRepository: ModelRepository,
    private val loadModelUseCase: LoadModelUseCase,
    private val downloadManager: ModelDownloadManager,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModelsUiState())
    val uiState: StateFlow<ModelsUiState> = _uiState.asStateFlow()

    init {
        val hwInfo = DeviceMemoryHelper.getDeviceHardwareInfo(context)
        val curated = modelRepository.getCuratedModels()

        _uiState.update {
            it.copy(
                curatedModels = curated,
                hardwareInfo = hwInfo
            )
        }

        appPreferences.activeModelId.onEach { id ->
            _uiState.update { it.copy(activeModelId = id) }
        }.launchIn(viewModelScope)

        appPreferences.activeModelPath.onEach { path ->
            _uiState.update { it.copy(activeModelPath = path) }
        }.launchIn(viewModelScope)

        modelRepository.getDownloadedModels().onEach { downloaded ->
            _uiState.update { it.copy(downloadedModels = downloaded) }
        }.launchIn(viewModelScope)

        // Observe background downloads managed by Foreground Service
        downloadManager.downloadProgressMap.onEach { progressMap ->
            _uiState.update { it.copy(downloadProgressMap = progressMap) }
        }.launchIn(viewModelScope)
    }

    fun downloadModel(model: ModelInfo) {
        // Critical: Unload any currently loaded model (e.g. 9B holding 4.2GB RAM)
        // to immediately release system memory so Android OS LMK doesn't kill the app in the background.
        if (loadModelUseCase.isModelLoaded()) {
            loadModelUseCase.unloadModel()
            _uiState.update {
                it.copy(userMessage = "Unloaded active model to free RAM for background download.")
            }
        }

        // Delegate to Foreground Service for background execution & persistent notification
        downloadManager.startDownload(model)
    }

    fun cancelDownload(modelId: String) {
        downloadManager.cancelDownload(modelId)
    }

    fun unloadActiveModel() {
        loadModelUseCase.unloadModel()
        viewModelScope.launch {
            appPreferences.clearActiveModel()
            _uiState.update {
                it.copy(
                    activeModelId = null,
                    activeModelPath = null,
                    userMessage = "Model unloaded. Memory released."
                )
            }
        }
    }

    fun loadModel(model: ModelInfo) {
        val path = model.localPath ?: File(context.filesDir, "models/${model.filename}").absolutePath
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingModel = true, userMessage = "Loading ${model.name}...") }
            val config = appPreferences.inferenceConfig.first()

            val result = loadModelUseCase(path, config)
            when (result) {
                is AppResult.Success -> {
                    appPreferences.setActiveModel(model.id, model.name, path)
                    _uiState.update {
                        it.copy(
                            isLoadingModel = false,
                            userMessage = "${model.name} loaded successfully!"
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingModel = false,
                            userMessage = "Failed to load model: ${result.message}"
                        )
                    }
                }
                AppResult.Loading -> {}
            }
        }
    }

    fun deleteModel(model: ModelInfo) {
        val candidatePath = model.localPath ?: File(context.filesDir, "models/${model.filename}").absolutePath
        viewModelScope.launch {
            downloadManager.cancelDownload(model.id)
            if (_uiState.value.activeModelId == model.id) {
                loadModelUseCase.unloadModel()
                appPreferences.clearActiveModel()
            }
            modelRepository.deleteModel(model.id, candidatePath)
            _uiState.update { state ->
                val map = state.downloadProgressMap.toMutableMap()
                map.remove(model.id)
                state.copy(
                    downloadProgressMap = map,
                    userMessage = "${model.name} deleted to free device storage."
                )
            }
        }
    }

    fun importLocalModelUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingModel = true, userMessage = "Importing model file...") }
            withContext(Dispatchers.IO) {
                try {
                    val contentResolver = context.contentResolver
                    val fileName = getFileName(uri) ?: "imported_model.gguf"
                    val modelsDir = File(context.filesDir, "models")
                    if (!modelsDir.exists()) modelsDir.mkdirs()

                    val destFile = File(modelsDir, fileName)
                    contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }

                    val regResult = modelRepository.registerLocalModel(destFile)
                    when (regResult) {
                        is AppResult.Success -> {
                            loadModel(regResult.data)
                        }
                        is AppResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoadingModel = false,
                                    userMessage = "Import failed: ${regResult.message}"
                                )
                            }
                        }
                        AppResult.Loading -> {}
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoadingModel = false,
                            userMessage = "Error importing file: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
