package io.locian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.locian.core.ui.theme.LocianTheme
import io.locian.data.datastore.AppPreferences
import io.locian.feature.chat.ChatViewModel
import io.locian.feature.conversations.ConversationsDrawerContent
import io.locian.feature.conversations.ConversationsViewModel
import io.locian.navigation.AppDestinations
import io.locian.navigation.AppNavGraph
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = androidx.activity.SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        setContent {
            val isDarkThemePref by appPreferences.isDarkTheme.collectAsStateWithLifecycle(initialValue = null)
            val useDark = isDarkThemePref ?: isSystemInDarkTheme()

            LocianTheme(darkTheme = useDark) {
                LocianAppRoot()
            }
        }
    }
}

@Composable
fun LocianAppRoot() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val chatViewModel: ChatViewModel = hiltViewModel()
    val conversationsViewModel: ConversationsViewModel = hiltViewModel()
    val chatUiState by chatViewModel.uiState.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ConversationsDrawerContent(
                viewModel = conversationsViewModel,
                activeConversationId = chatUiState.conversationId,
                onSelectConversation = { convId ->
                    chatViewModel.setConversation(convId)
                    coroutineScope.launch { drawerState.close() }
                },
                onNewChat = {
                    chatViewModel.startNewConversation()
                    coroutineScope.launch { drawerState.close() }
                },
                onDeleteConversation = { convId ->
                    if (chatUiState.conversationId == convId) {
                        chatViewModel.startNewConversation()
                    }
                },
                onNavigateToModels = {
                    navController.navigate(AppDestinations.MODELS)
                    coroutineScope.launch { drawerState.close() }
                },
                onNavigateToSettings = {
                    navController.navigate(AppDestinations.SETTINGS)
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        AppNavGraph(
            navController = navController,
            onOpenDrawer = {
                coroutineScope.launch { drawerState.open() }
            },
            chatViewModel = chatViewModel
        )
    }
}
