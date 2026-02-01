package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.repository.LibraryRepository

/**
 * Use case for moving multiple reels to a collection.
 */
class MoveReelsToCollectionUseCase(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(reelIds: List<String>, collectionId: Long?): Result<Unit> {
        return try {
            libraryRepository.moveReelsToCollection(reelIds, collectionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
