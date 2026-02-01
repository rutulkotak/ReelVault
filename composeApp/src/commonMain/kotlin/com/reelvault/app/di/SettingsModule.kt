package com.reelvault.app.di

import com.reelvault.app.data.notification.NotificationManager
import com.reelvault.app.data.settings.AppSettings
import com.reelvault.app.domain.usecase.CheckDailyNudgeUseCase
import com.reelvault.app.presentation.settings.SettingsViewModel
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for Settings and Growth features.
 * Provides AppSettings, NotificationManager, and related ViewModels.
 */
val settingsModule = module {
    // Multiplatform Settings
    single { Settings() }

    // App Settings Manager
    single { AppSettings(get()) }

    // Notification Manager (platform-specific, provided in platformModule)
    // single { NotificationManager() } // Provided by platform module

    // Use Cases
    factoryOf(::CheckDailyNudgeUseCase)

    // ViewModel
    viewModelOf(::SettingsViewModel)
}
