package com.reelvault.app.data.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.datetime.Clock

/**
 * App-wide settings manager using multiplatform-settings.
 * Tracks user behavior for growth features like Daily Nudge.
 */
class AppSettings(private val settings: Settings) {

    companion object {
        private const val KEY_LAST_APP_OPEN = "last_app_open"
        private const val KEY_DAILY_NUDGE_ENABLED = "daily_nudge_enabled"
        private const val KEY_TOTAL_VIDEOS_SAVED = "total_videos_saved"
        private const val KEY_HERITAGE_VAULT_ENABLED = "heritage_vault_enabled"
    }

    /**
     * Last time the app was opened (timestamp in milliseconds).
     */
    var lastAppOpenTime: Long
        get() = settings[KEY_LAST_APP_OPEN, 0L]
        set(value) {
            settings[KEY_LAST_APP_OPEN] = value
        }

    /**
     * Whether daily nudge notifications are enabled.
     */
    var isDailyNudgeEnabled: Boolean
        get() = settings[KEY_DAILY_NUDGE_ENABLED, true]
        set(value) {
            settings[KEY_DAILY_NUDGE_ENABLED] = value
        }

    /**
     * Total number of videos saved by the user.
     */
    var totalVideosSaved: Int
        get() = settings[KEY_TOTAL_VIDEOS_SAVED, 0]
        set(value) {
            settings[KEY_TOTAL_VIDEOS_SAVED] = value
        }

    /**
     * Whether Heritage Vault (Digital Inheritance) is enabled.
     */
    var isHeritageVaultEnabled: Boolean
        get() = settings[KEY_HERITAGE_VAULT_ENABLED, false]
        set(value) {
            settings[KEY_HERITAGE_VAULT_ENABLED] = value
        }

    /**
     * Update the last app open time to now.
     */
    fun updateLastAppOpenTime() {
        lastAppOpenTime = Clock.System.now().epochSeconds * 1000L
    }

    /**
     * Check if the user hasn't opened the app in 24 hours.
     */
    fun shouldShowDailyNudge(): Boolean {
        if (!isDailyNudgeEnabled) return false

        val nowMillis = Clock.System.now().epochSeconds * 1000L
        val millisSinceLastOpen = nowMillis - lastAppOpenTime
        val hoursSinceLastOpen = millisSinceLastOpen / (1000 * 60 * 60)

        return hoursSinceLastOpen >= 24
    }

    /**
     * Increment the total videos saved count.
     */
    fun incrementVideosSaved() {
        totalVideosSaved += 1
    }
}