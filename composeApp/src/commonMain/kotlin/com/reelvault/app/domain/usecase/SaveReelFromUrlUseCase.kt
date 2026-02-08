package com.reelvault.app.domain.usecase

import com.reelvault.app.data.remote.MetadataScraper
import com.reelvault.app.domain.model.Reel
import com.reelvault.app.domain.repository.LibraryRepository
import com.reelvault.app.utils.VaultTime
import kotlinx.coroutines.flow.first

/**
 * Use case for saving a reel from a shared URL.
 * Handles metadata scraping and reel persistence.
 *
 * This is the primary entry point for the Share Sheet flow.
 * Enforces the 50-reel vault capacity limit.
 */
class SaveReelFromUrlUseCase(
    private val libraryRepository: LibraryRepository,
    private val metadataScraper: MetadataScraper
) {
    companion object {
        private const val MAX_VAULT_CAPACITY = 50
    }

    /**
     * Result of saving a reel from URL.
     */
    sealed class SaveResult {
        data class Success(val reel: Reel) : SaveResult()
        data object AlreadyExists : SaveResult()
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
            val cleanUrl = url.trim()
            if (!isValidUrl(cleanUrl)) {
                return SaveResult.Error("Invalid URL format")
            }

            // Check vault capacity before proceeding with extraction
            val currentCount = libraryRepository.getTotalReelsCount().first()
            if (currentCount >= MAX_VAULT_CAPACITY) {
                throw VaultFullException(currentCount)
            }

            // Check if reel already exists
            if (libraryRepository.isReelSaved(cleanUrl)) {
                return SaveResult.AlreadyExists
            }

            // Scrape metadata from URL
            val metadata = metadataScraper.scrapeMetadata(cleanUrl)

            // Create reel with scraped metadata or defaults
            val reel = Reel(
                id = generateUuid(),
                url = cleanUrl,
                title = metadata?.title ?: extractTitleFromUrl(cleanUrl),
                thumbnail = metadata?.thumbnail ?: "",
                tags = extractTagsFromUrl(cleanUrl),
                createdAt = VaultTime().getCurrentEpochMillis()
            )

            // Save to repository
            libraryRepository.saveReel(reel)

            SaveResult.Success(reel)
        } catch (e: VaultFullException) {
            SaveResult.Error("Vault is full (${e.currentCount}/$MAX_VAULT_CAPACITY). Delete some reels to save new ones.")
        } catch (e: Exception) {
            SaveResult.Error(e.message ?: "Failed to save reel")
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

        when {
            url.contains("instagram.com") || url.contains("instagr.am") -> {
                tags.add("Instagram")
                if (url.contains("/reel/") || url.contains("/reels/")) {
                    tags.add("Reels")
                }
            }
            url.contains("tiktok.com") -> tags.add("TikTok")
            url.contains("youtube.com") || url.contains("youtu.be") -> {
                tags.add("YouTube")
                if (url.contains("/shorts/")) {
                    tags.add("Shorts")
                }
            }
            url.contains("twitter.com") || url.contains("x.com") -> tags.add("X")
            url.contains("facebook.com") || url.contains("fb.watch") -> tags.add("Facebook")
            url.contains("snapchat.com") -> tags.add("Snapchat")
        }

        return tags
    }
}

/**
 * Exception thrown when the vault has reached its maximum capacity of 50 reels.
 */
class VaultFullException(val currentCount: Int) : Exception("Vault is full ($currentCount/50 reels). Delete some reels to save new ones.")

