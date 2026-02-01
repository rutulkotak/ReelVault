package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.repository.LibraryRepository

/**
 * Use case for deleting multiple reels from the user's library.
 * Encapsulates business logic for batch deletion.
 */
class DeleteReelsUseCase(
    private val libraryRepository: LibraryRepository
) {
    /**
     * Invoke the use case to delete multiple reels.
     * @param ids The list of reel IDs to delete.
     * @return Result indicating success or failure.
     */
    suspend operator fun invoke(ids: List<String>): Result<Unit> {
        return try {
            if (ids.isEmpty()) {
                return Result.failure(IllegalArgumentException("No items to delete"))
            }
            libraryRepository.deleteReels(ids)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
