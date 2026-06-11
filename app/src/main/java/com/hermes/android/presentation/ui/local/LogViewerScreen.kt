package com.hermes.android.presentation.ui.local

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fontFamily
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.presentation.ui.theme.HermesTheme
import com.hermes.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun LogViewerScreen(
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val logs by remember { MutableStateFlow<List<String>>(emptyList()) }.collectAsStateWithLifecycle()
    val isConnected by remember { MutableStateFlow(false) }.collectAsStateWithLifecycle()
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    val binder by remember { mutableStateOf<android.os.Binder?>(null) }

    val serviceConnection = remember { LogServiceConnection(logs, isConnected, binder) }

    LaunchedEffect(Unit) {
        val intent = android.content.Intent(context, com.hermes.android.data.local.termux.TermuxConnectionService::class.java)
        val bound = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (!bound) {
            // Handle bind failure
        }
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            if (isConnected.value) {
                context.unbindService(serviceConnection)
            }
        }
    }

    HermesTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            TopAppBar(
                title = { Text("Termux Output Log") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { logs.value.clear() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.filled.Delete,
                            contentDescription = "Clear logs"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = HermesTheme.colorScheme.surfaceContainer,
                    titleContentColor = HermesTheme.colorScheme.onSurface
                )
            )

            if (!isConnected) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Service not connected",
                        fontSize = 16.sp,
                        color = HermesTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    reverseLayout = true,
                    autoScroll = true
                ) {
                    items(logs.value.reversed()) { log ->
                        LogEntry(log = log)
                    }
                }
            }
        }
    }
}

@Composable
fun LogEntry(log: String) {
    val isError = log.startsWith("[ERR]")
    val displayLog = if (isError) log.removePrefix("[ERR] ") else log

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isError)
                HermesTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else
                HermesTheme.colorScheme.surfaceContainer
        )
    ) {
        Text(
            text = displayLog,
            modifier = Modifier.padding(12.dp),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = if (isError)
                HermesTheme.colorScheme.error
            else
                HermesTheme.colorScheme.onSurfaceVariant,
            maxLines = Int.MAX_VALUE
        )
    }
}

class LogServiceConnection(
    private val logs: MutableStateFlow<List<String>>,
    private val isConnected: MutableStateFlow<Boolean>,
    private val binder: android.os.Binder?
) : android.content.ServiceConnection {

    override fun onServiceConnected(name: android.content.ComponentName?, service: android.os.IBinder?) {
        if (service is com.hermes.android.data.local.termux.TermuxConnectionService.LocalBinder) {
            val termuxService = service.getService()
            val outputChannel = termuxService.getOutputChannel()
            isConnected.value = true

            CoroutineScope(Dispatchers.IO).launch {
                for (line in outputChannel) {
                    logs.update { current ->
                        (current + line).takeLast(1000) // Keep last 1000 lines
                    }
                }
            }
        }
    }

    override fun onServiceDisconnected(name: android.content.ComponentName?) {
        isConnected.value = false
    }
}