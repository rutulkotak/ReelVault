package com.reelvault.app.domain.repository

import com.reelvault.app.domain.model.Collection
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing collections.
 * Implementation will be provided in the Data layer.
 */
interface CollectionRepository {

    /**
     * Get all collections as a Flow for reactive updates.
     */
    fun getCollections(): Flow<List<Collection>>

    /**
     * Get a single collection by its ID.
     */
    suspend fun getCollectionById(id: Long): Collection?

    /**
     * Create a new collection.
     * @return The ID of the created collection
     */
    suspend fun createCollection(name: String, color: String, icon: String): Long

    /**
     * Update an existing collection.
     */
    suspend fun updateCollection(id: Long, name: String, color: String, icon: String)

    /**
     * Delete a collection.
     * Note: Reels in the collection will have their collectionId set to NULL.
     */
    suspend fun deleteCollection(id: Long)
}
