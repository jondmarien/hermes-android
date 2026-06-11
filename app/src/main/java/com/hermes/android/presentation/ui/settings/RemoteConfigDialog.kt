package com.hermes.android.presentation.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.domain.model.RemoteConfig
import com.hermes.android.presentation.ui.theme.HermesTheme

@Composable
fun RemoteConfigDialog(
    config: RemoteConfig? = null,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Boolean) -> Unit
) {
    val name by remember { mutableStateOf(config?.name ?: "") }
    val baseUrl by remember { mutableStateOf(config?.baseUrl ?: "http://") }
    val apiKey by remember { mutableStateOf(config?.apiKey ?: "") }
    val isDefault by remember { mutableStateOf(config?.isDefault ?: false) }
    val nameError by remember { mutableStateOf("") }
    val urlError by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (config == null) "Add Remote Server" else "Edit Remote Server") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    placeholder = { Text("My Hermes Server") },
                    singleLine = true,
                    isError = nameError.isNotBlank(),
                    supportingText = { if (nameError.isNotBlank()) Text(nameError) }
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                TextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Base URL") },
                    placeholder = { Text("https://hermes.example.com/v1") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                        keyboardType = KeyboardType.Url
                    ),
                    isError = urlError.isNotBlank(),
                    supportingText = { if (urlError.isNotBlank()) Text(urlError) }
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                TextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("API Key") },
                    placeholder = { Text("API_SERVER_KEY from Hermes .env") },
                    singleLine = true,
                    visualTransformation = VisualTransformation.Password,
                    isError = false
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Set as Default")
                    androidx.compose.material3.Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
                            checkedThumbColor = HermesTheme.colorScheme.primary,
                            checkedTrackColor = HermesTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var valid = true
                    if (name.trim().isEmpty()) {
                        nameError = "Name is required"
                        valid = false
                    }
                    if (baseUrl.trim().isEmpty() || !baseUrl.startsWith("http")) {
                        urlError = "Valid URL required (http:// or https://)"
                        valid = false
                    }
                    if (valid) {
                        onSave(name.trim(), baseUrl.trim(), apiKey.trim(), isDefault)
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = HermesTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(config != null ? "Save" : "Add")
                }
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}