package com.hermes.android.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.presentation.ui.chat.ChatScreen
import com.hermes.android.presentation.ui.chat.EmptyChatScreen
import com.hermes.android.presentation.ui.local.LogViewerScreen
import com.hermes.android.presentation.ui.local.TermuxSetupActivity
import com.hermes.android.presentation.ui.sessions.SessionListScreen
import com.hermes.android.presentation.ui.settings.RemoteConfigDialog
import com.hermes.android.presentation.ui.settings.SettingsScreen
import com.hermes.android.presentation.viewmodel.MainViewModel
import com.hermes.android.presentation.viewmodel.MainViewModel.Destination
import javax.inject.Inject

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val activeSessionId by viewModel.activeSessionId.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val connectionMode by viewModel.connectionMode.collectAsStateWithLifecycle()
    val remoteConfigs by viewModel.remoteConfigs.collectAsStateWithLifecycle()
    val activeRemoteConfig by viewModel.activeRemoteConfig.collectAsStateWithLifecycle()
    val termuxStatus by viewModel.termuxStatus.collectAsStateWithLifecycle()
    val isStreaming by viewModel.isStreaming.collectAsStateWithLifecycle()
    val showRemoteConfigDialog by viewModel.showRemoteConfigDialog.collectAsStateWithLifecycle()
    val currentSession by viewModel.currentSession.collectAsStateWithLifecycle()

    NavHost(navController, startDestination = Destination.CHAT.route) {
        composable(Destination.CHAT.route) {
            if (activeSessionId != null) {
                currentSession?.let { session ->
                    ChatScreen(
                        sessionId = session.id,
                        sessionTitle = session.title,
                        mode = session.mode,
                        remoteConfigName = activeRemoteConfig?.name,
                        termuxStatus = termuxStatus,
                        messages = messages,
                        isStreaming = isStreaming,
                        onSendMessage = { content ->
                            viewModel.sendMessage(content)
                        },
                        onNewSession = { viewModel.createNewSession() },
                        onShowSessions = { navController.navigate(Destination.SESSIONS.route) },
                        onShowSettings = { navController.navigate(Destination.SETTINGS.route) },
                        onShowLogViewer = { navController.navigate(Destination.LOG_VIEWER.route) },
                        onStartHermes = { viewModel.startHermes() },
                        onStopHermes = { viewModel.stopHermes() },
                        onInstallHermes = { viewModel.installHermes() },
                        onRetryMessage = { messageId ->
                            // Handle retry - would need to get message content
                        }
                    )
                }
            } else {
                EmptyChatScreen(
                    mode = connectionMode,
                    onNewSession = { viewModel.createNewSession() }
                )
            }
        }

        composable(Destination.SESSIONS.route) {
            SessionListScreen(
                sessions = sessions,
                activeSessionId = activeSessionId,
                onSessionClick = { session ->
                    viewModel.onSessionSelected(session.id)
                    navController.navigateUp()
                },
                onNewSession = { viewModel.createNewSession() },
                onDeleteSession = { sessionId -> viewModel.deleteSession(sessionId) },
                onRenameSession = { sessionId, title -> viewModel.renameSession(sessionId, title) },
                onShowSettings = { navController.navigate(Destination.SETTINGS.route) }
            )
        }

        composable(Destination.SETTINGS.route) {
            SettingsScreen(
                connectionMode = connectionMode,
                onModeChange = { mode -> viewModel.setConnectionMode(mode) },
                autoStartLocal = false, // Would come from preferences flow
                onAutoStartChange = { enabled -> viewModel.setAutoStartLocal(enabled) },
                termuxPath = "", // Would come from preferences flow
                onTermuxPathChange = { path -> viewModel.setTermuxPath(path) },
                remoteConfigs = remoteConfigs,
                activeRemoteConfig = activeRemoteConfig,
                onAddRemoteConfig = { viewModel.showRemoteConfigDialog() },
                onEditRemoteConfig = { config -> viewModel.showRemoteConfigDialog(config) },
                onDeleteRemoteConfig = { configId -> /* handle delete */ },
                onSetDefaultRemoteConfig = { config -> /* handle set default */ },
                onTestConnection = { config -> viewModel.testRemoteConnection(config) },
                notificationsEnabled = true,
                onNotificationsChange = { _ -> },
                theme = "SYSTEM",
                onThemeChange = { _ -> },
                onShowAbout = { /* show about dialog */ }
            )
        }

        composable(Destination.TERMUX_SETUP.route) {
            androidx.compose.material3.Surface(
                modifier = androidx.compose.ui.Modifier.fillMaxSize()
            ) {
                // TermuxSetupScreen would go here
            }
        }

        composable(Destination.LOG_VIEWER.route) {
            LogViewerScreen(onClose = { viewModel.goBack() })
        }

        // Dialog destinations handled via state
    }

    // Show dialogs based on state
    if (showRemoteConfigDialog != null) {
        RemoteConfigDialog(
            config = showRemoteConfigDialog,
            onDismiss = { viewModel.dismissRemoteConfigDialog() },
            onSave = { name, url, apiKey, isDefault ->
                // Save remote config
                viewModel.dismissRemoteConfigDialog()
            }
        )
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<MainViewModel>()
    AppNavHost(navController, viewModel)
}