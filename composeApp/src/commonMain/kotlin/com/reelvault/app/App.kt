package com.reelvault.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.reelvault.app.presentation.splash.SplashScreen
import com.reelvault.app.presentation.theme.AuroraTheme

/**
 * Main App Composable
 * Entry point for ReelVault with Aurora Theme and Voyager Navigation.
 */
@Composable
fun App() {
    AuroraTheme {
        Navigator(
            SplashScreen()
        )
    }
}



