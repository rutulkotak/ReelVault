package com.reelvault.app.data.featuregate

import com.reelvault.app.domain.featuregate.FeatureGate
import com.reelvault.app.domain.featuregate.UserTier

/**
 * Implementation of FeatureGate that defines feature limits for each user tier.
 *
 * Tier Limits:
 * - SCOUTER (Free, Entry Level): 50 reel saves, 3 collections
 * - PRODUCER (Creator Choice): 1000 reel saves, 10 collections
 * - ICON (Most Advanced): Unlimited saves, AI, Cloud, Advanced Search & Filtering
 */
class FeatureGateImpl(
    override val currentTier: UserTier = UserTier.SCOUTER // Default to free tier
) : FeatureGate {

    companion object {
        // SCOUTER limits
        private const val SCOUTER_MAX_REELS = 3
        private const val SCOUTER_MAX_COLLECTIONS = 3

        // PRODUCER limits
        private const val PRODUCER_MAX_REELS = 1000
        private const val PRODUCER_MAX_COLLECTIONS = 10
    }

    override val maxReelSaves: Int?
        get() = when (currentTier) {
            UserTier.SCOUTER -> SCOUTER_MAX_REELS
            UserTier.PRODUCER -> PRODUCER_MAX_REELS
            UserTier.ICON -> null // Unlimited
        }

    override val maxCollections: Int?
        get() = when (currentTier) {
            UserTier.SCOUTER -> SCOUTER_MAX_COLLECTIONS
            UserTier.PRODUCER -> PRODUCER_MAX_COLLECTIONS
            UserTier.ICON -> null // Unlimited
        }

    override val hasAIAccess: Boolean
        get() = currentTier == UserTier.ICON

    override val hasCloudAccess: Boolean
        get() = currentTier == UserTier.ICON

    override val hasAdvancedSearchAccess: Boolean
        get() = currentTier == UserTier.ICON

    override fun canSaveReel(currentReelCount: Int): Boolean {
        val max = maxReelSaves ?: return true
        return currentReelCount < max
    }

    override fun canCreateCollection(currentCollectionCount: Int): Boolean {
        val max = maxCollections ?: return true
        return currentCollectionCount < max
    }

    override fun remainingReelSaves(currentReelCount: Int): Int? {
        val max = maxReelSaves ?: return null
        return (max - currentReelCount).coerceAtLeast(0)
    }

    override fun remainingCollections(currentCollectionCount: Int): Int? {
        val max = maxCollections ?: return null
        return (max - currentCollectionCount).coerceAtLeast(0)
    }
}

