package com.reelvault.app.domain.featuregate

/**
 * Represents the user's subscription tier.
 * Each tier provides different levels of access to app features.
 */
enum class UserTier {
    /**
     * SCOUTER - Free, Entry Level tier.
     * Limited to 50 reel saves and 3 collections.
     */
    SCOUTER,

    /**
     * PRODUCER - Creator Choice tier.
     * Limited to 1000 reel saves and 10 collections.
     */
    PRODUCER,

    /**
     * ICON - Most Advanced tier.
     * Unlimited access to all features including AI, Cloud, and Advanced Search.
     */
    ICON
}

