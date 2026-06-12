package com.hermes.android.presentation.ui.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.domain.model.Session
import com.hermes.android.presentation.ui.theme.HermesTheme
import com.hermes.android.R
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@Composable
fun SessionListScreen(
    sessions: List<Session>,
    activeSessionId: Long?,
    onSessionClick: (Session) -> Unit,
    onNewSession: () -> Unit,
    onDeleteSession: (Long) -> Unit,
    onRenameSession: (Long, String) -> Unit,
    onShowSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val renameDialogSessionId by remember { mutableStateOf<Long?>(null) }
    val renameDialogTitle by remember { mutableStateOf("") }
    val showRenameDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TopAppBar(
            title = { Text("Conversations") },
            navigationIcon = {
                IconButton(onClick = onShowSettings) {
                    Icon(
                        imageVector = androidx.compose.material.icons.filled.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            actions = {
                IconButton(onClick = onNewSession) {
                    Icon(
                        imageVector = androidx.compose.material.icons.filled.Add,
                        contentDescription = "New Chat"
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = HermesTheme.colorScheme.surfaceContainer,
                titleContentColor = HermesTheme.colorScheme.onSurface
            )
        )

        if (sessions.isEmpty()) {
            EmptySessionsScreen(onNewSession = onNewSession, modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
            ) {
                items(sessions) { session ->
                    SessionItem(
                        session = session,
                        isActive = session.id == activeSessionId,
                        onClick = { onSessionClick(session) },
                        onDelete = {
                            onDeleteSession(session.id)
                        },
                        onRename = {
                            renameDialogSessionId = session.id
                            renameDialogTitle = session.title
                            showRenameDialog = true
                        }
                    )
                }
            }
        }
    }

    // Rename Dialog
    if (showRenameDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Conversation") },
            text = {
                androidx.compose.material3.TextField(
                    value = renameDialogTitle,
                    onValueChange = { renameDialogTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") }
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    renameDialogSessionId?.let { id ->
                        onRenameSession(id, renameDialogTitle)
                    }
                    showRenameDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SessionItem(
    session: Session,
    isActive: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit,
    modifier: Modifier = Modifier
) {
    val modeIcon = when (session.mode) {
        ConnectionMode.LOCAL ->
            androidx.compose.material.icons.filled.PhoneAndroid
        ConnectionMode.REMOTE ->
            androidx.compose.material.icons.filled.Cloud
    }

    val modeColor = when (session.mode) {
        ConnectionMode.LOCAL ->
            HermesTheme.colorScheme.tertiary
        ConnectionMode.REMOTE ->
            HermesTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (isActive) Modifier.background(HermesTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) else Modifier
            ),
        onClick = onClick,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isActive) HermesTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else HermesTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Active indicator
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(HermesTheme.colorScheme.primary)
                                .padding(end = 12.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = session.title,
                                fontSize = 16.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = modeIcon,
                                contentDescription = session.mode.name,
                                tint = modeColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = formatSessionSubtitle(session),
                            fontSize = 12.sp,
                            color = HermesTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Menu button
                androidx.compose.material3.Menu(
                    expanded = mutableStateOf(false),
                    onDismissRequest = { }
                ) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = {
                            onRename()
                        }
                    )
                    if (!isActive) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Delete", color = HermesTheme.colorScheme.error) },
                            onClick = {
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun formatSessionSubtitle(session: Session): String {
    val dateStr = formatRelativeDate(session.updatedAt)
    val msgCount = session.messageCount
    return "$dateStr • $msgCount message${if (msgCount != 1) "s" else ""}"
}

private fun formatRelativeDate(instant: kotlinx.datetime.Instant): String {
    val zdt = instant.toLocalDateTime(java.util.TimeZone.getDefault().toZoneId())
    val now = kotlinx.datetime.Clock.System.now().toLocalDateTime(java.util.TimeZone.getDefault().toZoneId())
    
    if (zdt.toLocalDate() == now.toLocalDate()) {
        return "Today ${String.format("%02d:%02d", zdt.hour, zdt.minute)}"
    } else if (zdt.toLocalDate() == now.toLocalDate().minusDays(1)) {
        return "Yesterday"
    } else if (zdt.toLocalDate() >= now.toLocalDate().minusDays(7)) {
        return zdt.dayOfWeek.shortName
    } else {
        return String.format("%02d/%02d/%d", zdt.monthNumber, zdt.dayOfMonth, zdt.year)
    }
}

@Composable
fun EmptySessionsScreen(
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
                imageVector = androidx.compose.material.icons.filled.ChatBubbleOutline,
                contentDescription = "Chat",
                tint = HermesTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
            )
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        Text(
            text = "No Conversations Yet",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        Text(
            text = "Start a new chat to begin",
            fontSize = 16.sp,
            color = HermesTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        androidx.compose.material3.Button(onClick = onNewSession) {
            Text("Start New Chat", fontSize = 16.sp)
        }
    }
}