package com.hermes.android.presentation.ui

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.presentation.ui.theme.HermesTheme

@Composable
fun SplashScreen(
    onAnimationEnd: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        initialValue = 0.5f,
        animationSpec = tween(800)
    )
    val alpha by animateFloatAsState(
        targetValue = 1f,
        initialValue = 0f,
        animationSpec = tween(800)
    )
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2000, delayMillis = 800)
    )

    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2800)
        onAnimationEnd()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = alpha
                    }
                    .size(120.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(HermesTheme.colorScheme.primary)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.filled.SmartToy,
                    contentDescription = "Hermes",
                    tint = HermesTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(60.dp).align(Alignment.Center)
                )
            }

            Text(
                text = "Hermes Android",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                alpha = alpha
            )

            Text(
                text = "Connecting to Hermes…",
                fontSize = 14.sp,
                color = HermesTheme.colorScheme.onSurfaceVariant,
                alpha = alpha
            )

            androidx.compose.material3.LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
                    .alpha(alpha),
                color = HermesTheme.colorScheme.primary,
                trackColor = HermesTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val currentPage by remember { mutableStateOf(0) }
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to Hermes Android",
            description = "Chat with Hermes Agent locally on your device or connect to a remote server.",
            icon = androidx.compose.material.icons.filled.SmartToy
        ),
        OnboardingPage(
            title = "Local Mode (Termux)",
            description = "Run Hermes directly on your Android device using Termux. No internet required for local inference.",
            icon = androidx.compose.material.icons.filled.PhoneAndroid
        ),
        OnboardingPage(
            title = "Remote Mode",
            description = "Connect to any Hermes Gateway instance via the OpenAI-compatible API. Full tool access, streaming, and sessions.",
            icon = androidx.compose.material.icons.filled.Cloud
        ),
        OnboardingPage(
            title = "Ready to Chat",
            description = "Choose your mode in Settings and start a conversation. Your sessions sync across devices.",
            icon = androidx.compose.material.icons.filled.ChatBubble
        )
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        androidx.compose.foundation.layout.pager.HorizontalPager(
            count = pages.size,
            state = rememberPagerState(initialPage = 0),
            onPageChanged = { currentPage = it }
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        // Page indicators
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            pages.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 24.dp else 8.dp, 8.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                        .background(if (index == currentPage) HermesTheme.colorScheme.primary else HermesTheme.colorScheme.outlineVariant)
                        .padding(horizontal = 4.dp)
                        .animateContentSize()
                )
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 24.dp))

        // Next/Finish button
        androidx.compose.material3.Button(
            onClick = {
                if (currentPage < pages.size - 1) {
                    // This would need a pager state to animate
                } else {
                    onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(text = if (currentPage == pages.size - 1) "Get Started" else "Next", fontSize = 16.sp)
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 24.dp))
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(HermesTheme.colorScheme.primaryContainer)
        ) {
            androidx.compose.material3.Icon(
                imageVector = page.icon,
                contentDescription = "",
                tint = HermesTheme.colorScheme.primary,
                modifier = Modifier.size(50.dp).align(Alignment.Center)
            )
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 32.dp))
        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = HermesTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)