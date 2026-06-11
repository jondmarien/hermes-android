package com.hermes.android.presentation.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DerivedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.domain.model.Chat
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.domain.model.TermuxStatus
import com.hermes.android.presentation.ui.local.LogViewerScreen
import com.hermes.android.presentation.ui.local.TermuxStatusWidget
import com.hermes.android.presentation.ui.theme.HermesTheme
import com.hermes.android.R

@Composable
fun ChatScreen(
    sessionId: Long,
    sessionTitle: String,
    mode: ConnectionMode,
    remoteConfigName: String?,
    termuxStatus: TermuxStatus?,
    messages: List<Chat>,
    isStreaming: Boolean,
    onSendMessage: (String) -> Unit,
    onNewSession: () -> Unit,
    onShowSessions: () -> Unit,
    onShowSettings: () -> Unit,
    onShowLogViewer: () -> Unit,
    onStartHermes: () -> Unit,
    onStopHermes: () -> Unit,
    onInstallHermes: () -> Unit,
    onRetryMessage: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scrollToBottom = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val showTermuxWidget by remember { mutableStateOf(mode == ConnectionMode.LOCAL) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        text = sessionTitle,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onShowSessions) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.Menu,
                            contentDescription = "Sessions"
                        )
                    }
                },
                actions = {
                    if (showTermuxWidget && termuxStatus != null) {
                        IconButton(onClick = onShowLogViewer) {
                            Icon(
                                imageVector = androidx.compose.material.icons.filled.Terminal,
                                contentDescription = "Termux Logs",
                                tint = HermesTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onNewSession) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.Add,
                            contentDescription = "New Chat"
                        )
                    }
                    IconButton(onClick = onShowSettings) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = HermesTheme.colorScheme.surfaceContainer,
                    titleContentColor = HermesTheme.colorScheme.onSurface
                )
            )

            // Mode indicator
            ModeIndicator(
                mode = mode,
                remoteConfigName = remoteConfigName,
                isStreaming = isStreaming
            )

            // Termux status widget
            if (showTermuxWidget && termuxStatus != null) {
                TermuxStatusWidget(
                    status = termuxStatus,
                    onStartClick = onStartHermes,
                    onStopClick = onStopHermes,
                    onInstallClick = onInstallHermes,
                    onViewLogsClick = onShowLogViewer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            // Messages list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                state = listState,
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
            ) {
                items(messages.reversed()) { message ->
                    val isLast = messages.lastOrNull()?.id == message.id
                    MessageBubble(
                        message = message,
                        isStreaming = isStreaming && isLast && message.role != com.hermes.android.domain.model.MessageRole.USER,
                        onRetry = { onRetryMessage?.invoke(message.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Input bar
            InputBarWithScroll(
                messageText = "",
                onMessageTextChange = { /* handled by parent */ },
                onSendClick = { /* handled by parent */ },
                isStreaming = isStreaming,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Scroll to bottom FAB
        if (scrollToBottom.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomEnd)
                    .padding(16.dp, 16.dp, 16.dp, 16.dp + 100.dp) // Account for input bar
            ) {
                androidx.compose.material3.FloatingActionButton(
                    onClick = { listState.animateScrollToItem(0) },
                    containerColor = HermesTheme.colorScheme.primaryContainer,
                    contentColor = HermesTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.filled.KeyboardArrowDown,
                        contentDescription = "Scroll to bottom"
                    )
                }
            }
        }
    }
}

@Composable
fun ModeIndicator(
    mode: ConnectionMode,
    remoteConfigName: String?,
    isStreaming: Boolean
) {
    val (text, icon, color) = when (mode) {
        ConnectionMode.LOCAL -> "Local" to androidx.compose.material.icons.filled.PhoneAndroid to HermesTheme.colorScheme.tertiary
        ConnectionMode.REMOTE -> {
            val name = remoteConfigName ?: "Remote"
            "$name" to androidx.compose.material.icons.filled.Cloud to HermesTheme.colorScheme.primary
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 16.dp)
            .background(color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isStreaming) "$text • Streaming…" else text,
                fontSize = 12.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun EmptyChatScreen(
    mode: ConnectionMode,
    onNewSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(HermesTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = if (mode == ConnectionMode.LOCAL)
                    androidx.compose.material.icons.filled.PhoneAndroid
                else
                    androidx.compose.material.icons.filled.Cloud,
                contentDescription = "Hermes",
                tint = HermesTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
            )
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        Text(
            text = "Welcome to Hermes Android",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        Text(
            text = if (mode == ConnectionMode.LOCAL)
                "Start Hermes in Termux to begin chatting locally"
            else
                "Configure a remote server or start a local session",
            fontSize = 16.sp,
            color = HermesTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        androidx.compose.material3.Button(onClick = onNewSession) {
            Text("Start New Chat", fontSize = 16.sp)
        }
    }
}