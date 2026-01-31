package com.reelvault.app.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.reelvault.app.database.ReelVaultDatabase

/**
 * iOS implementation of DatabaseDriverFactory.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = ReelVaultDatabase.Schema,
            name = "reelvault.db"
        )
    }
}
