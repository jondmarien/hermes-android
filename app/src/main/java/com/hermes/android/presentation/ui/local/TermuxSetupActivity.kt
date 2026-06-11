package com.hermes.android.presentation.ui.local

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hermes.android.presentation.ui.theme.HermesTheme
import com.hermes.android.presentation.ui.theme.SystemBarsTheme
import com.hermes.android.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class TermuxSetupActivity : ComponentActivity() {

    private val viewModel: TermuxSetupViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HermesTheme {
                SystemBarsTheme(activity = this)
                TermuxSetupScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TermuxSetupScreen(viewModel: TermuxSetupViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Card(
            modifier = Modifier.padding(16.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            android.util.Log.d("TermuxSetup", "UI State: $uiState")
        }
    }
}

@Composable
fun TermuxSetupContent(
    isTermuxInstalled: Boolean,
    isHermesInstalled: Boolean,
    onInstallTermux: () -> Unit,
    onInstallHermes: () -> Unit,
    onCheckStatus: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Termux Setup Required",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Text(
            text = "Hermes Local Mode runs inside Termux. Please install Termux first, then install Hermes.",
            fontSize = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Status Cards
        Column(modifier = Modifier.fillMaxWidth()) {
            SetupStepCard(
                title = "1. Install Termux",
                description = "Linux environment for Android",
                isComplete = isTermuxInstalled,
                actionText = if (isTermuxInstalled) "Installed ✓" else "Install from F-Droid",
                onActionClick = onInstallTermux,
                enabled = !isTermuxInstalled
            )

            SetupStepCard(
                title = "2. Install Hermes in Termux",
                description = "Python package with gateway & CLI",
                isComplete = isHermesInstalled,
                actionText = if (isHermesInstalled) "Installed ✓" else "Install Hermes",
                onActionClick = onInstallHermes,
                enabled = isTermuxInstalled && !isHermesInstalled
            )

            SetupStepCard(
                title = "3. Verify Installation",
                description = "Check everything works",
                isComplete = false,
                actionText = "Check Status",
                onActionClick = onCheckStatus,
                enabled = isHermesInstalled
            )
        }

        // Continue button
        androidx.compose.material3.Button(
            onClick = onContinue,
            enabled = isHermesInstalled,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Continue to Chat", fontSize = 16.sp)
        }
    }
}

@Composable
fun SetupStepCard(
    title: String,
    description: String,
    isComplete: Boolean,
    actionText: String,
    onActionClick: () -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isComplete)
                androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = description, fontSize = 14.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (enabled) {
                    Button(onClick = onActionClick) {
                        Text(actionText)
                    }
                } else {
                    Text(
                        text = actionText,
                        fontSize = 14.sp,
                        color = if (isComplete) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}