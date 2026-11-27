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

package io.locian.core.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {
    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        val index = digitGroups.coerceIn(0, units.size - 1)
        val value = bytes / Math.pow(1024.0, index.toDouble())
        return String.format(Locale.US, "%.1f %s", value, units[index])
    }

    fun formatSpeed(bytesPerSec: Long): String {
        return "${formatBytes(bytesPerSec)}/s"
    }

    fun formatTokensPerSecond(tokPerSec: Float): String {
        return String.format(Locale.US, "%.1f tok/s", tokPerSec)
    }

    fun formatDurationMs(ms: Long): String {
        if (ms < 1000L) {
            return "${ms}ms"
        }
        val sec = ms / 1000.0
        return if (sec >= 10.0) {
            String.format(Locale.US, "%.1fs", sec)
        } else {
            val formatted = String.format(Locale.US, "%.2fs", sec)
            if (formatted.endsWith(".00s")) {
                formatted.replace(".00s", ".0s")
            } else if (formatted.endsWith("0s")) {
                formatted.replace("0s", "s")
            } else {
                formatted
            }
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
