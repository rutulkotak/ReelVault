package com.reelvault.app.data.remote

/**
 * Metadata extracted from a URL.
 */
data class PageMetadata(
    val title: String,
    val thumbnail: String?
)

/**
 * Service for scraping metadata from web pages.
 * Uses Ktor to fetch HTML and extracts Open Graph metadata.
 */
interface MetadataScraper {
    /**
     * Scrape metadata from a given URL.
     * @param url The URL to scrape.
     * @return PageMetadata containing title and thumbnail URL, or null if scraping fails.
     */
    suspend fun scrapeMetadata(url: String): PageMetadata?
}
