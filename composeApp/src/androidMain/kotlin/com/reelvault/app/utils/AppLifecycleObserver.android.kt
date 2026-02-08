package com.reelvault.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * Android implementation of AppLifecycleObserver.
 * Exposes MainActivity's resumeTrigger state.
 */
actual object AppLifecycleObserver {
    @Composable
    actual fun observeResume(): State<Long> {
        return com.reelvault.app.MainActivity.resumeTrigger
    }
}