package com.reelvault.app.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initialize Koin for the application.
 * Call this from platform-specific entry points.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        // Core modules
        libraryModule,
        // Add more feature modules here
    )
}

/**
 * Initialize Koin with default configuration.
 * Useful for iOS where no additional configuration is needed.
 */
fun initKoin() = initKoin {}
