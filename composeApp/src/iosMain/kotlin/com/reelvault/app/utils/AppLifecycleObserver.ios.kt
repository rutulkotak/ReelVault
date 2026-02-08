package com.reelvault.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * iOS implementation of AppLifecycleObserver.
 * For iOS, we rely on the app checking on each navigation or using NotificationCenter.
 */
actual object AppLifecycleObserver {
    @Composable
    actual fun observeResume(): State<Long> {
        // For iOS, return a constant state or implement proper lifecycle observation
        // iOS handles this via scenePhase in SwiftUI or NotificationCenter
        return remember { mutableStateOf(VaultTime().getCurrentEpochMillis()) }
    }
}