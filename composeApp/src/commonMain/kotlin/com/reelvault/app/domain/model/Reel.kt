package com.reelvault.app.domain.model

import kotlinx.datetime.Instant

/**
 * Domain model representing a saved reel in the user's library.
 */
data class Reel(
    val id: String,
    val url: String,
    val title: String,
    val thumbnail: String,
    val tags: List<String>,
    val createdAt: Instant,
    val collectionId: Long? = null,
    val notes: String? = null
)
