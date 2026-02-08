package com.reelvault.app.domain.usecase

import com.reelvault.app.domain.featuregate.FeatureGate
import com.reelvault.app.domain.repository.CollectionRepository

/**
 * Use case for creating a new collection.
 * Enforces collection limits based on user tier.
 */
class CreateCollectionUseCase(
    private val collectionRepository: CollectionRepository,
    private val featureGate: FeatureGate
) {
    /**
     * Result of creating a collection.
     */
    sealed class CreateResult {
        data class Success(val id: Long) : CreateResult()
        data class LimitReached(val maxCollections: Int) : CreateResult()
        data class Error(val message: String) : CreateResult()
    }

    /**
     * Invoke the use case to create a new collection.
     *
     * @param name Collection name
     * @param color Hex color string
     * @param icon Icon identifier string
     * @return CreateResult indicating outcome
     */
    suspend operator fun invoke(name: String, color: String, icon: String): CreateResult {
        return try {
            // 1. Check current collection count
            val currentCount = collectionRepository.getCollectionCount()

            // 2. Enforce limit using FeatureGate
            if (!featureGate.canCreateCollection(currentCount)) {
                return CreateResult.LimitReached(featureGate.maxCollections ?: 0)
            }

            // 3. Create collection if limit not reached
            val id = collectionRepository.createCollection(name, color, icon)
            CreateResult.Success(id)
        } catch (e: Exception) {
            CreateResult.Error(e.message ?: "Failed to create collection")
        }
    }
}
