package com.hermes.android.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.domain.model.Chat
import com.hermes.android.domain.model.MessageRole
import com.hermes.android.presentation.ui.theme.HermesTheme
import com.hermes.android.R
import kotlinx.coroutines.flow.collectAsStateWithLifecycle
import com.hermes.android.presentation.ui.components.SyntaxHighlighter

@Composable
fun MessageBubble(
    message: Chat,
    isStreaming: Boolean = false,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER
    val isSystem = message.role == MessageRole.SYSTEM || message.role == MessageRole.TOOL
    val isError = message.error != null

    val bubbleColor = when {
        isUser -> HermesTheme.colorScheme.primaryContainer
        isSystem -> HermesTheme.colorScheme.tertiaryContainer
        isError -> HermesTheme.colorScheme.errorContainer
        else -> HermesTheme.colorScheme.surfaceContainerHighest
    }

    val textColor = when {
        isUser -> HermesTheme.colorScheme.onPrimaryContainer
        isSystem -> HermesTheme.colorScheme.onTertiaryContainer
        isError -> HermesTheme.colorScheme.onErrorContainer
        else -> HermesTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            // Assistant avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(HermesTheme.colorScheme.primary)
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.filled.SmartToy,
                    contentDescription = "Hermes",
                    tint = HermesTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center)
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(bottom = 4.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = bubbleColor.copy(alpha = if (isStreaming) 0.9f else 1f)
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Content
                    if (message.role == MessageRole.USER) {
                        Text(
                            text = message.content,
                            color = textColor,
                            fontSize = 15.sp,
                            maxLines = Int.MAX_VALUE
                        )
                    } else {
                        SyntaxHighlighter(
                            text = message.content,
                            textColor = textColor,
                            fontSize = 15.sp
                        )
                    }

                    // Error indicator
                    if (isError) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.filled.ErrorOutline,
                                contentDescription = "Error",
                                tint = HermesTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = message.error ?: "Unknown error",
                                color = HermesTheme.colorScheme.error,
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (onRetry != null) {
                                androidx.compose.material3.TextButton(onClick = onRetry) {
                                    Text("Retry", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Streaming indicator
                    if (isStreaming && !isError) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
                        StreamingIndicator()
                    }

                    // Timestamp
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
                    Text(
                        text = formatTimestamp(message.createdAt),
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        textAlign = if (isUser) TextAlign.End else TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (isUser) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            // User avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(HermesTheme.colorScheme.secondaryContainer)
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.filled.Person,
                    contentDescription = "You",
                    tint = HermesTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun StreamingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(HermesTheme.colorScheme.primary.copy(alpha = 0.5f))
                    .graphicsLayer {
                        // Simple animation - in production use animateFloatAsState
                        alpha = 0.3f + (0.7f * (1 + kotlin.math.sin((System.currentTimeMillis() / 150 + index * 200) * kotlin.math.PI / 180)) / 2)
                    }
            )
        }
        Text(
            text = "Streaming…",
            fontSize = 11.sp,
            color = HermesTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
    }
}

private fun formatTimestamp(instant: kotlinx.datetime.Instant): String {
    val zdt = instant.toLocalDateTime(java.util.TimeZone.getDefault().toZoneId())
    val now = kotlinx.datetime.Clock.System.now().toLocalDateTime(java.util.TimeZone.getDefault().toZoneId())
    
    if (zdt.toLocalDate() == now.toLocalDate()) {
        return String.format("%02d:%02d", zdt.hour, zdt.minute)
    } else if (zdt.toLocalDate() == now.toLocalDate().minusDays(1)) {
        return "Yesterday ${String.format("%02d:%02d", zdt.hour, zdt.minute)}"
    } else {
        return String.format("%02d/%02d %02d:%02d", zdt.monthNumber, zdt.dayOfMonth, zdt.hour, zdt.minute)
    }
}