package com.reelvault.app.data

import com.reelvault.app.data.remote.KtorMetadataScraper
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MetadataScraperTest {

    @Test
    fun testScrapingOpenGraphMetadata() = runTest {
        val mockHtml = """
            <html>
            <head>
                <meta property="og:title" content="Test Reel Title" />
                <meta property="og:image" content="https://example.com/image.jpg" />
            </head>
            <body></body>
            </html>
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel(mockHtml),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/html")
            )
        }

        val httpClient = HttpClient(mockEngine)
        val scraper = KtorMetadataScraper(httpClient)

        val metadata = scraper.scrapeMetadata("https://example.com/reel")

        assertNotNull(metadata)
        assertEquals("Test Reel Title", metadata.title)
        assertEquals("https://example.com/image.jpg", metadata.thumbnail)
    }

    @Test
    fun testScrapingWithTitleTagFallback() = runTest {
        val mockHtml = """
            <html>
            <head>
                <title>Fallback Title</title>
            </head>
            <body></body>
            </html>
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel(mockHtml),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/html")
            )
        }

        val httpClient = HttpClient(mockEngine)
        val scraper = KtorMetadataScraper(httpClient)

        val metadata = scraper.scrapeMetadata("https://example.com/reel")

        assertNotNull(metadata)
        assertEquals("Fallback Title", metadata.title)
    }
}
