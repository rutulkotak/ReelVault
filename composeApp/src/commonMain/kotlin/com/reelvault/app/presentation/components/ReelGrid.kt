package com.reelvault.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.reelvault.app.domain.model.Reel

/**
 * ReelGrid Component
 * A staggered vertical grid for displaying reels in a Pinterest-style layout.
 * Optimized for the Aurora UI with proper spacing and responsiveness.
 *
 * @param reels List of reels to display
 * @param onReelThumbnailClick Callback when a reel thumbnail is clicked (opens external app)
 * @param onReelContentClick Callback when a reel content area is clicked (navigates to detail)
 * @param modifier Optional modifier
 * @param columns Number of columns (defaults to 2)
 * @param selectedItemIds Set of selected reel IDs for multi-selection mode
 * @param onReelLongClick Callback when a reel is long-pressed (for selection)
 */
@Composable
fun ReelGrid(
    reels: List<Reel>,
    onReelThumbnailClick: (Reel) -> Unit,
    onReelContentClick: (Reel) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    selectedItemIds: Set<String> = emptySet(),
    onReelLongClick: ((Reel) -> Unit)? = null
) {
    val isSelectionMode = selectedItemIds.isNotEmpty()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(
            items = reels,
            key = { reel -> reel.id }
        ) { reel ->
            ReelCard(
                reel = reel,
                onThumbnailClick = { onReelThumbnailClick(reel) },
                onContentClick = { onReelContentClick(reel) },
                isSelected = reel.id in selectedItemIds,
                onLongClick = onReelLongClick?.let { { it(reel) } },
                isSelectionMode = isSelectionMode
            )
        }
    }
}
