package com.reelvault.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Android implementation of PlatformUrlOpener.
 * Opens URLs using Android's Intent system.
 */
actual object PlatformUrlOpener {
    private lateinit var context: Context

    /**
     * Initialize the URL opener with Android context.
     * Should be called from the Application or Activity.
     */
    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    actual fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
