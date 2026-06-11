package com.hermes.android.presentation.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DerivedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBar(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: (() -> Unit)? = null,
    isStreaming: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = remember { androidx.compose.ui.text.input.LocalTextInputService.current }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(HermesTheme.colorScheme.surfaceContainerLowest)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(bottom = 8.dp)
    ) {
        // Attach button
        if (onAttachClick != null) {
            IconButton(onClick = onAttachClick) {
                Icon(
                    imageVector = androidx.compose.material.icons.filled.AttachFile,
                    contentDescription = "Attach",
                    tint = HermesTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Text field
        androidx.compose.material3.TextField(
            value = messageText,
            onValueChange = { onMessageTextChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            singleLine = true,
            maxLines = 5,
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                imeAction = ImeAction.Send,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
                onDone = {
                    if (enabled && messageText.trim().isNotBlank() && !isStreaming) {
                        onSendClick()
                    }
                }
            ),
            enabled = enabled && !isStreaming,
            colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                containerColor = HermesTheme.colorScheme.surfaceContainer,
                focusedContainerColor = HermesTheme.colorScheme.surfaceContainer,
                disabledContainerColor = HermesTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                textColor = HermesTheme.colorScheme.onSurface,
                disabledTextColor = HermesTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                placeholderColor = HermesTheme.colorScheme.onSurfaceVariant,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = HermesTheme.colorScheme.primary
            ),
            placeholder = { Text(text = if (isStreaming) "Streaming…" else "Type a message…") },
            shape = RoundedCornerShape(24.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
        )

        // Send button
        if (isStreaming) {
            // Stop/loading indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(HermesTheme.colorScheme.primaryContainer)
                    .padding(8.dp)
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = HermesTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            IconButton(
                onClick = {
                    if (enabled && messageText.trim().isNotBlank()) {
                        onSendClick()
                    }
                },
                enabled = enabled && messageText.trim().isNotBlank()
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.filled.Send,
                    contentDescription = "Send",
                    tint = HermesTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled && messageText.trim().isNotBlank()) 1f else 0.4f)
                )
            }
        }
    }
}

@Composable
fun InputBarWithScroll(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: (() -> Unit)? = null,
    isStreaming: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Multi-line input that grows
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(HermesTheme.colorScheme.surfaceContainerLowest)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            androidx.compose.material3.TextField(
                value = messageText,
                onValueChange = { onMessageTextChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 12.dp, 16.dp, 0.dp)
                    .minLines(1)
                    .heightIn(min = 56.dp, max = 160.dp),
                singleLine = false,
                maxLines = 5,
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    imeAction = ImeAction.Send,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
                    onDone = {
                        if (enabled && messageText.trim().isNotBlank() && !isStreaming) {
                            onSendClick()
                        }
                    }
                ),
                enabled = enabled && !isStreaming,
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = HermesTheme.colorScheme.surfaceContainer,
                    focusedContainerColor = HermesTheme.colorScheme.surfaceContainer,
                    disabledContainerColor = HermesTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
                    textColor = HermesTheme.colorScheme.onSurface,
                    disabledTextColor = HermesTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    placeholderColor = HermesTheme.colorScheme.onSurfaceVariant,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = HermesTheme.colorScheme.primary
                ),
                placeholder = { Text(text = if (isStreaming) "Streaming…" else "Type a message…") },
                shape = RoundedCornerShape(16.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                visualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
            )

            // Action row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (isStreaming) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(HermesTheme.colorScheme.primaryContainer)
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = HermesTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Streaming…",
                        fontSize = 14.sp,
                        color = HermesTheme.colorScheme.primary
                    )
                } else {
                    if (onAttachClick != null) {
                        IconButton(onClick = onAttachClick) {
                            Icon(
                                imageVector = androidx.compose.material.icons.filled.AttachFile,
                                contentDescription = "Attach",
                                tint = HermesTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                    }
                    IconButton(
                        onClick = {
                            if (enabled && messageText.trim().isNotBlank()) {
                                onSendClick()
                            }
                        },
                        enabled = enabled && messageText.trim().isNotBlank()
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.Send,
                            contentDescription = "Send",
                            tint = HermesTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}