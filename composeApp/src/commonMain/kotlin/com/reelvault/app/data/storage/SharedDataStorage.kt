package com.reelvault.app.data.storage

/**
 * Cross-process data storage interface for sharing data between
 * the main app and extensions (e.g., iOS Share Extension).
 *
 * This is necessary because iOS Share Extension runs in a different process
 * than the main app, so they cannot share in-memory data directly.
 *
 * Android uses EncryptedSharedPreferences (via actual implementation).
 * iOS uses NSUserDefaults with App Group (via actual implementation).
 */
expect class SharedDataStorage {

    /**
     * Store a pending URL to be saved when the main app opens.
     * Used by the Share Extension to queue URLs for processing.
     */
    fun setPendingUrl(url: String?)

    /**
     * Get and clear the pending URL.
     * Returns null if no URL is pending.
     */
    fun getPendingUrl(): String?

    /**
     * Check if there's a pending URL to process.
     */
    fun hasPendingUrl(): Boolean

    /**
     * Clear any pending URL.
     */
    fun clearPendingUrl()
}

/**
 * App Group identifier for iOS.
 * Must match the App Group configured in Xcode for both the main app and Share Extension.
 */
const val IOS_APP_GROUP = "group.com.reelvault.app"

/**
 * Key used to store the pending URL.
 */
const val PENDING_URL_KEY = "pending_shared_url"
