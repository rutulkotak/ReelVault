package com.reelvault.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.reelvault.app.data.settings.AppSettings
import com.reelvault.app.domain.usecase.CheckDailyNudgeUseCase
import com.reelvault.app.utils.PlatformUrlOpener
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val appSettings: AppSettings by inject()
    private val checkDailyNudgeUseCase: CheckDailyNudgeUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize PlatformUrlOpener with application context
        PlatformUrlOpener.init(applicationContext)

        // Track app open for Daily Nudge
        lifecycleScope.launch {
            checkDailyNudgeUseCase()
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}