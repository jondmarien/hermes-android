package com.hermes.android.presentation.ui.local

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.domain.model.TermuxStatus
import com.hermes.android.presentation.ui.components.StatusIndicator
import com.hermes.android.R

@Composable
fun TermuxStatusWidget(
    status: TermuxStatus,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onInstallClick: () -> Unit,
    onViewLogsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!status.isInstalled) {
        TermuxNotInstalledCard(onInstallClick = onInstallClick, modifier = modifier)
    } else {
        TermuxInstalledCard(
            status = status,
            onStartClick = onStartClick,
            onStopClick = onStopClick,
            onViewLogsClick = onViewLogsClick,
            modifier = modifier
        )
    }
}

@Composable
fun TermuxNotInstalledCard(
    onInstallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.filled.Terminal,
                    contentDescription = "Termux",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Termux Not Installed",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Install Termux from F-Droid to run Hermes locally",
                        fontSize = 14.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            androidx.compose.material3.Button(
                onClick = onInstallClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open F-Droid to Install Termux")
            }
        }
    }
}

@Composable
fun TermuxInstalledCard(
    status: TermuxStatus,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onViewLogsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (status.isRunning)
                androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusIndicator(
                        isActive = status.isRunning,
                        activeColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        inactiveColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (status.isRunning) "Hermes Running" else "Hermes Stopped",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (status.hermesInstalled) "Hermes installed in Termux" else "Hermes not installed",
                            fontSize = 12.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (status.error != null) {
                    Row(modifier = Modifier.padding(end = 8.dp)) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.ErrorOutline,
                            contentDescription = "Error",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Error message
            if (status.error != null) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                Text(
                    text = status.error!!,
                    fontSize = 12.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    maxLines = 2
                )
            }

            // Action buttons
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!status.isRunning) {
                    if (!status.hermesInstalled) {
                        androidx.compose.material3.Button(
                            onClick = onInstallClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Install Hermes")
                        }
                    }
                    androidx.compose.material3.Button(
                        onClick = onStartClick,
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Start Hermes")
                    }
                } else {
                    androidx.compose.material3.OutlinedButton(
                        onClick = onViewLogsClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View Logs")
                    }
                    androidx.compose.material3.Button(
                        onClick = onStopClick,
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer,
                            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Stop Hermes")
                    }
                }
            }
        }
    }
}