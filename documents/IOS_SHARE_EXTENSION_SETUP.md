# iOS Share Extension Setup Guide

This guide explains how to create and configure the iOS Share Extension for ReelVault, enabling users to save reels directly from other apps via the iOS Share Sheet.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                     iOS System                                   │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐    Share Sheet    ┌──────────────────────┐   │
│  │  Other Apps  │ ───────────────► │  ShareExtension      │   │
│  │  (Instagram, │                   │  (Separate Process)  │   │
│  │  TikTok...)  │                   └──────────┬───────────┘   │
│  └──────────────┘                              │               │
│                                                 │               │
│                        NSUserDefaults           │               │
│                     (App Group Storage)         │               │
│                    ┌────────────────────┐       │               │
│                    │ group.com.reelvault│◄──────┘               │
│                    │      .app          │                       │
│                    └─────────┬──────────┘                       │
│                              │                                  │
│                              ▼                                  │
│                    ┌──────────────────────┐                     │
│                    │   ReelVault App      │                     │
│                    │   (Main Process)     │                     │
│                    └──────────────────────┘                     │
└─────────────────────────────────────────────────────────────────┘
```

## Step 1: Create the Share Extension Target in Xcode

1. **Open the Xcode project:**
   ```
   open iosApp/iosApp.xcodeproj
   ```

2. **Add a new target:**
   - Go to `File` → `New` → `Target...`
   - Select `iOS` → `Share Extension`
   - Click `Next`

3. **Configure the extension:**
   - **Product Name:** `ShareExtension`
   - **Team:** Select your development team
   - **Organization Identifier:** `com.reelvault.app`
   - **Bundle Identifier:** `com.reelvault.app.ShareExtension`
   - **Language:** `Swift`
   - Click `Finish`

4. **When prompted to activate the scheme, click "Cancel"** (we'll manage schemes manually)

## Step 2: Configure App Groups

App Groups enable data sharing between the main app and the Share Extension.

### For the Main App Target (iosApp):

1. Select the `iosApp` target in Xcode
2. Go to `Signing & Capabilities` tab
3. Click `+ Capability`
4. Add `App Groups`
5. Click the `+` button and add: `group.com.reelvault.app`

### For the Share Extension Target:

1. Select the `ShareExtension` target
2. Go to `Signing & Capabilities` tab
3. Click `+ Capability`
4. Add `App Groups`
5. Select the same group: `group.com.reelvault.app`

## Step 3: Configure the Share Extension Info.plist

Update `ShareExtension/Info.plist` to accept URLs:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>NSExtension</key>
    <dict>
        <key>NSExtensionAttributes</key>
        <dict>
            <key>NSExtensionActivationRule</key>
            <dict>
                <key>NSExtensionActivationSupportsWebURLWithMaxCount</key>
                <integer>1</integer>
                <key>NSExtensionActivationSupportsText</key>
                <true/>
            </dict>
        </dict>
        <key>NSExtensionMainStoryboard</key>
        <string>MainInterface</string>
        <key>NSExtensionPointIdentifier</key>
        <string>com.apple.share-services</string>
    </dict>
</dict>
</plist>
```

## Step 4: Implement the ShareViewController

Replace the contents of `ShareExtension/ShareViewController.swift`:

```swift
import UIKit
import Social
import UniformTypeIdentifiers

class ShareViewController: SLComposeServiceViewController {
    
    // App Group identifier - must match the KMP SharedDataStorage
    private let appGroupId = "group.com.reelvault.app"
    private let pendingUrlKey = "pending_shared_url"
    
    override func isContentValid() -> Bool {
        // Validate that we have a URL to share
        return extractURL() != nil
    }

    override func didSelectPost() {
        // Save the URL to shared UserDefaults
        if let url = extractURL() {
            saveURLToAppGroup(url)
        }
        
        // Inform the host that we're done
        self.extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
    }

    override func configurationItems() -> [Any]! {
        return []
    }
    
    // MARK: - URL Extraction
    
    private func extractURL() -> String? {
        guard let extensionItems = extensionContext?.inputItems as? [NSExtensionItem] else {
            return nil
        }
        
        for item in extensionItems {
            guard let attachments = item.attachments else { continue }
            
            for provider in attachments {
                // Try URL type first
                if provider.hasItemConformingToTypeIdentifier(UTType.url.identifier) {
                    var extractedURL: String?
                    let semaphore = DispatchSemaphore(value: 0)
                    
                    provider.loadItem(forTypeIdentifier: UTType.url.identifier, options: nil) { item, error in
                        if let url = item as? URL {
                            extractedURL = url.absoluteString
                        }
                        semaphore.signal()
                    }
                    
                    semaphore.wait()
                    if let url = extractedURL {
                        return url
                    }
                }
                
                // Try plain text (may contain URL)
                if provider.hasItemConformingToTypeIdentifier(UTType.plainText.identifier) {
                    var extractedURL: String?
                    let semaphore = DispatchSemaphore(value: 0)
                    
                    provider.loadItem(forTypeIdentifier: UTType.plainText.identifier, options: nil) { item, error in
                        if let text = item as? String {
                            extractedURL = self.extractURLFromText(text)
                        }
                        semaphore.signal()
                    }
                    
                    semaphore.wait()
                    if let url = extractedURL {
                        return url
                    }
                }
            }
        }
        
        return nil
    }
    
    private func extractURLFromText(_ text: String) -> String? {
        // Regular expression to find URLs in text
        let pattern = "(https?://[^\\s]+)"
        guard let regex = try? NSRegularExpression(pattern: pattern, options: .caseInsensitive) else {
            return nil
        }
        
        let range = NSRange(text.startIndex..., in: text)
        if let match = regex.firstMatch(in: text, options: [], range: range) {
            if let urlRange = Range(match.range(at: 1), in: text) {
                var urlString = String(text[urlRange])
                // Clean up trailing punctuation
                while urlString.hasSuffix(".") || urlString.hasSuffix(",") || 
                      urlString.hasSuffix("!") || urlString.hasSuffix("?") {
                    urlString.removeLast()
                }
                return urlString
            }
        }
        
        return nil
    }
    
    // MARK: - App Group Storage
    
    private func saveURLToAppGroup(_ url: String) {
        guard let userDefaults = UserDefaults(suiteName: appGroupId) else {
            print("Error: Could not access App Group UserDefaults")
            return
        }
        
        userDefaults.set(url, forKey: pendingUrlKey)
        userDefaults.synchronize()
        
        print("ShareExtension: Saved URL to App Group: \(url)")
    }
}
```

## Step 5: Update the Main App to Check for Pending URLs

The main ReelVault app needs to check for pending URLs when it becomes active. The KMP `SharedDataStorage` class handles this with the `getPendingUrl()` method.

### In the iOS app entry point (iOSApp.swift), add:

```swift
import SwiftUI

@main
struct iOSApp: App {
    @Environment(\.scenePhase) var scenePhase
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onChange(of: scenePhase) { newPhase in
                    if newPhase == .active {
                        checkForPendingSharedURL()
                    }
                }
        }
    }
    
    private func checkForPendingSharedURL() {
        let appGroupId = "group.com.reelvault.app"
        let pendingUrlKey = "pending_shared_url"
        
        guard let userDefaults = UserDefaults(suiteName: appGroupId),
              let pendingURL = userDefaults.string(forKey: pendingUrlKey) else {
            return
        }
        
        // Clear the pending URL
        userDefaults.removeObject(forKey: pendingUrlKey)
        userDefaults.synchronize()
        
        // Notify the KMP layer about the shared URL
        // This can be done via a shared ViewModel or NotificationCenter
        NotificationCenter.default.post(
            name: Notification.Name("SharedURLReceived"),
            object: nil,
            userInfo: ["url": pendingURL]
        )
    }
}
```

## Step 6: Bridge to KMP (Optional - For Direct Integration)

If you want to call the KMP `SharedDataStorage` directly from Swift:

1. The `SharedDataStorage` class is already implemented in `iosMain` with the `actual` keyword
2. You can access it from Swift like any other Kotlin class exported to iOS

```swift
// In Swift code that has access to the KMP framework
import shared // or your KMP module name

let storage = SharedDataStorage()
if storage.hasPendingUrl() {
    if let url = storage.getPendingUrl() {
        // Handle the URL
    }
}
```

## Testing the Share Extension

1. **Build and run** the main app on a simulator or device
2. **Open Safari** and navigate to an Instagram Reel or TikTok video
3. **Tap the Share button**
4. **Select "ReelVault"** from the share sheet
5. **Tap "Post"** to save
6. **Open ReelVault** - the reel should be saved with metadata

## Troubleshooting

### Share Extension not appearing:
- Verify the extension target is included in the build scheme
- Check that the extension's `Info.plist` has correct activation rules
- Ensure the App Group is configured for both targets

### Data not being shared:
- Verify the App Group identifier matches in both targets
- Check that `synchronize()` is called after writing
- Verify the main app checks for pending URLs on `scenePhase` change

### Extension crashes:
- Check the Console app for crash logs
- Verify all required frameworks are linked
- Ensure the extension has the minimum required entitlements

## File Summary

| File | Location | Purpose |
|------|----------|---------|
| `SharedDataStorage.kt` | `commonMain/.../data/storage/` | Expect class definition |
| `SharedDataStorage.ios.kt` | `iosMain/.../data/storage/` | iOS actual implementation |
| `ShareViewController.swift` | `ShareExtension/` | Share Extension UI and logic |
| `Info.plist` | `ShareExtension/` | Extension configuration |

## Security Considerations

- The `SharedDataStorage` on iOS uses `NSUserDefaults` with App Groups, which is sandboxed per app group
- URLs are cleared immediately after being read by the main app
- No sensitive user data is stored in the shared container
