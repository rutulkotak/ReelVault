package com.reelvault.app.data.notification

/**
 * Platform-specific notification manager.
 * Handles showing local push notifications for growth features.
 */
expect class NotificationManager {
    /**
     * Show a daily nudge notification to re-engage the user.
     * @param videoCount The number of videos the user has saved.
     */
    fun showDailyNudge(videoCount: Int)

    /**
     * Request notification permissions if needed (iOS 10+, Android 13+).
     */
    suspend fun requestPermissions(): Boolean

    /**
     * Cancel all pending notifications.
     */
    fun cancelAll()
}
