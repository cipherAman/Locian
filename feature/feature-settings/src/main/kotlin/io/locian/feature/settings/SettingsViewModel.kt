package io.locian.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.locian.core.model.InferenceConfig
import io.locian.data.datastore.AppPreferences
import io.locian.inference.DeviceMemoryHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val config: InferenceConfig = InferenceConfig(),
    val isDarkTheme: Boolean? = null,
    val maxCpuCores: Int = 8
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(maxCpuCores = Runtime.getRuntime().availableProcessors())
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        appPreferences.inferenceConfig.onEach { conf ->
            _uiState.update { it.copy(config = conf) }
        }.launchIn(viewModelScope)

        appPreferences.isDarkTheme.onEach { dark ->
            _uiState.update { it.copy(isDarkTheme = dark) }
        }.launchIn(viewModelScope)
    }

    fun updateConfig(config: InferenceConfig) {
        viewModelScope.launch {
            appPreferences.updateInferenceConfig(config)
        }
    }

    fun setDarkTheme(isDark: Boolean?) {
        viewModelScope.launch {
            appPreferences.setDarkTheme(isDark)
        }
    }

    fun resetToHardwareDefaults() {
        viewModelScope.launch {
            val optimal = DeviceMemoryHelper.getOptimalInferenceConfig(context)
            appPreferences.updateInferenceConfig(optimal)
        }
    }
}
