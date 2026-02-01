package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.repository.LibraryRepository

/**
 * Use case for updating reel details including title, notes, tags, and collection.
 */
class UpdateReelDetailsUseCase(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(
        id: String,
        title: String,
        notes: String?,
        tags: List<String>,
        collectionId: Long?
    ): Result<Unit> {
        return try {
            libraryRepository.updateReelDetails(id, title, notes, tags, collectionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
