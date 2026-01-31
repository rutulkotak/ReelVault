package com.reelvault.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

/**
 * Implementation of MetadataScraper using Ktor HTTP client.
 * Extracts Open Graph metadata (og:title, og:image) from HTML using simple regex patterns.
 */
class KtorMetadataScraper(private val httpClient: HttpClient) : MetadataScraper {

    override suspend fun scrapeMetadata(url: String): PageMetadata? {
        return try {
            val html = httpClient.get(url).bodyAsText()

            val title = extractMetaTag(html, "og:title")
                ?: extractMetaTag(html, "twitter:title")
                ?: extractTitleTag(html)
                ?: "Untitled"

            val thumbnail = extractMetaTag(html, "og:image")
                ?: extractMetaTag(html, "twitter:image")

            PageMetadata(
                title = title.trim(),
                thumbnail = thumbnail?.trim()
            )
        } catch (e: Exception) {
            // Log error in production
            println("Error scraping metadata from $url: ${e.message}")
            null
        }
    }

    /**
     * Extract content from Open Graph or Twitter meta tags.
     * Example: <meta property="og:title" content="Some Title">
     */
    private fun extractMetaTag(html: String, property: String): String? {
        // Try property="..." pattern (Open Graph)
        val propertyPattern = """<meta\s+property=["']$property["']\s+content=["']([^"']+)["']""".toRegex(RegexOption.IGNORE_CASE)
        propertyPattern.find(html)?.groupValues?.getOrNull(1)?.let { return it }

        // Try content="..." property="..." pattern (reversed order)
        val reversedPattern = """<meta\s+content=["']([^"']+)["']\s+property=["']$property["']""".toRegex(RegexOption.IGNORE_CASE)
        reversedPattern.find(html)?.groupValues?.getOrNull(1)?.let { return it }

        // Try name="..." pattern (Twitter cards)
        val namePattern = """<meta\s+name=["']$property["']\s+content=["']([^"']+)["']""".toRegex(RegexOption.IGNORE_CASE)
        namePattern.find(html)?.groupValues?.getOrNull(1)?.let { return it }

        // Try content="..." name="..." pattern (reversed order)
        val reversedNamePattern = """<meta\s+content=["']([^"']+)["']\s+name=["']$property["']""".toRegex(RegexOption.IGNORE_CASE)
        return reversedNamePattern.find(html)?.groupValues?.getOrNull(1)
    }

    /**
     * Extract content from <title> tag as fallback.
     */
    private fun extractTitleTag(html: String): String? {
        val titlePattern = """<title>([^<]+)</title>""".toRegex(RegexOption.IGNORE_CASE)
        return titlePattern.find(html)?.groupValues?.getOrNull(1)
    }
}
