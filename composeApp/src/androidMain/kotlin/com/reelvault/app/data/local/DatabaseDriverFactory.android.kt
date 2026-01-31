package com.reelvault.app.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.reelvault.app.database.ReelVaultDatabase

/**
 * Android implementation of DatabaseDriverFactory.
 */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = ReelVaultDatabase.Schema,
            context = context,
            name = "reelvault.db"
        )
    }
}
