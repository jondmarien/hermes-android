package com.hermes.android.presentation.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
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
import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.presentation.ui.theme.HermesTheme
import com.hermes.android.R
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(
    connectionMode: ConnectionMode,
    onModeChange: (ConnectionMode) -> Unit,
    autoStartLocal: Boolean,
    onAutoStartChange: (Boolean) -> Unit,
    termuxPath: String,
    onTermuxPathChange: (String) -> Unit,
    remoteConfigs: List<RemoteConfig>,
    activeRemoteConfig: RemoteConfig?,
    onAddRemoteConfig: () -> Unit,
    onEditRemoteConfig: (RemoteConfig) -> Unit,
    onDeleteRemoteConfig: (Long) -> Unit,
    onSetDefaultRemoteConfig: (RemoteConfig) -> Unit,
    onTestConnection: (RemoteConfig) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    theme: String,
    onThemeChange: (String) -> Unit,
    onShowAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                androidx.compose.material3.IconButton(onClick = { /* handled by nav */ }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.filled.Close,
                        contentDescription = "Close"
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = HermesTheme.colorScheme.surfaceContainer,
                titleContentColor = HermesTheme.colorScheme.onSurface
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            // Connection Mode Section
            SettingsSection(title = "Connection Mode") {
                ModeSelectorCard(
                    currentMode = connectionMode,
                    onModeChange = onModeChange
                )
            }

            // Remote Config Section (only show in Remote mode)
            if (connectionMode == ConnectionMode.REMOTE || remoteConfigs.isNotEmpty()) {
                SettingsSection(title = "Remote Servers") {
                    if (remoteConfigs.isEmpty()) {
                        EmptyRemoteConfigCard(onAddClick = onAddRemoteConfig)
                    } else {
                        remoteConfigs.forEach { config ->
                            RemoteConfigCard(
                                config = config,
                                isActive = activeRemoteConfig?.id == config.id,
                                onClick = { onEditRemoteConfig(config) },
                                onSetDefault = { onSetDefaultRemoteConfig(config) },
                                onDelete = { onDeleteRemoteConfig(config.id) },
                                onTest = { onTestConnection(config) }
                            )
                        }
                    }
                }
            }

            // Local Mode Section (only show in Local mode or if Termux is relevant)
            if (connectionMode == ConnectionMode.LOCAL) {
                SettingsSection(title = "Local Execution (Termux)") {
                    TermuxSettingsCard(
                        autoStart = autoStartLocal,
                        onAutoStartChange = onAutoStartChange,
                        termuxPath = termuxPath,
                        onTermuxPathChange = onTermuxPathChange
                    )
                }
            }

            // Notifications
            SettingsSection(title = "Notifications") {
                SettingsToggleRow(
                    title = "Enable Notifications",
                    subtitle = "Receive notifications for background tasks",
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsChange
                )
            }

            // Appearance
            SettingsSection(title = "Appearance") {
                SettingsSelectRow(
                    title = "Theme",
                    subtitle = "Choose app theme",
                    value = theme,
                    options = listOf("SYSTEM" to "System", "LIGHT" to "Light", "DARK" to "Dark"),
                    onValueChange = onThemeChange
                )
            }

            // About
            SettingsSection(title = "About") {
                SettingsActionRow(
                    title = "About Hermes Android",
                    subtitle = "Version 1.0.0",
                    onClick = onShowAbout
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = HermesTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = HermesTheme.colorScheme.surfaceContainerLow
            )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ModeSelectorCard(
    currentMode: ConnectionMode,
    onModeChange: (ConnectionMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ModeOption(
            mode = ConnectionMode.REMOTE,
            title = "Remote",
            subtitle = "Connect to Hermes Gateway API",
            icon = androidx.compose.material.icons.filled.Cloud,
            isSelected = currentMode == ConnectionMode.REMOTE,
            onClick = { onModeChange(ConnectionMode.REMOTE) }
        )
        ModeOption(
            mode = ConnectionMode.LOCAL,
            title = "Local",
            subtitle = "Run Hermes in Termux on device",
            icon = androidx.compose.material.icons.filled.PhoneAndroid,
            isSelected = currentMode == ConnectionMode.LOCAL,
            onClick = { onModeChange(ConnectionMode.LOCAL) }
        )
    }
}

@Composable
fun ModeOption(
    mode: ConnectionMode,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(12.dp),
        onClick = onClick,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected)
                HermesTheme.colorScheme.primaryContainer
            else
                HermesTheme.colorScheme.surfaceContainer,
            contentColor = if (isSelected)
                HermesTheme.colorScheme.onPrimaryContainer
            else
                HermesTheme.colorScheme.onSurface
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) HermesTheme.colorScheme.primary else HermesTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, fontSize = 12.sp, color = HermesTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun EmptyRemoteConfigCard(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = HermesTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.filled.CloudOff,
                contentDescription = "No servers",
                tint = HermesTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            Text(text = "No Remote Servers", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = "Add a Hermes Gateway server to connect", fontSize = 14.sp, color = HermesTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
            Button(onClick = onAddClick) {
                Text("Add Server", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RemoteConfigCard(
    config: RemoteConfig,
    isActive: Boolean,
    onClick: () -> Unit,
    onSetDefault: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isActive) HermesTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else HermesTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(HermesTheme.colorScheme.primary)
                                .padding(end = 12.dp)
                        )
                    }
                    if (config.isDefault) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.Star,
                            contentDescription = "Default",
                            tint = HermesTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp).padding(end = 8.dp)
                        )
                    }
                    Column {
                        Text(text = config.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(text = config.baseUrl, fontSize = 12.sp, color = HermesTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                // Use a simple row with buttons instead of Menu for compatibility
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.material3.OutlinedButton(onClick = onTest) {
                        Text("Test")
                    }
                    androidx.compose.material3.TextButton(onClick = onSetDefault) {
                        Text("Set Default")
                    }
                    androidx.compose.material3.TextButton(onClick = onDelete) {
                        Text("Delete", color = HermesTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun TermuxSettingsCard(
    autoStart: Boolean,
    onAutoStartChange: (Boolean) -> Unit,
    termuxPath: String,
    onTermuxPathChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingsToggleRow(
            title = "Auto-start Local",
            subtitle = "Automatically start Hermes on app launch",
            checked = autoStart,
            onAutoStartChange = onAutoStartChange
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        SettingsTextRow(
            title = "Termux Path",
            subtitle = "Path to Termux bash executable",
            value = termuxPath,
            onValueChange = onTermuxPathChange
        )
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 16.sp)
            Text(text = subtitle, fontSize = 12.sp, color = HermesTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = androidx.compose.material3.SwitchDefaults.colors(
                checkedThumbColor = HermesTheme.colorScheme.primary,
                checkedTrackColor = HermesTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
fun SettingsSelectRow(
    title: String,
    subtitle: String,
    value: String,
    options: List<Pair<String, String>>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 16.sp)
            Text(text = subtitle, fontSize = 12.sp, color = HermesTheme.colorScheme.onSurfaceVariant)
        }
        androidx.compose.material3.Menu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (val, label) ->
                androidx.compose.material3.DropdownMenuItem(
                    text = label,
                    onClick = { onValueChange(val); expanded = false }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = label)
                        if (val == value) {
                            Icon(
                                imageVector = androidx.compose.material.icons.filled.Check,
                                contentDescription = "Selected",
                                tint = HermesTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SettingsTextRow(
    title: String,
    subtitle: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 16.sp)
            Text(text = subtitle, fontSize = 12.sp, color = HermesTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = value, fontSize = 14.sp, color = HermesTheme.colorScheme.onSurfaceVariant, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
    }
}

@Composable
fun SettingsActionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(HermesTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 16.sp)
            Text(text = subtitle, fontSize = 12.sp, color = HermesTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = androidx.compose.material.icons.filled.ChevronRight,
            contentDescription = "",
            tint = HermesTheme.colorScheme.onSurfaceVariant
        )
    }
}