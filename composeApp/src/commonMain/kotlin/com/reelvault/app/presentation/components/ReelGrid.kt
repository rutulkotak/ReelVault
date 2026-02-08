package com.reelvault.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.reelvault.app.domain.featuregate.UserTier
import com.reelvault.app.domain.model.Reel

/**
 * ReelGrid Component
 * A staggered vertical grid for displaying reels in a Pinterest-style layout.
 * Supports native promo card injection at index 0 and every 8th position.
 * Optimized for the Aurora UI with proper spacing and responsiveness.
 *
 * @param reels List of reels to display
 * @param onReelThumbnailClick Callback when a reel thumbnail is clicked (opens external app)
 * @param onReelContentClick Callback when a reel content area is clicked (navigates to detail)
 * @param modifier Optional modifier
 * @param columns Number of columns (defaults to 2)
 * @param selectedItemIds Set of selected reel IDs for multi-selection mode
 * @param onReelLongClick Callback when a reel is long-pressed (for selection)
 * @param userTier Current user's tier for promo card targeting
 * @param onPromoClick Callback when a promo card is clicked
 * @param showPromoCards Whether to inject promo cards (defaults to true)
 */
@Composable
fun ReelGrid(
    reels: List<Reel>,
    onReelThumbnailClick: (Reel) -> Unit,
    onReelContentClick: (Reel) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    selectedItemIds: Set<String> = emptySet(),
    onReelLongClick: ((Reel) -> Unit)? = null,
    userTier: UserTier = UserTier.SCOUTER,
    onPromoClick: (() -> Unit)? = null,
    showPromoCards: Boolean = true
) {
    val isSelectionMode = selectedItemIds.isNotEmpty()

    // Generate mixed list with promo cards injected at strategic positions
    val mixedItems = if (showPromoCards && !isSelectionMode && reels.isNotEmpty()) {
        buildMixedItemList(reels)
    } else {
        reels.map { GridItem.ReelItem(it) }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(
            count = mixedItems.size,
            key = { index ->
                when (val item = mixedItems[index]) {
                    is GridItem.ReelItem -> item.reel.id
                    is GridItem.PromoItem -> "promo_${item.index}"
                }
            }
        ) { index ->
            when (val item = mixedItems[index]) {
                is GridItem.ReelItem -> {
                    ReelCard(
                        reel = item.reel,
                        onThumbnailClick = { onReelThumbnailClick(item.reel) },
                        onContentClick = { onReelContentClick(item.reel) },
                        isSelected = item.reel.id in selectedItemIds,
                        onLongClick = onReelLongClick?.let { { it(item.reel) } },
                        isSelectionMode = isSelectionMode
                    )
                }
                is GridItem.PromoItem -> {
                    PromoCard(
                        userTier = userTier,
                        onUpgradeClick = { onPromoClick?.invoke() }
                    )
                }
            }
        }
    }
}

/**
 * Sealed class representing grid items (Reel or Promo)
 */
private sealed class GridItem {
    data class ReelItem(val reel: Reel) : GridItem()
    data class PromoItem(val index: Int) : GridItem()
}

/**
 * Builds a mixed list of reels and promo cards.
 * Injects promo cards at index 0 and every 8th position thereafter.
 *
 * Injection pattern: [Promo, Reel, Reel, Reel, Reel, Reel, Reel, Reel, Promo, Reel, Reel, ...]
 */
private fun buildMixedItemList(reels: List<Reel>): List<GridItem> {
    val mixedItems = mutableListOf<GridItem>()
    var reelIndex = 0
    var promoCount = 0

    // Inject first promo at index 0
    mixedItems.add(GridItem.PromoItem(promoCount++))

    while (reelIndex < reels.size) {
        // Add up to 7 reels after each promo (8 items between promos including the promo itself)
        val reelsToAdd = minOf(7, reels.size - reelIndex)
        repeat(reelsToAdd) {
            mixedItems.add(GridItem.ReelItem(reels[reelIndex++]))
        }

        // Inject next promo if there are more reels
        if (reelIndex < reels.size) {
            mixedItems.add(GridItem.PromoItem(promoCount++))
        }
    }

    return mixedItems
}
