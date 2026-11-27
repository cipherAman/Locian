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

package io.locian.data.repository

import io.locian.core.common.AppResult
import io.locian.core.model.InferenceConfig
import io.locian.domain.inference.repository.InferenceRepository
import io.locian.inference.InferenceEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InferenceRepositoryImpl @Inject constructor(
    private val inferenceEngine: InferenceEngine
) : InferenceRepository {

    override fun isModelLoaded(): Boolean = inferenceEngine.isModelLoaded

    override suspend fun loadModel(modelPath: String, config: InferenceConfig): AppResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val success = inferenceEngine.loadModel(modelPath, config)
                if (success) {
                    AppResult.Success(true)
                } else {
                    AppResult.Error(IllegalStateException("Failed to load model from $modelPath"))
                }
            } catch (t: Throwable) {
                AppResult.Error(t)
            }
        }
    }

    override fun streamResponse(prompt: String): Flow<String> {
        return inferenceEngine.generate(prompt)
    }

    override fun stopGeneration() {
        inferenceEngine.stopGeneration()
    }

    override fun clearSessionMessages() {
        inferenceEngine.clearMessages()
    }

    override fun getTokensPerSecond(): Float {
        return inferenceEngine.getResponseGenerationSpeed()
    }

    override fun getTimeToFirstTokenMs(): Long {
        return (inferenceEngine.getTimeToFirstToken() * 1000).toLong()
    }

    override fun getContextSizeUsed(): Int {
        return inferenceEngine.getContextSizeUsed()
    }

    override fun unloadModel() {
        inferenceEngine.close()
    }
}
