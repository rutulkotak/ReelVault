package com.reelvault.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.navigator.Navigator
import com.reelvault.app.presentation.splash.SplashScreen
import com.reelvault.app.presentation.theme.AuroraTheme
import io.kamel.core.config.KamelConfig
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig

/**
 * Main App Composable
 * Entry point for ReelVault with Aurora Theme and Voyager Navigation.
 */
@Composable
fun App() {
    AuroraTheme {
        CompositionLocalProvider(
            LocalKamelConfig provides KamelConfig.Default
        ) {
            // Your Navigation / Screens go here
            Navigator(
                SplashScreen()
            )
        }
    }
}



