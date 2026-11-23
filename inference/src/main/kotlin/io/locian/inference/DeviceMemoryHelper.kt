package io.locian.inference

import android.app.ActivityManager
import android.content.Context
import io.locian.core.model.DeviceHardwareInfo
import io.locian.core.model.InferenceConfig
import java.io.RandomAccessFile

object DeviceMemoryHelper {

    fun getDeviceHardwareInfo(context: Context): DeviceHardwareInfo {
        val totalRamMb = getTotalRamMb(context)
        val availableRamMb = getAvailableRamMb(context)
        val cores = Runtime.getRuntime().availableProcessors()

        val (recommendedMaxModelSizeMb, recommendedThreads, recommendedContextLength) = when {
            totalRamMb <= 4096 -> Triple(800L, cores.coerceAtMost(2), 2048)
            totalRamMb <= 6144 -> Triple(1600L, cores.coerceAtMost(4), 4096)
            totalRamMb <= 8192 -> Triple(2500L, cores.coerceAtMost(4), 8192)
            else -> Triple(4000L, cores.coerceAtMost(6), 16384)
        }

        return DeviceHardwareInfo(
            totalRamMb = totalRamMb,
            availableRamMb = availableRamMb,
            cpuCores = cores,
            supportsVulkan = true,
            recommendedMaxModelSizeMb = recommendedMaxModelSizeMb,
            recommendedThreads = recommendedThreads,
            recommendedContextLength = recommendedContextLength
        )
    }

    fun getOptimalInferenceConfig(context: Context): InferenceConfig {
        val info = getDeviceHardwareInfo(context)
        return InferenceConfig(
            contextLength = info.recommendedContextLength,
            nThreads = info.recommendedThreads,
            useMmap = true
        )
    }

    private fun getTotalRamMb(context: Context): Long {
        return try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager.getMemoryInfo(memInfo)
            memInfo.totalMem / (1024 * 1024)
        } catch (e: Exception) {
            readTotalRamFromProc()
        }
    }

    private fun getAvailableRamMb(context: Context): Long {
        return try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager.getMemoryInfo(memInfo)
            memInfo.availMem / (1024 * 1024)
        } catch (e: Exception) {
            1024L
        }
    }

    private fun readTotalRamFromProc(): Long {
        return try {
            val reader = RandomAccessFile("/proc/meminfo", "r")
            val load = reader.readLine()
            reader.close()
            val totalKb = load.replace(Regex("\\D+"), "").toLong()
            totalKb / 1024
        } catch (e: Exception) {
            4096L
        }
    }
}
