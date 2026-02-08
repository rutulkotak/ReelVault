package com.reelvault.app.domain.usecase

import com.reelvault.app.data.remote.MetadataScraper
import com.reelvault.app.domain.featuregate.FeatureGate
import com.reelvault.app.domain.model.Reel
import com.reelvault.app.domain.repository.LibraryRepository
import com.reelvault.app.utils.VaultTime

/**
 * Use case for saving a reel from a shared URL.
 * Handles metadata scraping and reel persistence.
 *
 * This is the primary entry point for the Share Sheet flow.
 * Enforces reel limits defined by the user's tier.
 */
class SaveReelFromUrlUseCase(
    private val libraryRepository: LibraryRepository,
    private val metadataScraper: MetadataScraper,
    private val featureGate: FeatureGate
) {
    /**
     * Result of saving a reel from URL.
     */
    sealed class SaveResult {
        data class Success(val reel: Reel) : SaveResult()
        data object AlreadyExists : SaveResult()
        data class LimitReached(val maxReels: Int) : SaveResult()
        data class Error(val message: String) : SaveResult()
    }

    /**
     * Invoke the use case to save a reel from a URL.
     *
     * @param url The shared URL to save.
     * @return SaveResult indicating the outcome.
     */
    suspend operator fun invoke(url: String): SaveResult {
        return try {
            // Validate URL format
            val rawUrl = url.trim()
            if (!isValidUrl(rawUrl)) {
                return SaveResult.Error("Invalid URL format")
            }

            // Normalize URL to handle duplicates with different query params (e.g., YouTube 'si')
            val normalizedUrl = normalizeUrl(rawUrl)

            // Check if reel already exists using normalized URL
            if (libraryRepository.isReelSaved(normalizedUrl)) {
                return SaveResult.AlreadyExists
            }

            // Check limits before proceeding with scraping
            val currentCount = libraryRepository.getReelCount()
            if (!featureGate.canSaveReel(currentCount)) {
                return SaveResult.LimitReached(featureGate.maxReelSaves ?: 0)
            }

            // Scrape metadata from normalized URL
            val metadata = metadataScraper.scrapeMetadata(normalizedUrl)

            // Create reel with scraped metadata or defaults
            val reel = Reel(
                id = generateUuid(),
                url = normalizedUrl,
                title = metadata?.title ?: extractTitleFromUrl(normalizedUrl),
                thumbnail = metadata?.thumbnail ?: "",
                tags = extractTagsFromUrl(normalizedUrl),
                createdAt = VaultTime().getCurrentEpochMillis()
            )

            // Save to repository
            libraryRepository.saveReel(reel)

            SaveResult.Success(reel)
        } catch (e: Exception) {
            SaveResult.Error(e.message ?: "Failed to save reel")
        }
    }

    /**
     * Normalizes social media URLs by stripping tracking parameters.
     * e.g., strips 'si' from YouTube and 'igsh' from Instagram.
     */
    private fun normalizeUrl(url: String): String {
        return try {
            var normalized = url.trim()
            
            // Common tracking parameters used by social platforms
            val trackingParams = listOf("si=", "igsh=", "utm_", "fbclid=", "s=")
            
            if (normalized.contains("?") && trackingParams.any { normalized.contains(it) }) {
                // For most social platforms, the content ID is in the path, 
                // and query params are for tracking/referrals.
                if (normalized.contains("youtube.com/shorts/") || 
                    normalized.contains("youtu.be/") ||
                    normalized.contains("instagram.com/reel/") ||
                    normalized.contains("tiktok.com/")) {
                    normalized = normalized.substringBefore("?")
                }
            }

            // Remove trailing slash for consistency
            normalized.removeSuffix("/")
        } catch (e: Exception) {
            url
        }
    }

    /**
     * Generate a unique identifier for the reel.
     * Uses a simple random hex string for KMP compatibility.
     */
    private fun generateUuid(): String {
        val chars = "0123456789abcdef"
        return buildString {
            repeat(32) {
                append(chars.random())
                if (it == 7 || it == 11 || it == 15 || it == 19) {
                    append('-')
                }
            }
        }
    }

    /**
     * Basic URL validation.
     */
    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    /**
     * Extract a readable title from URL if metadata scraping fails.
     */
    private fun extractTitleFromUrl(url: String): String {
        return try {
            // Remove protocol and www
            val cleanUrl = url
                .removePrefix("https://")
                .removePrefix("http://")
                .removePrefix("www.")

            // Get domain and path
            val parts = cleanUrl.split("/")
            val domain = parts.firstOrNull() ?: "Unknown"
            val path = parts.getOrNull(1)?.takeIf { it.isNotBlank() }

            if (path != null) {
                "$domain - $path"
            } else {
                domain
            }
        } catch (_: Exception) {
            "Shared Reel"
        }
    }

    /**
     * Extract tags based on the URL source.
     */
    private fun extractTagsFromUrl(url: String): List<String> {
        val tags = mutableListOf<String>()
        val lowercaseUrl = url.lowercase()

        when {
            lowercaseUrl.contains("instagram.com") || lowercaseUrl.contains("instagr.am") -> {
                tags.add("Instagram")
                if (lowercaseUrl.contains("/reel/") || lowercaseUrl.contains("/reels/")) {
                    tags.add("Reels")
                }
            }
            lowercaseUrl.contains("tiktok.com") -> tags.add("TikTok")
            lowercaseUrl.contains("youtube.com") || lowercaseUrl.contains("youtu.be") -> {
                tags.add("YouTube")
                if (lowercaseUrl.contains("/shorts/")) {
                    tags.add("Shorts")
                }
            }
            lowercaseUrl.contains("twitter.com") || lowercaseUrl.contains("x.com") -> tags.add("X")
            lowercaseUrl.contains("facebook.com") || lowercaseUrl.contains("fb.watch") -> tags.add("Facebook")
            lowercaseUrl.contains("snapchat.com") -> tags.add("Snapchat")
        }

        return tags
    }
}
