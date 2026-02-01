package com.reelvault.app.data.notification

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.UserNotifications.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * iOS implementation of NotificationManager.
 */
@OptIn(ExperimentalForeignApi::class)
actual class NotificationManager {

    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    actual fun showDailyNudge(videoCount: Int) {
        val content = UNMutableNotificationContent().apply {
            setTitle("Your ReelVault awaits! 📱")
            setBody("You saved $videoCount videos—ready to learn something new today?")
            setSound(UNNotificationSound.defaultSound())
        }

        // Trigger immediately (for testing, should be scheduled for 24h later)
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 1.0,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "daily_nudge",
            content = content,
            trigger = trigger
        )

        notificationCenter.addNotificationRequest(request) { error ->
            error?.let {
                println("Failed to schedule notification: ${it.localizedDescription}")
            }
        }
    }

    actual suspend fun requestPermissions(): Boolean = suspendCoroutine { continuation ->
        notificationCenter.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, error ->
            if (error != null) {
                println("Notification permission error: ${error.localizedDescription}")
                continuation.resume(false)
            } else {
                continuation.resume(granted)
            }
        }
    }

    actual fun cancelAll() {
        notificationCenter.removeAllPendingNotificationRequests()
        notificationCenter.removeAllDeliveredNotifications()
    }
}
