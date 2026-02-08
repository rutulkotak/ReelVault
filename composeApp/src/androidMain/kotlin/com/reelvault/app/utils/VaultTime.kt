package com.reelvault.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual class VaultTime actual constructor() {
    actual fun getCurrentEpochMillis(): Long {
        return System.currentTimeMillis()
    }

    actual fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
