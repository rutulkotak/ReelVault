package com.reelvault.app.di

import com.reelvault.app.data.local.DatabaseDriverFactory
import com.reelvault.app.data.remote.KtorMetadataScraper
import com.reelvault.app.data.remote.MetadataScraper
import com.reelvault.app.data.repository.CollectionRepositoryImpl
import com.reelvault.app.data.repository.LibraryRepositoryImpl
import com.reelvault.app.database.ReelVaultDatabase
import com.reelvault.app.domain.repository.CollectionRepository
import com.reelvault.app.domain.repository.LibraryRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for the Data layer.
 * Provides database, HTTP client, repository, and scraper dependencies.
 */
val dataModule = module {
    // SQLDelight Database
    single {
        val driver = get<DatabaseDriverFactory>().createDriver()
        ReelVaultDatabase(driver)
    }

    // HTTP Client for metadata scraping
    single {
        HttpClient(get<HttpClientEngine>())
    }

    // Metadata Scraper
    singleOf(::KtorMetadataScraper) bind MetadataScraper::class

    // Repositories
    singleOf(::LibraryRepositoryImpl) bind LibraryRepository::class
    singleOf(::CollectionRepositoryImpl) bind CollectionRepository::class
}

/**
 * Platform-specific module for providing database driver factory.
 * Must be provided by each platform (Android/iOS).
 */
expect val platformModule: Module
