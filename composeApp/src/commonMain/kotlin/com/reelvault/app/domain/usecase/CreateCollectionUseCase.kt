package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.repository.CollectionRepository

/**
 * Use case for creating a new collection.
 */
class CreateCollectionUseCase(
    private val collectionRepository: CollectionRepository
) {
    suspend operator fun invoke(name: String, color: String, icon: String): Result<Long> {
        return try {
            val id = collectionRepository.createCollection(name, color, icon)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
