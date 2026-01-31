package com.reelvault.app.data.local

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform-specific factory for creating SQLDelight database drivers.
 * Implementations provided in androidMain and iosMain.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
