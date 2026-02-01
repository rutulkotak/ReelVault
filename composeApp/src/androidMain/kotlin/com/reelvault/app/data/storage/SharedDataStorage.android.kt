package com.reelvault.app.data.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Android implementation of SharedDataStorage using SharedPreferences.
 * Provides storage for pending URLs from the Share Sheet.
 */
actual class SharedDataStorage(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "reelvault_shared_data",
        Context.MODE_PRIVATE
    )

    /**
     * Store a pending URL to be processed by the app.
     */
    actual fun setPendingUrl(url: String?) {
        sharedPreferences.edit()
            .putString(PENDING_URL_KEY, url)
            .apply()
    }

    /**
     * Get and clear the pending URL atomically.
     */
    actual fun getPendingUrl(): String? {
        val url = sharedPreferences.getString(PENDING_URL_KEY, null)
        if (url != null) {
            clearPendingUrl()
        }
        return url
    }

    /**
     * Check if there's a pending URL.
     */
    actual fun hasPendingUrl(): Boolean {
        return sharedPreferences.contains(PENDING_URL_KEY) &&
               !sharedPreferences.getString(PENDING_URL_KEY, null).isNullOrBlank()
    }

    /**
     * Clear the pending URL.
     */
    actual fun clearPendingUrl() {
        sharedPreferences.edit()
            .remove(PENDING_URL_KEY)
            .apply()
    }
}
