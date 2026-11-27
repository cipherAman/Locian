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

package io.locian.domain.inference.usecases

import io.locian.core.common.AppResult
import io.locian.core.model.ChatMessage
import io.locian.core.model.ChatRole
import io.locian.core.model.Conversation
import io.locian.core.model.DownloadProgress
import io.locian.core.model.InferenceConfig
import io.locian.core.model.ModelInfo
import io.locian.domain.inference.repository.ChatRepository
import io.locian.domain.inference.repository.InferenceRepository
import io.locian.domain.inference.repository.ModelRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class StreamChatResponseUseCase @Inject constructor(
    private val inferenceRepository: InferenceRepository,
    private val chatRepository: ChatRepository
) {
    operator fun invoke(prompt: String): Flow<String> {
        return inferenceRepository.streamResponse(prompt)
    }

    fun stopGeneration() {
        inferenceRepository.stopGeneration()
    }

    fun clearSession() {
        inferenceRepository.clearSessionMessages()
    }

    suspend fun saveMessage(message: ChatMessage) {
        chatRepository.saveMessage(message)
    }

    fun getTelemetry(): Triple<Float, Long, Int> {
        return Triple(
            inferenceRepository.getTokensPerSecond(),
            inferenceRepository.getTimeToFirstTokenMs(),
            inferenceRepository.getContextSizeUsed()
        )
    }
}

class LoadModelUseCase @Inject constructor(
    private val inferenceRepository: InferenceRepository
) {
    suspend operator fun invoke(modelPath: String, config: InferenceConfig): AppResult<Boolean> {
        return inferenceRepository.loadModel(modelPath, config)
    }

    fun isModelLoaded(): Boolean = inferenceRepository.isModelLoaded()

    fun unloadModel() = inferenceRepository.unloadModel()
}

class DownloadModelUseCase @Inject constructor(
    private val modelRepository: ModelRepository
) {
    operator fun invoke(model: ModelInfo, destinationDir: File): Flow<DownloadProgress> {
        return modelRepository.downloadModel(model, destinationDir)
    }
}

class ManageConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    fun getAllConversations(): Flow<List<Conversation>> = chatRepository.getAllConversations()

    fun getMessages(conversationId: Long): Flow<List<ChatMessage>> = chatRepository.getMessages(conversationId)

    suspend fun getConversation(id: Long): Conversation? = chatRepository.getConversation(id)

    suspend fun createConversation(title: String, modelId: String, systemPrompt: String? = null): Long {
        return chatRepository.createConversation(title, modelId, systemPrompt)
    }

    suspend fun updateTitle(id: Long, title: String) {
        chatRepository.updateConversationTitle(id, title)
    }

    suspend fun deleteConversation(id: Long) {
        chatRepository.deleteConversation(id)
    }
}
