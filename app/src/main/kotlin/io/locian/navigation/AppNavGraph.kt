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

package io.locian.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.locian.feature.chat.ChatScreen
import io.locian.feature.chat.ChatViewModel
import io.locian.feature.models.ModelsScreen
import io.locian.feature.models.ModelsViewModel
import io.locian.feature.settings.SettingsScreen
import io.locian.feature.settings.SettingsViewModel

object AppDestinations {
    const val CHAT = "chat"
    const val MODELS = "models"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    onOpenDrawer: () -> Unit,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.CHAT,
        modifier = modifier
    ) {
        composable(AppDestinations.CHAT) {
            ChatScreen(
                viewModel = chatViewModel,
                onOpenDrawer = onOpenDrawer,
                onNavigateToModels = { navController.navigate(AppDestinations.MODELS) }
            )
        }
        composable(AppDestinations.MODELS) {
            val modelsViewModel: ModelsViewModel = hiltViewModel()
            ModelsScreen(
                viewModel = modelsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(AppDestinations.SETTINGS) {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
