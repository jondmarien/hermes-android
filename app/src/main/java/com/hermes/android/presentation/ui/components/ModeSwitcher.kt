package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.presentation.ui.theme.HermesTheme

@Composable
fun ModeSwitcher(
    mode: ConnectionMode,
    remoteConfigName: String?,
    onModeChange: (ConnectionMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModeOption(
            mode = ConnectionMode.REMOTE,
            label = "Remote",
            icon = androidx.compose.material.icons.filled.Cloud,
            isSelected = mode == ConnectionMode.REMOTE,
            onClick = { onModeChange(ConnectionMode.REMOTE) }
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
        ModeOption(
            mode = ConnectionMode.LOCAL,
            label = "Local",
            icon = androidx.compose.material.icons.filled.PhoneAndroid,
            isSelected = mode == ConnectionMode.LOCAL,
            onClick = { onModeChange(ConnectionMode.LOCAL) }
        )

        // Show active remote config name when in remote mode
        if (mode == ConnectionMode.REMOTE && remoteConfigName != null) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = remoteConfigName,
                fontSize = 11.sp,
                color = HermesTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ModeOption(
    mode: ConnectionMode,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
            containerColor = if (isSelected)
                HermesTheme.colorScheme.primaryContainer
            else
                HermesTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) HermesTheme.colorScheme.primary else HermesTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) HermesTheme.colorScheme.primary else HermesTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}