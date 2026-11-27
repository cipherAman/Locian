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

package io.locian.feature.models.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import dagger.hilt.android.AndroidEntryPoint
import io.locian.core.model.DownloadProgress
import io.locian.core.model.DownloadStatus
import io.locian.core.model.ModelInfo
import io.locian.domain.inference.usecases.DownloadModelUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AndroidEntryPoint
class ModelDownloadService : Service() {

    companion object {
        const val CHANNEL_ID = "locian_model_downloads"
        const val CHANNEL_NAME = "Model Downloads"
        const val NOTIFICATION_ID = 9001

        const val ACTION_START_DOWNLOAD = "io.locian.ACTION_START_DOWNLOAD"
        const val ACTION_CANCEL_DOWNLOAD = "io.locian.ACTION_CANCEL_DOWNLOAD"

        const val EXTRA_MODEL_ID = "extra_model_id"
        const val EXTRA_MODEL_NAME = "extra_model_name"
        const val EXTRA_DOWNLOAD_URL = "extra_download_url"
        const val EXTRA_FILENAME = "extra_filename"
        const val EXTRA_SIZE_BYTES = "extra_size_bytes"
        const val EXTRA_SHA256 = "extra_sha256"
    }

    @Inject
    lateinit var downloadModelUseCase: DownloadModelUseCase

    @Inject
    lateinit var downloadManager: ModelDownloadManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val activeDownloadJobs = ConcurrentHashMap<String, Job>()

    private var wakeLock: PowerManager.WakeLock? = null
    private var wifiLock: WifiManager.WifiLock? = null
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        acquireLocks()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                val modelId = intent.getStringExtra(EXTRA_MODEL_ID) ?: return START_NOT_STICKY
                val modelName = intent.getStringExtra(EXTRA_MODEL_NAME) ?: "Model"
                val downloadUrl = intent.getStringExtra(EXTRA_DOWNLOAD_URL) ?: return START_NOT_STICKY
                val filename = intent.getStringExtra(EXTRA_FILENAME) ?: return START_NOT_STICKY
                val sizeBytes = intent.getLongExtra(EXTRA_SIZE_BYTES, 0L)
                val sha256 = intent.getStringExtra(EXTRA_SHA256)

                val model = ModelInfo(
                    id = modelId,
                    name = modelName,
                    description = "",
                    repoId = "",
                    filename = filename,
                    downloadUrl = downloadUrl,
                    sizeBytes = sizeBytes,
                    sha256 = sha256
                )
                startModelDownload(model)
            }
            ACTION_CANCEL_DOWNLOAD -> {
                val modelId = intent.getStringExtra(EXTRA_MODEL_ID)
                if (modelId != null) {
                    cancelModelDownload(modelId)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun startModelDownload(model: ModelInfo) {
        if (activeDownloadJobs.containsKey(model.id)) return

        // Start foreground immediately with placeholder notification
        val notification = createNotification(
            title = "Downloading ${model.name}",
            content = "Connecting to server...",
            progress = 0,
            indeterminate = true,
            modelId = model.id
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        val modelsDir = File(filesDir, "models")
        if (!modelsDir.exists()) modelsDir.mkdirs()

        val job = serviceScope.launch {
            downloadModelUseCase(model, modelsDir)
                .catch { error ->
                    val failedProgress = DownloadProgress(
                        modelId = model.id,
                        status = DownloadStatus.FAILED,
                        errorMessage = error.localizedMessage ?: "Download failed"
                    )
                    downloadManager.updateProgress(model.id, failedProgress)
                    showCompletionNotification(
                        title = "${model.name} download failed",
                        content = error.localizedMessage ?: "Unknown network error",
                        isSuccess = false
                    )
                    checkStopSelf(model.id)
                }
                .collect { progress ->
                    downloadManager.updateProgress(model.id, progress)

                    when (progress.status) {
                        DownloadStatus.DOWNLOADING -> {
                            val downloadedMb = progress.downloadedBytes / (1024 * 1024)
                            val totalMb = progress.totalBytes / (1024 * 1024)
                            val speedMb = progress.speedBytesPerSec / (1024f * 1024f)
                            val speedStr = String.format("%.1f MB/s", speedMb)

                            val contentText = if (totalMb > 0) {
                                "$downloadedMb MB / $totalMb MB ($speedStr)"
                            } else {
                                "$downloadedMb MB downloaded ($speedStr)"
                            }

                            val updatedNotif = createNotification(
                                title = "Downloading ${model.name}",
                                content = contentText,
                                progress = progress.progressPercentage,
                                indeterminate = progress.totalBytes <= 0,
                                modelId = model.id
                            )
                            notificationManager.notify(NOTIFICATION_ID, updatedNotif)
                        }
                        DownloadStatus.VERIFYING -> {
                            val verifyingNotif = createNotification(
                                title = "Verifying ${model.name}",
                                content = "Checking SHA-256 integrity...",
                                progress = 100,
                                indeterminate = true,
                                modelId = model.id
                            )
                            notificationManager.notify(NOTIFICATION_ID, verifyingNotif)
                        }
                        DownloadStatus.COMPLETED -> {
                            showCompletionNotification(
                                title = "${model.name} downloaded",
                                content = "Ready to load and chat offline.",
                                isSuccess = true
                            )
                            checkStopSelf(model.id)
                        }
                        DownloadStatus.FAILED -> {
                            showCompletionNotification(
                                title = "${model.name} download failed",
                                content = progress.errorMessage ?: "Download failed",
                                isSuccess = false
                            )
                            checkStopSelf(model.id)
                        }
                        DownloadStatus.PAUSED -> {
                            checkStopSelf(model.id)
                        }
                        else -> {}
                    }
                }
        }

        activeDownloadJobs[model.id] = job
    }

    private fun cancelModelDownload(modelId: String) {
        val job = activeDownloadJobs.remove(modelId)
        job?.cancel()
        downloadManager.updateProgress(
            modelId,
            DownloadProgress(modelId = modelId, status = DownloadStatus.PAUSED)
        )
        if (activeDownloadJobs.isEmpty()) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun checkStopSelf(completedModelId: String) {
        activeDownloadJobs.remove(completedModelId)
        if (activeDownloadJobs.isEmpty()) {
            stopForeground(STOP_FOREGROUND_DETACH)
            stopSelf()
        }
    }

    private fun createNotification(
        title: String,
        content: String,
        progress: Int,
        indeterminate: Boolean,
        modelId: String
    ) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(android.R.drawable.stat_sys_download)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setProgress(100, progress, indeterminate)
        .addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Cancel",
            createCancelPendingIntent(modelId)
        )
        .build()

    private fun showCompletionNotification(title: String, content: String, isSuccess: Boolean) {
        val icon = if (isSuccess) android.R.drawable.stat_sys_download_done else android.R.drawable.stat_notify_error
        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(icon)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notif)
    }

    private fun createCancelPendingIntent(modelId: String): PendingIntent {
        val intent = Intent(this, ModelDownloadService::class.java).apply {
            action = ACTION_CANCEL_DOWNLOAD
            putExtra(EXTRA_MODEL_ID, modelId)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getService(this, modelId.hashCode(), intent, flags)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of local LLM model downloads"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Suppress("DEPRECATION")
    private fun acquireLocks() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "Locian::ModelDownloadWakeLock"
            ).apply {
                setReferenceCounted(false)
                acquire(2 * 60 * 60 * 1000L) // 2 hours timeout
            }

            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiLock = wifiManager.createWifiLock(
                WifiManager.WIFI_MODE_FULL_HIGH_PERF,
                "Locian::ModelDownloadWifiLock"
            ).apply {
                setReferenceCounted(false)
                acquire()
            }
        } catch (e: Exception) {
            android.util.Log.w("ModelDownloadService", "Could not acquire wake/wifi locks", e)
        }
    }

    private fun releaseLocks() {
        try {
            wakeLock?.let { if (it.isHeld) it.release() }
            wifiLock?.let { if (it.isHeld) it.release() }
        } catch (ignored: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        releaseLocks()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
