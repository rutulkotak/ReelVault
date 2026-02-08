package com.reelvault.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reelvault.app.domain.featuregate.UserTier
import com.reelvault.app.presentation.theme.AuroraColors

/**
 * PromoCard Component
 * A promotional card that mimics the Reel card design but promotes tier upgrades.
 * Injected at strategic positions in the ReelGrid to drive user engagement.
 *
 * @param userTier The current user's tier
 * @param onUpgradeClick Callback when the promo card is clicked
 * @param modifier Optional modifier
 */
@Composable
fun PromoCard(
    userTier: UserTier,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val promoData = getPromoDataForTier(userTier)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onUpgradeClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            hoveredElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AuroraColors.SoftViolet.copy(alpha = 0.3f),
                            AuroraColors.BrightIndigo.copy(alpha = 0.5f),
                            AuroraColors.DarkViolet.copy(alpha = 0.4f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon
                Text(
                    text = promoData.icon,
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Headline
                Text(
                    text = promoData.headline,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = promoData.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CTA Badge
                Box(
                    modifier = Modifier
                        .background(
                            color = AuroraColors.BrightIndigo,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = promoData.ctaText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Data class representing promo card content
 */
private data class PromoData(
    val icon: String,
    val headline: String,
    val description: String,
    val ctaText: String
)

/**
 * Returns promo data based on the user's current tier
 */
private fun getPromoDataForTier(userTier: UserTier): PromoData {
    return when (userTier) {
        UserTier.SCOUTER -> PromoData(
            icon = "🎬",
            headline = "Upgrade to PRODUCER",
            description = "Save up to 1000 reels with 10 collections. Perfect for creators!",
            ctaText = "UNLOCK MORE →"
        )
        UserTier.PRODUCER -> PromoData(
            icon = "⭐",
            headline = "Become an ICON",
            description = "Unlock AI Magic, Cloud Sync & Advanced Search. Ultimate power user experience!",
            ctaText = "GO UNLIMITED →"
        )
        UserTier.ICON -> PromoData(
            icon = "✨",
            headline = "You're an ICON!",
            description = "Share ReelVault with friends and help them organize their content too!",
            ctaText = "SHARE APP →"
        )
    }
}


