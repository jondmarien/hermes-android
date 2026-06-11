package com.hermes.android.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusIndicator(
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    size: androidx.compose.ui.unit.Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (isActive) activeColor else inactiveColor,
        animationSpec = tween(300)
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .align(Alignment.CenterVertically)
    ) {
        if (isActive) {
            // Pulsing ring effect
            PulsingRing(color = activeColor, size = size)
        }
    }
}

@Composable
private fun PulsingRing(color: Color, size: androidx.compose.ui.unit.Dp) {
    // Simple implementation - in production use AnimatedVisibility with scale
    Box(
        modifier = Modifier
            .size(size * 2)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f))
            .graphicsLayer {
                // Animation would go here
            }
    )
}

@Composable
fun ConnectionStatusChip(
    isConnected: Boolean,
    text: String? = null,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Chip(
        onClick = null,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = androidx.compose.material3.ChipDefaults.elevatedChipColors(
            containerColor = if (isConnected)
                androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
            else
                androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                imageVector = if (isConnected)
                    androidx.compose.material.icons.filled.CheckCircle
                else
                    androidx.compose.material.icons.filled.ErrorOutline,
                contentDescription = if (isConnected) "Connected" else "Disconnected",
                tint = if (isConnected)
                    androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                else
                    androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text ?: (if (isConnected) "Connected" else "Disconnected"),
                color = if (isConnected)
                    androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                else
                    androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
        }
    }
}