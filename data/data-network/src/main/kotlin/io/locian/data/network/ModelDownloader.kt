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

package io.locian.data.network

import io.locian.core.model.DownloadProgress
import io.locian.core.model.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class ModelDownloader(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
) {

    fun downloadModel(
        modelId: String,
        downloadUrl: String,
        destinationFile: File,
        expectedSha256: String? = null
    ): Flow<DownloadProgress> = flow {
        var downloadedBytes = 0L
        val tempFile = File(destinationFile.parentFile, "${destinationFile.name}.download")

        if (tempFile.exists()) {
            downloadedBytes = tempFile.length()
        }

        emit(
            DownloadProgress(
                modelId = modelId,
                downloadedBytes = downloadedBytes,
                status = DownloadStatus.DOWNLOADING
            )
        )

        val requestBuilder = Request.Builder().url(downloadUrl)
        if (downloadedBytes > 0) {
            requestBuilder.addHeader("Range", "bytes=$downloadedBytes-")
        }

        val request = requestBuilder.build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful && response.code != 206) {
            emit(
                DownloadProgress(
                    modelId = modelId,
                    status = DownloadStatus.FAILED,
                    errorMessage = "Server returned error HTTP ${response.code}"
                )
            )
            return@flow
        }

        val body = response.body
        if (body == null) {
            emit(
                DownloadProgress(
                    modelId = modelId,
                    status = DownloadStatus.FAILED,
                    errorMessage = "Empty response body"
                )
            )
            return@flow
        }

        val isResume = response.code == 206
        val contentLength = body.contentLength()
        val totalBytes = if (isResume) downloadedBytes + contentLength else contentLength

        val fileStream = RandomAccessFile(tempFile, "rw")
        if (isResume) {
            fileStream.seek(downloadedBytes)
        } else {
            fileStream.setLength(0)
            downloadedBytes = 0L
        }

        val inputStream: InputStream = body.byteStream()
        val buffer = ByteArray(64 * 1024) // 64 KB buffer
        var bytesRead: Int

        var lastEmitTime = System.currentTimeMillis()
        var bytesSinceLastEmit = 0L

        try {
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                if (!coroutineContext.isActive) {
                    emit(
                        DownloadProgress(
                            modelId = modelId,
                            downloadedBytes = downloadedBytes,
                            totalBytes = totalBytes,
                            status = DownloadStatus.PAUSED
                        )
                    )
                    return@flow
                }

                fileStream.write(buffer, 0, bytesRead)
                downloadedBytes += bytesRead
                bytesSinceLastEmit += bytesRead

                val now = System.currentTimeMillis()
                if (now - lastEmitTime >= 300) {
                    val durationSec = (now - lastEmitTime) / 1000.0
                    val speed = if (durationSec > 0) (bytesSinceLastEmit / durationSec).toLong() else 0L
                    val progressPercent = if (totalBytes > 0) ((downloadedBytes * 100) / totalBytes).toInt() else 0

                    emit(
                        DownloadProgress(
                            modelId = modelId,
                            downloadedBytes = downloadedBytes,
                            totalBytes = totalBytes,
                            progressPercentage = progressPercent.coerceIn(0, 100),
                            speedBytesPerSec = speed,
                            status = DownloadStatus.DOWNLOADING
                        )
                    )

                    lastEmitTime = now
                    bytesSinceLastEmit = 0L
                }
            }

            fileStream.close()
            inputStream.close()

            // Verification phase
            if (expectedSha256 != null && expectedSha256.isNotBlank()) {
                emit(
                    DownloadProgress(
                        modelId = modelId,
                        downloadedBytes = downloadedBytes,
                        totalBytes = totalBytes,
                        progressPercentage = 100,
                        status = DownloadStatus.VERIFYING
                    )
                )

                val calculatedHash = calculateSha256(tempFile)
                if (!calculatedHash.equals(expectedSha256, ignoreCase = true)) {
                    tempFile.delete()
                    emit(
                        DownloadProgress(
                            modelId = modelId,
                            status = DownloadStatus.FAILED,
                            errorMessage = "Checksum mismatch: expected $expectedSha256, got $calculatedHash"
                        )
                    )
                    return@flow
                }
            }

            // Rename temp file to final destination
            if (destinationFile.exists()) destinationFile.delete()
            tempFile.renameTo(destinationFile)

            emit(
                DownloadProgress(
                    modelId = modelId,
                    downloadedBytes = totalBytes,
                    totalBytes = totalBytes,
                    progressPercentage = 100,
                    status = DownloadStatus.COMPLETED
                )
            )

        } catch (e: Exception) {
            emit(
                DownloadProgress(
                    modelId = modelId,
                    downloadedBytes = downloadedBytes,
                    totalBytes = totalBytes,
                    status = DownloadStatus.FAILED,
                    errorMessage = e.localizedMessage ?: "Download failed"
                )
            )
        } finally {
            try { fileStream.close() } catch (ignored: Exception) {}
            try { inputStream.close() } catch (ignored: Exception) {}
        }
    }.flowOn(Dispatchers.IO)

    private fun calculateSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
