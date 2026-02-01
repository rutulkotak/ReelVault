package com.reelvault.app.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.reelvault.app.presentation.library.LibraryScreen
import com.reelvault.app.presentation.theme.AuroraColors
import kotlinx.coroutines.delay

/**
 * Splash Screen with Aurora-themed 1.5s animation.
 * Shows a beautiful gradient animation before navigating to the main screen.
 */
class SplashScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        var animationStarted by remember { mutableStateOf(false) }

        // Trigger animation on composition
        LaunchedEffect(Unit) {
            animationStarted = true
            delay(1500) // 1.5s Aurora transition
            navigator?.replace(LibraryScreen())
        }

        // Animated values
        val alpha by animateFloatAsState(
            targetValue = if (animationStarted) 1f else 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "alpha"
        )

        val scale by animateFloatAsState(
            targetValue = if (animationStarted) 1f else 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "scale"
        )

        // Infinite gradient shimmer animation
        val infiniteTransition = rememberInfiniteTransition(label = "gradient")
        val shimmerOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shimmer"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AuroraColors.MidnightIndigo,
                            AuroraColors.DeepIndigo,
                            AuroraColors.RichIndigo
                        ),
                        startY = shimmerOffset * 500f,
                        endY = shimmerOffset * 1500f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .alpha(alpha)
                    .scale(scale)
            ) {
                // App Icon/Logo
                Text(
                    text = "📱",
                    fontSize = 72.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // App Name
                Text(
                    text = "ReelVault",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AuroraColors.TextPrimary,
                    fontSize = 36.sp
                )

                // Tagline
                Text(
                    text = "Your personal knowledge vault",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuroraColors.TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Aurora glow effect indicator
                Box(
                    modifier = Modifier
                        .padding(top = 48.dp)
                        .size(40.dp, 4.dp)
                        .alpha(0.6f)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    AuroraColors.BrightIndigo,
                                    AuroraColors.SoftViolet,
                                    AuroraColors.BrightIndigo
                                )
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                )
            }
        }
    }
}
