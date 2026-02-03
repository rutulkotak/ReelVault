package com.reelvault.app.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.reelvault.app.domain.model.Reel
import com.reelvault.app.presentation.theme.AuroraColors
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

/**
 * ReelCard Component
 * A vertical card displaying a reel's thumbnail, title, and tags.
 * Implements glassmorphism design with Aurora theming.
 *
 * @param reel The reel to display
 * @param onThumbnailClick Callback when thumbnail is clicked (opens external app)
 * @param onContentClick Callback when content area is clicked (navigates to detail screen)
 * @param isSelected Whether this card is in selected state
 * @param onLongClick Optional callback for long press (enables selection mode)
 * @param isSelectionMode Whether the library is in selection mode
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun ReelCard(
    reel: Reel,
    onThumbnailClick: () -> Unit,
    onContentClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    isSelectionMode: Boolean = false
) {
    // Hover state for play icon overlay
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) AuroraColors.SoftViolet else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuroraColors.MediumCharcoal
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Thumbnail with gradient overlay - Click to open in external app
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 16f) // Instagram reel aspect ratio
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .hoverable(interactionSource = interactionSource)
                    .then(
                        if (onLongClick != null) {
                            Modifier.combinedClickable(
                                onClick = {
                                    if (isSelectionMode) {
                                        onLongClick()
                                    } else {
                                        onThumbnailClick()
                                    }
                                },
                                onLongClick = onLongClick
                            )
                        } else {
                            Modifier.clickable {
                                if (isSelectionMode) {
                                    // In selection mode without long click, do nothing
                                } else {
                                    onThumbnailClick()
                                }
                            }
                        }
                    )
            ) {
                // Image with Kamel loading/error handling
                KamelImage(
                    resource = asyncPainterResource(data = reel.thumbnail),
                    contentDescription = reel.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onLoading = { progress: Float ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AuroraColors.DarkCharcoal),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = AuroraColors.SoftViolet
                            )
                        }
                    },
                    onFailure = { exception: Throwable ->
                        // Fallback to platform icon
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AuroraColors.DarkCharcoal),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getPlatformIcon(reel.url),
                                style = MaterialTheme.typography.displayLarge
                            )
                        }
                    }
                )

                // Gradient overlay for glassmorphism effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    AuroraColors.MidnightIndigo.copy(alpha = 0.7f)
                                ),
                                startY = 300f
                            )
                        )
                )

                // Play Icon Overlay (visible on hover or with 0.5 opacity always)
                if (!isSelected) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = AuroraColors.TextPrimary.copy(
                                alpha = if (isHovered) 0.9f else 0.5f
                            ),
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    color = AuroraColors.MidnightIndigo.copy(
                                        alpha = if (isHovered) 0.7f else 0.4f
                                    ),
                                    shape = CircleShape
                                )
                                .padding(8.dp)
                        )
                    }
                }

                // Selection Overlay
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AuroraColors.SoftViolet.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = AuroraColors.TextPrimary,
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = AuroraColors.SoftViolet,
                                    shape = CircleShape
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }

            // Content section - Click to navigate to detail screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isSelectionMode) {
                            onLongClick?.invoke()
                        } else {
                            onContentClick()
                        }
                    }
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title with edit icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = reel.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = AuroraColors.TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Edit",
                        tint = AuroraColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Tags
                if (reel.tags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        reel.tags.take(3).forEach { tag ->
                            TagChip(tag = tag)
                        }
                        if (reel.tags.size > 3) {
                            TagChip(tag = "+${reel.tags.size - 3}")
                        }
                    }
                }
            }
        }
    }
}

/**
 * TagChip Component
 * A small chip displaying a tag with glassmorphism styling.
 */
@Composable
private fun TagChip(
    tag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AuroraColors.GlassOverlay)
            .border(
                width = 1.dp,
                color = AuroraColors.GlassStroke,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = AuroraColors.VioletGlow
        )
    }
}

/**
 * Get platform-specific icon based on URL.
 * Returns high-res platform logo emoji for fallback display.
 */
private fun getPlatformIcon(url: String): String {
    return when {
        url.contains("youtube", ignoreCase = true) ||
        url.contains("youtu.be", ignoreCase = true) -> "▶️"  // YouTube/Shorts
        url.contains("instagram", ignoreCase = true) -> "📸"  // Instagram
        url.contains("tiktok", ignoreCase = true) -> "🎵"  // TikTok
        url.contains("facebook", ignoreCase = true) ||
        url.contains("fb.watch", ignoreCase = true) -> "📘"  // Facebook
        url.contains("twitter", ignoreCase = true) ||
        url.contains("x.com", ignoreCase = true) -> "🐦"  // Twitter/X
        else -> "🎬"  // Generic video
    }
}

