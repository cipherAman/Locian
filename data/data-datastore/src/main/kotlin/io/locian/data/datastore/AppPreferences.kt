package io.locian.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.locian.core.model.InferenceConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "locian_preferences")

class AppPreferences(private val context: Context) {

    companion object {
        val KEY_ACTIVE_MODEL_ID = stringPreferencesKey("active_model_id")
        val KEY_ACTIVE_MODEL_PATH = stringPreferencesKey("active_model_path")
        val KEY_ACTIVE_MODEL_NAME = stringPreferencesKey("active_model_name")
        val KEY_TEMPERATURE = floatPreferencesKey("temperature")
        val KEY_TOP_P = floatPreferencesKey("top_p")
        val KEY_MIN_P = floatPreferencesKey("min_p")
        val KEY_REPEAT_PENALTY = floatPreferencesKey("repeat_penalty")
        val KEY_CONTEXT_LENGTH = intPreferencesKey("context_length")
        val KEY_N_THREADS = intPreferencesKey("n_threads")
        val KEY_USE_MMAP = booleanPreferencesKey("use_mmap")
        val KEY_SYSTEM_PROMPT = stringPreferencesKey("system_prompt")
        val KEY_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val KEY_SHOW_THINKING = booleanPreferencesKey("show_thinking")
    }

    val activeModelId: Flow<String?> = context.dataStore.data.map { it[KEY_ACTIVE_MODEL_ID] }
    val activeModelPath: Flow<String?> = context.dataStore.data.map { it[KEY_ACTIVE_MODEL_PATH] }
    val activeModelName: Flow<String?> = context.dataStore.data.map { it[KEY_ACTIVE_MODEL_NAME] }

    val inferenceConfig: Flow<InferenceConfig> = context.dataStore.data.map { prefs ->
        InferenceConfig(
            temperature = prefs[KEY_TEMPERATURE] ?: 0.7f,
            topP = prefs[KEY_TOP_P] ?: 0.9f,
            minP = prefs[KEY_MIN_P] ?: 0.05f,
            repeatPenalty = prefs[KEY_REPEAT_PENALTY] ?: 1.10f,
            contextLength = prefs[KEY_CONTEXT_LENGTH] ?: 2048,
            nThreads = prefs[KEY_N_THREADS] ?: 4,
            useMmap = prefs[KEY_USE_MMAP] ?: true,
            systemPrompt = prefs[KEY_SYSTEM_PROMPT] ?: "You are Locian, a helpful and precise offline AI assistant running locally on the user's Android device."
        )
    }

    val isDarkTheme: Flow<Boolean?> = context.dataStore.data.map { it[KEY_DARK_THEME] }
    val showThinking: Flow<Boolean> = context.dataStore.data.map { it[KEY_SHOW_THINKING] ?: true }

    suspend fun setActiveModel(modelId: String, name: String, localPath: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACTIVE_MODEL_ID] = modelId
            prefs[KEY_ACTIVE_MODEL_NAME] = name
            prefs[KEY_ACTIVE_MODEL_PATH] = localPath
        }
    }

    suspend fun clearActiveModel() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_ACTIVE_MODEL_ID)
            prefs.remove(KEY_ACTIVE_MODEL_NAME)
            prefs.remove(KEY_ACTIVE_MODEL_PATH)
        }
    }

    suspend fun updateInferenceConfig(config: InferenceConfig) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TEMPERATURE] = config.temperature
            prefs[KEY_TOP_P] = config.topP
            prefs[KEY_MIN_P] = config.minP
            prefs[KEY_REPEAT_PENALTY] = config.repeatPenalty
            prefs[KEY_CONTEXT_LENGTH] = config.contextLength
            prefs[KEY_N_THREADS] = config.nThreads
            prefs[KEY_USE_MMAP] = config.useMmap
            prefs[KEY_SYSTEM_PROMPT] = config.systemPrompt
        }
    }

    suspend fun setDarkTheme(isDark: Boolean?) {
        context.dataStore.edit { prefs ->
            if (isDark == null) {
                prefs.remove(KEY_DARK_THEME)
            } else {
                prefs[KEY_DARK_THEME] = isDark
            }
        }
    }

    suspend fun setShowThinking(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SHOW_THINKING] = show
        }
    }
}
