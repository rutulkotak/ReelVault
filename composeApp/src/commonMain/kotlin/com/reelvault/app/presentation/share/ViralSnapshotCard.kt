package com.reelvault.app.presentation.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reelvault.app.domain.model.Reel
import com.reelvault.app.presentation.theme.AuroraColors
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

/**
 * Viral Snapshot - A beautiful shareable image for Instagram Stories.
 * Displays a collection summary with Aurora-themed design.
 *
 * Usage: Capture this composable as an image and share to social media.
 */
@Composable
fun ViralSnapshotCard(
    collectionName: String,
    reels: List<Reel>,
    modifier: Modifier = Modifier
) {
    // Instagram Story dimensions: 1080x1920 (9:16 ratio)
    Box(
        modifier = modifier
            .aspectRatio(9f / 16f)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AuroraColors.MidnightIndigo,
                        AuroraColors.DeepIndigo,
                        AuroraColors.RichIndigo
                    )
                )
            )
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = "📱",
                    fontSize = 48.sp
                )

                Text(
                    text = "ReelVault",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AuroraColors.TextPrimary,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Collection Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = collectionName,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AuroraColors.TextPrimary,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Video count badge
                Box(
                    modifier = Modifier
                        .background(
                            color = AuroraColors.BrightIndigo.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "${reels.size} Videos Saved",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = AuroraColors.BrightIndigo,
                        fontSize = 20.sp
                    )
                }

                // Preview thumbnails in a grid (max 4)
                if (reels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))

                    ThumbnailGrid(
                        reels = reels.take(4),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Tags
                if (reels.isNotEmpty()) {
                    val topTags = reels
                        .flatMap { it.tags }
                        .groupBy { it }
                        .entries
                        .sortedByDescending { it.value.size }
                        .take(3)
                        .map { it.key }

                    if (topTags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = topTags.joinToString(" • ") { "#$it" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = AuroraColors.SoftViolet,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Call to Action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Text(
                    text = "Save. Organize. Never Lose.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AuroraColors.TextSecondary,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Download ReelVault",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AuroraColors.BrightIndigo,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ThumbnailGrid(
    reels: List<Reel>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        reels.forEach { reel ->
            KamelImage(
                resource = asyncPainterResource(reel.thumbnail),
                contentDescription = reel.title,
                modifier = Modifier
                    .size(70.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(AuroraColors.LightCharcoal),
                contentScale = ContentScale.Crop,
                onLoading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AuroraColors.MediumCharcoal)
                    )
                },
                onFailure = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AuroraColors.MediumCharcoal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📹", fontSize = 24.sp)
                    }
                }
            )
        }
    }
}
