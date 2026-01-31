package com.reelvault.app.utils

/**
 * Platform-specific utility to open URLs in the system browser or app.
 */
expect object PlatformUrlOpener {
    /**
     * Opens a URL in the system browser or appropriate app.
     * @param url The URL to open
     */
    fun openUrl(url: String)
}
