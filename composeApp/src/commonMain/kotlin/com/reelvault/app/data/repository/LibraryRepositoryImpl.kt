package com.reelvault.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.reelvault.app.data.remote.MetadataScraper
import com.reelvault.app.database.ReelVaultDatabase
import com.reelvault.app.domain.model.Reel
import com.reelvault.app.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of LibraryRepository using SQLDelight for local persistence
 * and MetadataScraper for fetching metadata from URLs.
 */
class LibraryRepositoryImpl(
    private val database: ReelVaultDatabase,
    private val metadataScraper: MetadataScraper
) : LibraryRepository {

    private val queries = database.reelVaultQueries

    override fun getSavedReels(): Flow<List<Reel>> {
        return queries.getAllReels()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbReels ->
                dbReels.map { it.toDomainModel() }
            }
    }

    override suspend fun getReelCount(): Int = withContext(Dispatchers.IO) {
        queries.getReelCount().executeAsOne().toInt()
    }

    override suspend fun getReelById(id: String): Reel? = withContext(Dispatchers.IO) {
        queries.getReelById(id).executeAsOneOrNull()?.toDomainModel()
    }

    override suspend fun saveReel(reel: Reel): Unit = withContext(Dispatchers.IO) {
        queries.insertReel(
            id = reel.id,
            url = reel.url,
            title = reel.title,
            thumbnailUrl = reel.thumbnail,
            tags = reel.tags.joinToString(","),
            createdAt = reel.createdAt,
            collectionId = reel.collectionId,
            notes = reel.notes
        )
    }

    override suspend fun deleteReel(id: String): Unit = withContext(Dispatchers.IO) {
        queries.deleteReelById(id)
    }

    override suspend fun deleteReels(ids: List<String>) = withContext(Dispatchers.IO) {
        ids.forEach { id ->
            queries.deleteReelById(id)
        }
    }

    override suspend fun isReelSaved(url: String): Boolean = withContext(Dispatchers.IO) {
        queries.isReelSaved(url).executeAsOne()
    }

    override suspend fun updateReelDetails(
        id: String,
        title: String,
        notes: String?,
        tags: List<String>,
        collectionId: Long?
    ): Unit = withContext(Dispatchers.IO) {
        queries.updateReelDetails(
            title = title,
            notes = notes,
            tags = tags.joinToString(","),
            collectionId = collectionId,
            id = id
        )
    }

    override fun getReelsByCollection(collectionId: Long): Flow<List<Reel>> {
        return queries.getReelsByCollection(collectionId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbReels ->
                dbReels.map { it.toDomainModel() }
            }
    }

    override fun getReelsWithoutCollection(): Flow<List<Reel>> {
        return queries.getReelsWithoutCollection()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbReels ->
                dbReels.map { it.toDomainModel() }
            }
    }

    override suspend fun moveReelsToCollection(reelIds: List<String>, collectionId: Long?) = withContext(Dispatchers.IO) {
        queries.transaction {
            reelIds.forEach { id ->
                val reel = queries.getReelById(id).executeAsOne()
                queries.updateReelDetails(
                    id = id,
                    title = reel.title,
                    notes = reel.notes,
                    tags = reel.tags,
                    collectionId = collectionId
                )
            }
        }
    }

    /**
     * Fetch metadata for a URL using the MetadataScraper.
     * This can be used when saving a reel to auto-populate title and thumbnail.
     */
    suspend fun fetchMetadata(url: String) = withContext(Dispatchers.IO) {
        metadataScraper.scrapeMetadata(url)
    }
}

/**
 * Extension function to convert database Reel to domain Reel model.
 */
private fun com.reelvault.app.database.Reel.toDomainModel(): Reel {
    return Reel(
        id = id,
        url = url,
        title = title,
        thumbnail = thumbnailUrl,
        tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
        createdAt = createdAt,
        collectionId = collectionId,
        notes = notes
    )
}
