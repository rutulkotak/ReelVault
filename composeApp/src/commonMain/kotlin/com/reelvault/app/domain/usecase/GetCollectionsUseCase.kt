package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.model.Collection
import com.reelvault.app.domain.repository.CollectionRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving all collections.
 */
class GetCollectionsUseCase(
    private val collectionRepository: CollectionRepository
) {
    operator fun invoke(): Flow<List<Collection>> {
        return collectionRepository.getCollections()
    }
}
