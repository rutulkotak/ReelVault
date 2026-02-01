package com.reelvault.app.data.storage

import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of SharedDataStorage using NSUserDefaults with App Group.
 *
 * This enables data sharing between the main app and the Share Extension,
 * which run in different processes on iOS.
 *
 * IMPORTANT: The App Group must be configured in Xcode for both:
 * - The main ReelVault app target
 * - The ShareExtension target
 *
 * App Group identifier: "group.com.reelvault.app"
 */
actual class SharedDataStorage {

    /**
     * NSUserDefaults instance with the app group suite name.
     * This allows data to be shared across the app and its extensions.
     */
    private val userDefaults: NSUserDefaults = NSUserDefaults(suiteName = IOS_APP_GROUP)!!

    /**
     * Store a pending URL to be processed when the main app opens.
     * Called by the Share Extension after receiving a shared URL.
     */
    actual fun setPendingUrl(url: String?) {
        if (url != null) {
            userDefaults.setObject(url, forKey = PENDING_URL_KEY)
        } else {
            userDefaults.removeObjectForKey(PENDING_URL_KEY)
        }
        userDefaults.synchronize()
    }

    /**
     * Get and clear the pending URL atomically.
     * Called by the main app on launch/resume to check for pending URLs.
     */
    actual fun getPendingUrl(): String? {
        val url = userDefaults.stringForKey(PENDING_URL_KEY)
        if (url != null) {
            clearPendingUrl()
        }
        return url
    }

    /**
     * Check if there's a pending URL to process.
     */
    actual fun hasPendingUrl(): Boolean {
        val url = userDefaults.stringForKey(PENDING_URL_KEY)
        return !url.isNullOrBlank()
    }

    /**
     * Clear the pending URL.
     */
    actual fun clearPendingUrl() {
        userDefaults.removeObjectForKey(PENDING_URL_KEY)
        userDefaults.synchronize()
    }
}
