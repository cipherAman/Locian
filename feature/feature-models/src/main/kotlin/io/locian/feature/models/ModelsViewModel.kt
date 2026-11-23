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
import io.locian.core.model.DownloadStatus
import io.locian.core.model.ModelInfo
import io.locian.data.datastore.AppPreferences
import io.locian.domain.inference.repository.ModelRepository
import io.locian.domain.inference.usecases.DownloadModelUseCase
import io.locian.domain.inference.usecases.LoadModelUseCase
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
    private val downloadModelUseCase: DownloadModelUseCase,
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
    }

    fun downloadModel(model: ModelInfo) {
        val modelsDir = File(context.filesDir, "models")
        if (!modelsDir.exists()) modelsDir.mkdirs()

        downloadModelUseCase(model, modelsDir)
            .onEach { progress ->
                _uiState.update { state ->
                    val map = state.downloadProgressMap.toMutableMap()
                    map[model.id] = progress
                    state.copy(downloadProgressMap = map)
                }
                if (progress.status == DownloadStatus.COMPLETED) {
                    val localFile = File(modelsDir, model.filename)
                    loadModel(model.copy(localPath = localFile.absolutePath, isDownloaded = true))
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadModel(model: ModelInfo) {
        val path = model.localPath ?: return
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
        val path = model.localPath ?: return
        viewModelScope.launch {
            if (_uiState.value.activeModelId == model.id) {
                loadModelUseCase.unloadModel()
                appPreferences.clearActiveModel()
            }
            modelRepository.deleteModel(model.id, path)
            _uiState.update { it.copy(userMessage = "${model.name} deleted") }
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
