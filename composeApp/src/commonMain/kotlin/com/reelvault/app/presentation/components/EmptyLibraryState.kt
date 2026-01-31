package com.reelvault.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reelvault.app.presentation.theme.AuroraColors

/**
 * EmptyState Component
 * An illustrated empty state for the library with Aurora theming.
 */
@Composable
fun EmptyLibraryState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustrated icon with glassmorphism
        Column(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(AuroraColors.GlassOverlay)
                .border(
                    width = 2.dp,
                    color = AuroraColors.GlassStroke,
                    shape = CircleShape
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎬",
                style = MaterialTheme.typography.displayLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "Your Vault is Empty",
            style = MaterialTheme.typography.headlineMedium,
            color = AuroraColors.TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Start saving your favorite reels\nand build your collection",
            style = MaterialTheme.typography.bodyLarge,
            color = AuroraColors.TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hint
        Text(
            text = "✨ Tap the + button to add your first reel",
            style = MaterialTheme.typography.bodyMedium,
            color = AuroraColors.VioletGlow,
            textAlign = TextAlign.Center
        )
    }
}
