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

package io.locian.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ModelInfo(
    val id: String,
    val name: String,
    val description: String,
    val repoId: String,
    val filename: String,
    val downloadUrl: String,
    val sizeBytes: Long,
    val quantization: String = "Q4_K_M",
    val parameterCount: String = "1.5B",
    val minRamGb: Int = 4,
    val isDownloaded: Boolean = false,
    val localPath: String? = null,
    val sha256: String? = null,
    val defaultChatTemplate: String? = null
)
