package com.reelvault.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
 * @param onClick Callback when the card is clicked
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReelCard(
    reel: Reel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuroraColors.MediumCharcoal
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Thumbnail with gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 16f) // Instagram reel aspect ratio
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                // Image
                KamelImage(
                    resource = asyncPainterResource(data = reel.thumbnail),
                    contentDescription = reel.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onLoading = { progress ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AuroraColors.DarkCharcoal),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.size(40.dp),
                                color = AuroraColors.SoftViolet
                            )
                        }
                    },
                    onFailure = { exception ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AuroraColors.DarkCharcoal),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🎬",
                                style = MaterialTheme.typography.displayMedium,
                                color = AuroraColors.TextTertiary
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
            }

            // Content section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title
                Text(
                    text = reel.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = AuroraColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

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
