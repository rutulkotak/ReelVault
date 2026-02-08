package com.reelvault.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/**
 * Cross-platform app lifecycle observer.
 * Provides a composable state that changes when the app resumes from background.
 */
expect object AppLifecycleObserver {
    /**
     * Returns a State that changes value whenever the app resumes.
     * Use this in LaunchedEffect to trigger actions on app resume.
     */
    @Composable
    fun observeResume(): State<Long>
}