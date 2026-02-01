package com.reelvault.app.di

import com.reelvault.app.data.local.DatabaseDriverFactory
import com.reelvault.app.data.notification.NotificationManager
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android platform module providing platform-specific dependencies.
 */
actual val platformModule = module {
    // Database driver factory (requires Android Context)
    single { DatabaseDriverFactory(androidContext()) }

    // Ktor HTTP Client Engine for Android
    single<HttpClientEngine> { OkHttp.create() }

    // Notification Manager (requires Android Context)
    single { NotificationManager(androidContext()) }
}
