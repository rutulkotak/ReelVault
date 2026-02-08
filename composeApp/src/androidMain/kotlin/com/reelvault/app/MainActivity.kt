package com.reelvault.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.reelvault.app.data.settings.AppSettings
import com.reelvault.app.data.storage.SharedDataStorage
import com.reelvault.app.domain.usecase.CheckDailyNudgeUseCase
import com.reelvault.app.utils.PlatformUrlOpener
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val appSettings: AppSettings by inject()
    private val checkDailyNudgeUseCase: CheckDailyNudgeUseCase by inject()
    private val sharedDataStorage: SharedDataStorage by inject()

    companion object {
        // Global state to trigger URL check when activity resumes
        val resumeTrigger = mutableLongStateOf(0L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize PlatformUrlOpener with application context
        PlatformUrlOpener.init(applicationContext)

        // Track app open for Daily Nudge
        lifecycleScope.launch {
            checkDailyNudgeUseCase()
        }

        // Handle initial intent if app was launched via share
        handleShareIntent(intent)

        setContent {
            App()
        }
    }

    /**
     * Called when the activity receives a new intent while already running.
     * This handles share intents when the app is already open (due to singleTask launchMode).
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    /**
     * Called when activity resumes (comes from background).
     * Triggers LibraryScreen to check for pending shared URLs.
     */
    override fun onResume() {
        super.onResume()
        // Increment trigger to cause LibraryScreen to check for pending URLs
        resumeTrigger.longValue = System.currentTimeMillis()
    }

    /**
     * Extract shared URL from the intent and store it for the LibraryScreen to process.
     * This ensures the correct ViewModel instance handles the URL.
     */
    private fun handleShareIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                // Extract URL from shared text (may contain additional text)
                val url = extractUrl(sharedText)
                if (url != null) {
                    // Store URL in SharedDataStorage for LibraryScreen to process
                    sharedDataStorage.setPendingUrl(url)
                }
            }
        }
    }

    /**
     * Extract URL from shared text which may contain additional content.
     */
    private fun extractUrl(text: String): String? {
        // Common URL patterns for social media
        val urlPattern = """(https?://[^\s]+)""".toRegex()
        return urlPattern.find(text)?.value?.trimEnd('/', '.', ',', '!', '?')
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}