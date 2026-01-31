package com.reelvault.app.di

import com.reelvault.app.data.local.DatabaseDriverFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

/**
 * iOS platform module providing platform-specific dependencies.
 */
actual val platformModule = module {
    // Database driver factory (no context needed for iOS)
    single { DatabaseDriverFactory() }

    // Ktor HTTP Client Engine for iOS
    single<HttpClientEngine> { Darwin.create() }
}
