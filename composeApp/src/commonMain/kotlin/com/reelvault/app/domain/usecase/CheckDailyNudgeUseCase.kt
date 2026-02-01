package com.reelvault.app.domain.usecase

import com.reelvault.app.data.notification.NotificationManager
import com.reelvault.app.data.settings.AppSettings
import com.reelvault.app.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.first

/**
 * Use case for checking and scheduling daily nudge notifications.
 * Part of the growth strategy to re-engage users who haven't opened the app in 24h.
 */
class CheckDailyNudgeUseCase(
    private val appSettings: AppSettings,
    private val notificationManager: NotificationManager,
    private val libraryRepository: LibraryRepository
) {
    /**
     * Check if a daily nudge should be shown and schedule it if needed.
     * This should be called when the app comes to foreground.
     */
    suspend operator fun invoke() {
        // Update last app open time
        appSettings.updateLastAppOpenTime()

        // Check if we should schedule a nudge for tomorrow
        if (appSettings.isDailyNudgeEnabled) {
            // Get video count for the notification message
            val videoCount = libraryRepository.getSavedReels().first().size
            appSettings.totalVideosSaved = videoCount

            // In a real implementation, you'd schedule a notification for 24 hours from now
            // For now, this is a simple check
        }
    }

    /**
     * Show the daily nudge immediately (for testing or background scheduling).
     */
    suspend fun showNudgeNow() {
        if (appSettings.shouldShowDailyNudge()) {
            val videoCount = appSettings.totalVideosSaved
            notificationManager.showDailyNudge(videoCount)
        }
    }

    /**
     * Request notification permissions.
     */
    suspend fun requestPermissions(): Boolean {
        return notificationManager.requestPermissions()
    }
}
