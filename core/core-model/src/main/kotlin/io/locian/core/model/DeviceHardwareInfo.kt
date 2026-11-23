package io.locian.core.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceHardwareInfo(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val cpuCores: Int,
    val supportsVulkan: Boolean,
    val recommendedMaxModelSizeMb: Long,
    val recommendedThreads: Int,
    val recommendedContextLength: Int
)
