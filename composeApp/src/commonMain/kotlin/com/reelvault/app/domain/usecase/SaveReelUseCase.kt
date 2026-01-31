package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.model.Reel
import com.reelvault.app.domain.repository.LibraryRepository

/**
 * Use case for saving a reel to the user's library.
 * Encapsulates business logic for persisting a new reel.
 */
class SaveReelUseCase(
    private val libraryRepository: LibraryRepository
) {
    /**
     * Invoke the use case to save a reel.
     * @param reel The reel to save to the library.
     * @return Result indicating success or failure.
     */
    suspend operator fun invoke(reel: Reel): Result<Unit> {
        return try {
            // Check if reel already exists
            if (libraryRepository.isReelSaved(reel.url)) {
                return Result.failure(ReelAlreadySavedException(reel.url))
            }
            libraryRepository.saveReel(reel)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Exception thrown when attempting to save a reel that already exists.
 */
class ReelAlreadySavedException(url: String) : Exception("Reel with URL '$url' is already saved")
