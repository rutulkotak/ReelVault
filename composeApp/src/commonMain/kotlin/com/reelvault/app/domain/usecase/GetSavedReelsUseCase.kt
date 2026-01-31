package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.model.Reel
import com.reelvault.app.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving saved reels from the library.
 * Encapsulates business logic for fetching the user's saved reel collection.
 */
class GetSavedReelsUseCase(
    private val libraryRepository: LibraryRepository
) {
    /**
     * Invoke the use case to get a reactive flow of saved reels.
     * @return Flow emitting the list of saved reels, updated on changes.
     */
    operator fun invoke(): Flow<List<Reel>> {
        return libraryRepository.getSavedReels()
    }
}
