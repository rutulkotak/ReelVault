package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.model.Reel
import com.reelvault.app.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting reels filtered by collection.
 */
class GetReelsByCollectionUseCase(
    private val libraryRepository: LibraryRepository
) {
    operator fun invoke(collectionId: Long?): Flow<List<Reel>> {
        return if (collectionId == null) {
            libraryRepository.getReelsWithoutCollection()
        } else {
            libraryRepository.getReelsByCollection(collectionId)
        }
    }
}
