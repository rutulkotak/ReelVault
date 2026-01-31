package com.reelvault.app.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * iOS implementation of PlatformUrlOpener.
 * Opens URLs using UIKit's UIApplication.
 */
actual object PlatformUrlOpener {
    actual fun openUrl(url: String) {
        val nsUrl = NSURL(string = url)
        if (UIApplication.sharedApplication.canOpenURL(nsUrl)) {
            UIApplication.sharedApplication.openURL(nsUrl)
        }
    }
}
