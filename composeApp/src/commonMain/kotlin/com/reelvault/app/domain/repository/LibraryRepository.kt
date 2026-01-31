package com.reelvault.app.domain.repository

import com.reelvault.app.domain.model.Reel
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing the user's reel library.
 * Implementation will be provided in the Data layer.
 */
interface LibraryRepository {

    /**
     * Get all saved reels as a Flow for reactive updates.
     */
    fun getSavedReels(): Flow<List<Reel>>

    /**
     * Get a single reel by its ID.
     */
    suspend fun getReelById(id: String): Reel?

    /**
     * Save a new reel to the library.
     */
    suspend fun saveReel(reel: Reel)

    /**
     * Delete a reel from the library.
     */
    suspend fun deleteReel(id: String)

    /**
     * Check if a reel exists in the library.
     */
    suspend fun isReelSaved(url: String): Boolean
}
