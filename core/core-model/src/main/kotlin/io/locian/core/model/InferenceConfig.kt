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
data class InferenceConfig(
    val temperature: Float = 0.7f,
    val minP: Float = 0.05f,
    val topP: Float = 0.9f,
    val repeatPenalty: Float = 1.10f,
    val contextLength: Int = 2048,
    val nThreads: Int = 4,
    val useMmap: Boolean = true,
    val useMlock: Boolean = false,
    val nGpuLayers: Int = 0,
    val systemPrompt: String = "You are Locian, a helpful and precise offline AI assistant running locally on the user's Android device. Provide direct, concise, and accurate answers. Keep internal reasoning concise."
)
