package com.reelvault.app.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.timeIntervalSince1970

actual class VaultTime actual constructor() {
    actual fun getCurrentEpochMillis(): Long {
        return (NSDate().timeIntervalSince1970 * 1000).toLong()
    }

    actual fun getFormattedDate(): String {
        val formatter = NSDateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        return formatter.stringFromDate(NSDate())
    }
}
