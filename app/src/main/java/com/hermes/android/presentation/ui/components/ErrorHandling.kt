package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.presentation.ui.theme.HermesTheme

@Composable
fun ErrorDisplay(
    error: String,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = HermesTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = androidx.compose.material.icons.filled.ErrorOutline,
                        contentDescription = "Error",
                        tint = HermesTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp).padding(end = 12.dp)
                    )
                    Text(
                        text = "Error",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = HermesTheme.colorScheme.onErrorContainer
                    )
                }
                onDismiss?.let {
                    androidx.compose.material3.IconButton(onClick = it) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.Close,
                            contentDescription = "Dismiss",
                            tint = HermesTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(
                text = error,
                fontSize = 14.sp,
                color = HermesTheme.colorScheme.onErrorContainer,
                modifier = Modifier.fillMaxWidth()
            )
            if (onRetry != null) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                    if (onDismiss != null) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.material3.TextButton(onClick = onDismiss) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
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
                .background(HermesTheme.colorScheme.surfaceContainerHighest)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = HermesTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp).align(Alignment.Center)
            )
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = HermesTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        actionText?.let { text ->
            onAction?.let { action ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
                Button(onClick = action) {
                    Text(text)
                }
            }
        }
    }
}

@Composable
fun LoadingState(
    message: String = "Loading…",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            color = HermesTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            color = HermesTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RetryableContent(
    state: ResultState,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    when (state) {
        is ResultState.Loading -> LoadingState()
        is ResultState.Error -> ErrorDisplay(
            error = state.error,
            onRetry = onRetry
        )
        is ResultState.Empty -> EmptyState(
            icon = androidx.compose.material.icons.filled.Inbox,
            title = "No Data",
            subtitle = state.message,
        )
        is ResultState.Success -> content()
    }
}

sealed class ResultState {
    data class Success : ResultState()
    data class Error(val error: String) : ResultState()
    object Loading : ResultState()
    data class Empty(val message: String) : ResultState()
}