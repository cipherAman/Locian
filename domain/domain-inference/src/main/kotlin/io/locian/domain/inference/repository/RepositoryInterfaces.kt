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

package io.locian.domain.inference.repository

import io.locian.core.common.AppResult
import io.locian.core.model.ChatMessage
import io.locian.core.model.Conversation
import io.locian.core.model.DownloadProgress
import io.locian.core.model.InferenceConfig
import io.locian.core.model.ModelInfo
import kotlinx.coroutines.flow.Flow
import java.io.File

interface InferenceRepository {
    fun isModelLoaded(): Boolean
    suspend fun loadModel(modelPath: String, config: InferenceConfig): AppResult<Boolean>
    fun streamResponse(prompt: String): Flow<String>
    fun stopGeneration()
    fun clearSessionMessages()
    fun getTokensPerSecond(): Float
    fun getTimeToFirstTokenMs(): Long
    fun getContextSizeUsed(): Int
    fun unloadModel()
}

interface ChatRepository {
    fun getAllConversations(): Flow<List<Conversation>>
    suspend fun getConversation(id: Long): Conversation?
    suspend fun createConversation(title: String, modelId: String, systemPrompt: String? = null): Long
    suspend fun updateConversationTitle(id: Long, title: String)
    suspend fun deleteConversation(id: Long)
    fun getMessages(conversationId: Long): Flow<List<ChatMessage>>
    suspend fun saveMessage(message: ChatMessage)
    suspend fun clearMessagesForConversation(conversationId: Long)
}

interface ModelRepository {
    fun getCuratedModels(): List<ModelInfo>
    fun getDownloadedModels(): Flow<List<ModelInfo>>
    fun downloadModel(model: ModelInfo, destinationDir: File): Flow<DownloadProgress>
    suspend fun deleteModel(modelId: String, localPath: String): Boolean
    suspend fun registerLocalModel(file: File): AppResult<ModelInfo>
}
