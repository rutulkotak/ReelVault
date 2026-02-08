package com.reelvault.app.domain.featuregate

/**
 * Interface for controlling feature access based on user tier.
 * Defines limits and availability of features for different subscription tiers.
 */
interface FeatureGate {

    /**
     * The current user's subscription tier.
     */
    val currentTier: UserTier

    /**
     * Maximum number of reels the user can save.
     * Returns null for unlimited.
     */
    val maxReelSaves: Int?

    /**
     * Maximum number of collections the user can create.
     * Returns null for unlimited.
     */
    val maxCollections: Int?

    /**
     * Whether the user has access to AI features.
     */
    val hasAIAccess: Boolean

    /**
     * Whether the user has access to Cloud sync features.
     */
    val hasCloudAccess: Boolean

    /**
     * Whether the user has access to Advanced Search & Filtering.
     */
    val hasAdvancedSearchAccess: Boolean

    /**
     * Checks if the user can save another reel given the current count.
     * @param currentReelCount The current number of saved reels.
     * @return True if the user can save more reels, false if limit reached.
     */
    fun canSaveReel(currentReelCount: Int): Boolean

    /**
     * Checks if the user can create another collection given the current count.
     * @param currentCollectionCount The current number of collections.
     * @return True if the user can create more collections, false if limit reached.
     */
    fun canCreateCollection(currentCollectionCount: Int): Boolean

    /**
     * Returns the remaining reel saves available.
     * Returns null for unlimited.
     * @param currentReelCount The current number of saved reels.
     */
    fun remainingReelSaves(currentReelCount: Int): Int?

    /**
     * Returns the remaining collections available.
     * Returns null for unlimited.
     * @param currentCollectionCount The current number of collections.
     */
    fun remainingCollections(currentCollectionCount: Int): Int?
}

