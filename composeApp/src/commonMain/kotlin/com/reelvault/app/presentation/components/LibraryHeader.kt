package com.reelvault.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.reelvault.app.presentation.theme.AuroraColors

/**
 * LibraryHeader Component
 * Contains a glassmorphism search bar and platform filter chips.
 *
 * @param searchQuery Current search query
 * @param onSearchQueryChange Callback when search query changes
 * @param selectedPlatform Currently selected platform filter (null = "All")
 * @param onPlatformSelected Callback when a platform filter is selected
 * @param resultsCount Number of filtered results
 * @param modifier Optional modifier
 */
@Composable
fun LibraryHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedPlatform: String?,
    onPlatformSelected: (String?) -> Unit,
    resultsCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Glassmorphism Search Bar
        GlassmorphismSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth()
        )

        // Platform Filter Chips
        PlatformFilterRow(
            selectedPlatform = selectedPlatform,
            onPlatformSelected = onPlatformSelected,
            modifier = Modifier.fillMaxWidth()
        )

        // Results Count (Animated)
        AnimatedVisibility(
            visible = searchQuery.isNotEmpty() || selectedPlatform != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "$resultsCount result${if (resultsCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = AuroraColors.TextSecondary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

/**
 * Glassmorphism Search Bar Component
 */
@Composable
private fun GlassmorphismSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = AuroraColors.MediumCharcoal.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = AuroraColors.SoftViolet.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        placeholder = {
            Text(
                text = "Search your vault...",
                style = MaterialTheme.typography.bodyLarge,
                color = AuroraColors.TextTertiary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = AuroraColors.SoftViolet
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = AuroraColors.TextSecondary
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = AuroraColors.TextPrimary,
            unfocusedTextColor = AuroraColors.TextPrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = AuroraColors.SoftViolet
        ),
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Platform Filter Chip Row
 */
@Composable
private fun PlatformFilterRow(
    selectedPlatform: String?,
    onPlatformSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlatformChip(
            label = "All",
            isSelected = selectedPlatform == null,
            onClick = { onPlatformSelected(null) }
        )
        PlatformChip(
            label = "Instagram",
            isSelected = selectedPlatform == "instagram",
            onClick = { onPlatformSelected("instagram") }
        )
        PlatformChip(
            label = "YouTube",
            isSelected = selectedPlatform == "youtube",
            onClick = { onPlatformSelected("youtube") }
        )
        PlatformChip(
            label = "TikTok",
            isSelected = selectedPlatform == "tiktok",
            onClick = { onPlatformSelected("tiktok") }
        )
    }
}

/**
 * Individual Platform Chip
 */
@Composable
private fun PlatformChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        AuroraColors.SoftViolet.copy(alpha = 0.3f)
    } else {
        AuroraColors.MediumCharcoal.copy(alpha = 0.4f)
    }

    val borderColor = if (isSelected) {
        AuroraColors.SoftViolet
    } else {
        AuroraColors.LightCharcoal
    }

    val textColor = if (isSelected) {
        AuroraColors.TextPrimary
    } else {
        AuroraColors.TextSecondary
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
