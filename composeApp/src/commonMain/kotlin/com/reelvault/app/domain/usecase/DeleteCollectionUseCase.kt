package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.repository.CollectionRepository

/**
 * Use case for deleting a collection.
 * Reels in the collection will have their collectionId set to NULL.
 */
class DeleteCollectionUseCase(
    private val collectionRepository: CollectionRepository
) {
    suspend operator fun invoke(collectionId: Long): Result<Unit> {
        return try {
            collectionRepository.deleteCollection(collectionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
