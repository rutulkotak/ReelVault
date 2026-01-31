package com.reelvault.app

import android.app.Application
import com.reelvault.app.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

/**
 * Android Application class for ReelVault.
 * Initializes Koin dependency injection.
 */
class ReelVaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ReelVaultApplication)
        }
    }
}
