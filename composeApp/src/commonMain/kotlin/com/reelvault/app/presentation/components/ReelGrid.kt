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
 * @param onReelClick Callback when a reel is clicked
 * @param modifier Optional modifier
 * @param columns Number of columns (defaults to 2)
 */
@Composable
fun ReelGrid(
    reels: List<Reel>,
    onReelClick: (Reel) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2
) {
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
                onClick = { onReelClick(reel) }
            )
        }
    }
}
