package com.reelvault.app.domain.model

/**
 * Domain model representing a collection of reels.
 * Collections allow users to organize their saved reels into categories.
 *
 * @param id Unique identifier for the collection
 * @param name Display name (e.g., "Gym", "Recipes")
 * @param color Hex color code for the collection (e.g., "#FF6B9D")
 * @param icon Icon identifier for the collection
 * @param reelCount Number of reels in this collection
 */
data class Collection(
    val id: Long,
    val name: String,
    val color: String,
    val icon: String,
    val reelCount: Int = 0
)
