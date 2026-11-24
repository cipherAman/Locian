package io.locian.inference

import android.os.Build
import android.util.Log
import io.locian.core.model.InferenceConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.atomic.AtomicBoolean

class InferenceEngine {

    companion object {
        private const val TAG = "InferenceEngine"
        private var isNativeLibraryLoaded = false

        init {
            loadNativeLibraries()
        }

        @JvmStatic
        private external fun getCPUFeatures(): String

        private fun loadNativeLibraries() {
            if (isNativeLibraryLoaded) return
            try {
                System.loadLibrary("locian_cpu_features")
                val cpuFeatures = getCPUFeatures()
                val hasFp16 = cpuFeatures.contains("fp16") || cpuFeatures.contains("fphp")
                val hasDotProd = cpuFeatures.contains("dotprod") || cpuFeatures.contains("asimddp")
                val hasI8mm = cpuFeatures.contains("i8mm")
                val isAtLeastArmV84 = cpuFeatures.contains("dcpop") && cpuFeatures.contains("uscat")
                val isAtLeastArmV82 = cpuFeatures.contains("asimd") && cpuFeatures.contains("crc32") && cpuFeatures.contains("aes")

                val isEmulated = Build.HARDWARE.contains("goldfish") || Build.HARDWARE.contains("ranchu")

                if (!isEmulated && isArm64()) {
                    when {
                        isAtLeastArmV84 && hasI8mm && hasFp16 && hasDotProd -> {
                            loadLibrarySafely("locian_inference_v8_4_fp16_dotprod_i8mm")
                        }
                        isAtLeastArmV84 && hasFp16 && hasDotProd -> {
                            loadLibrarySafely("locian_inference_v8_4_fp16_dotprod")
                        }
                        isAtLeastArmV82 && hasFp16 && hasDotProd -> {
                            loadLibrarySafely("locian_inference_v8_2_fp16_dotprod")
                        }
                        isAtLeastArmV82 && hasFp16 -> {
                            loadLibrarySafely("locian_inference_v8_2_fp16")
                        }
                        else -> {
                            loadLibrarySafely("locian_inference_v8")
                        }
                    }
                } else {
                    loadLibrarySafely("locian_inference")
                }
                isNativeLibraryLoaded = true
            } catch (e: Throwable) {
                Log.w(TAG, "Native library load failed, trying fallback locian_inference", e)
                try {
                    System.loadLibrary("locian_inference")
                    isNativeLibraryLoaded = true
                } catch (fallbackError: Throwable) {
                    Log.e(TAG, "Fatal: unable to load native inference library", fallbackError)
                }
            }
        }

        private fun loadLibrarySafely(libraryName: String) {
            try {
                Log.d(TAG, "Attempting to load: $libraryName")
                System.loadLibrary(libraryName)
                Log.i(TAG, "Successfully loaded native library: $libraryName")
            } catch (t: Throwable) {
                Log.w(TAG, "Failed loading $libraryName, falling back to locian_inference", t)
                System.loadLibrary("locian_inference")
            }
        }

        private fun isArm64(): Boolean {
            return Build.SUPPORTED_64_BIT_ABIS.any { it.equals("arm64-v8a", ignoreCase = true) }
        }
    }

    private var nativeHandle: Long = 0L
    private val isInterrupted = AtomicBoolean(false)
    private val isGenerating = AtomicBoolean(false)

    val isModelLoaded: Boolean
        get() = nativeHandle != 0L

    fun loadModel(modelPath: String, config: InferenceConfig = InferenceConfig()): Boolean {
        val file = File(modelPath)
        if (!file.exists()) {
            throw FileNotFoundException("Model file does not exist at $modelPath")
        }

        if (nativeHandle != 0L) {
            close()
        }

        nativeHandle = loadModel(
            modelPath = modelPath,
            minP = config.minP,
            temperature = config.temperature,
            topP = config.topP,
            repeatPenalty = config.repeatPenalty,
            repeatLastN = 64,
            storeChats = true,
            contextSize = config.contextLength.toLong(),
            chatTemplate = "",
            systemPrompt = config.systemPrompt,
            nThreads = config.nThreads,
            useMmap = config.useMmap,
            useMlock = config.useMlock
        )

        return nativeHandle != 0L
    }

    fun addChatMessage(message: String, role: String) {
        if (nativeHandle != 0L) {
            addChatMessage(nativeHandle, message, role)
        }
    }

    fun clearMessages() {
        if (nativeHandle != 0L) {
            clearMessages(nativeHandle)
        }
    }

    fun generate(prompt: String): Flow<String> = callbackFlow {
        check(nativeHandle != 0L) { "Model is not loaded" }
        check(!isGenerating.getAndSet(true)) { "Generation is already in progress" }
        isInterrupted.set(false)

        startCompletion(nativeHandle, prompt)

        try {
            while (!isInterrupted.get()) {
                val piece = completionLoop(nativeHandle)
                if (piece == null || piece == "[EOG]") {
                    break
                }
                if (piece.isNotEmpty()) {
                    trySend(piece)
                }
            }
        } catch (e: Throwable) {
            close(e)
        } finally {
            isGenerating.set(false)
            channel.close()
        }

        awaitClose {
            stopGeneration()
        }
    }.flowOn(Dispatchers.IO)

    fun stopGeneration() {
        isInterrupted.set(true)
        if (nativeHandle != 0L) {
            stopCompletion(nativeHandle)
        }
        isGenerating.set(false)
    }

    fun getResponseGenerationSpeed(): Float {
        return if (nativeHandle != 0L) getResponseGenerationSpeed(nativeHandle) else 0f
    }

    fun getTimeToFirstToken(): Float {
        return if (nativeHandle != 0L) getTimeToFirstToken(nativeHandle) else 0f
    }

    fun getContextSizeUsed(): Int {
        return if (nativeHandle != 0L) getContextSizeUsed(nativeHandle) else 0
    }

    fun close() {
        if (nativeHandle != 0L) {
            stopGeneration()
            close(nativeHandle)
            nativeHandle = 0L
        }
    }

    // Native methods
    private external fun loadModel(
        modelPath: String,
        minP: Float,
        temperature: Float,
        topP: Float,
        repeatPenalty: Float,
        repeatLastN: Int,
        storeChats: Boolean,
        contextSize: Long,
        chatTemplate: String,
        systemPrompt: String,
        nThreads: Int,
        useMmap: Boolean,
        useMlock: Boolean
    ): Long

    private external fun addChatMessage(modelPtr: Long, message: String, role: String)
    private external fun clearMessages(modelPtr: Long)
    private external fun startCompletion(modelPtr: Long, prompt: String): Boolean
    private external fun completionLoop(modelPtr: Long): String?
    private external fun stopCompletion(modelPtr: Long)
    private external fun getResponseGenerationSpeed(modelPtr: Long): Float
    private external fun getTimeToFirstToken(modelPtr: Long): Float
    private external fun getContextSizeUsed(modelPtr: Long): Int
    private external fun close(modelPtr: Long)
}
