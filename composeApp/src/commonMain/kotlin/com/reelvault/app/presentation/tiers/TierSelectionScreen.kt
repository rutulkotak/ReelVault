package com.reelvault.app.presentation.tiers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.reelvault.app.domain.featuregate.UserTier
import com.reelvault.app.presentation.base.ObserveEffect
import com.reelvault.app.presentation.theme.AuroraColors
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * Voyager Screen for Tier Selection.
 * Shows SCOUTER, PRODUCER, and ICON tiers with feature comparison.
 */
data class TierSelectionScreen(
    val highlightTier: UserTier? = null
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel: TierSelectionViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        val pagerState = rememberPagerState(
            initialPage = when (highlightTier) {
                UserTier.PRODUCER -> 1
                UserTier.ICON -> 2
                else -> 0
            },
            pageCount = { 3 }
        )

        // Sync pager state with viewModel
        LaunchedEffect(pagerState.currentPage) {
            viewModel.onIntent(TierSelectionContract.Intent.PageChanged(pagerState.currentPage))
        }

        // Effect handling
        ObserveEffect(viewModel.effect) { effect ->
            when (effect) {
                is TierSelectionContract.Effect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is TierSelectionContract.Effect.NavigateBack -> {
                    navigator.pop()
                }
                is TierSelectionContract.Effect.UpgradeSuccess -> {
                    snackbarHostState.showSnackbar("Successfully upgraded to ${effect.tier.name}!")
                    navigator.pop()
                }
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(AuroraColors.MidnightIndigo),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Choose Your Tier",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AuroraColors.TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.onIntent(TierSelectionContract.Intent.DismissScreen)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = AuroraColors.TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AuroraColors.DeepIndigo,
                        titleContentColor = AuroraColors.TextPrimary
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = AuroraColors.MidnightIndigo
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Pager Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index)
                                        AuroraColors.BrightIndigo
                                    else
                                        AuroraColors.LightCharcoal
                                )
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                        if (index < 2) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }

                // Horizontal Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    when (page) {
                        0 -> TierCard(
                            tier = UserTier.SCOUTER,
                            currentTier = state.currentTier,
                            onUpgrade = { viewModel.onIntent(TierSelectionContract.Intent.UpgradeToTier(UserTier.SCOUTER)) }
                        )
                        1 -> TierCard(
                            tier = UserTier.PRODUCER,
                            currentTier = state.currentTier,
                            onUpgrade = { viewModel.onIntent(TierSelectionContract.Intent.UpgradeToTier(UserTier.PRODUCER)) }
                        )
                        2 -> TierCard(
                            tier = UserTier.ICON,
                            currentTier = state.currentTier,
                            onUpgrade = { viewModel.onIntent(TierSelectionContract.Intent.UpgradeToTier(UserTier.ICON)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TierCard(
    tier: UserTier,
    currentTier: UserTier,
    onUpgrade: () -> Unit
) {
    val tierData = getTierData(tier)
    val isCurrentTier = tier == currentTier

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Tier Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentTier)
                    AuroraColors.BrightIndigo.copy(alpha = 0.2f)
                else
                    AuroraColors.DeepIndigo
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tier Icon
                Text(
                    text = tierData.icon,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tier Name
                Text(
                    text = tierData.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = AuroraColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tier Tagline
                Text(
                    text = tierData.tagline,
                    style = MaterialTheme.typography.bodyLarge,
                    color = AuroraColors.TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Price
                Text(
                    text = tierData.price,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AuroraColors.SoftViolet
                )

                if (isCurrentTier) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Current",
                            tint = AuroraColors.AuroraGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Current Tier",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AuroraColors.AuroraGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Features Section
        Text(
            text = "Features",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AuroraColors.TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        tierData.features.forEach { feature ->
            FeatureItem(feature)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Upgrade Button
        if (!isCurrentTier) {
            Button(
                onClick = onUpgrade,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuroraColors.BrightIndigo,
                    contentColor = AuroraColors.TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Upgrade to ${tierData.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FeatureItem(feature: TierFeature) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = if (feature.included) "✓" else "×",
            style = MaterialTheme.typography.titleMedium,
            color = if (feature.included) AuroraColors.AuroraGreen else AuroraColors.TextTertiary,
            modifier = Modifier.padding(end = 12.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = feature.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (feature.included) AuroraColors.TextPrimary else AuroraColors.TextTertiary,
                fontWeight = if (feature.included) FontWeight.Medium else FontWeight.Normal
            )

            if (feature.description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuroraColors.TextSecondary
                )
            }
        }
    }
}

private data class TierData(
    val name: String,
    val icon: String,
    val tagline: String,
    val price: String,
    val features: List<TierFeature>
)

private data class TierFeature(
    val title: String,
    val description: String? = null,
    val included: Boolean = true
)

private fun getTierData(tier: UserTier): TierData {
    return when (tier) {
        UserTier.SCOUTER -> TierData(
            name = "SCOUTER",
            icon = "🔍",
            tagline = "Perfect for getting started",
            price = "₹0 / month",
            features = listOf(
                TierFeature(
                    title = "One-tap Share Sheet saving",
                    description = "Capture: Save videos effortlessly"
                ),
                TierFeature(
                    title = "Save up to 50 items",
                    description = "Limit: Enough to curate your favorites"
                ),
                TierFeature(
                    title = "Basic Manual Collections (Max 3)",
                    description = "Org: Organize with simple folders"
                ),
                TierFeature(
                    title = "Basic Search",
                    description = "Find your videos with text search"
                ),
                TierFeature(
                    title = "AI Magic",
                    description = "Auto-tagging & Topic Clustering",
                    included = false
                ),
                TierFeature(
                    title = "Cloud Sync",
                    description = "Cross-platform access",
                    included = false
                ),
                TierFeature(
                    title = "Advanced Search & Filtering",
                    included = false
                )
            )
        )
        UserTier.PRODUCER -> TierData(
            name = "PRODUCER",
            icon = "🎬",
            tagline = "For serious content curators",
            price = "₹499 / year",
            features = listOf(
                TierFeature(
                    title = "EVERYTHING IN SCOUTER",
                    description = "All free features included"
                ),
                TierFeature(
                    title = "Save up to 1,000 items",
                    description = "Limit: Build a substantial library"
                ),
                TierFeature(
                    title = "Basic Manual Collections (Max 10)",
                    description = "Org: Better organization options"
                ),
                TierFeature(
                    title = "Advanced Search & Filtering",
                    description = "Management: Find anything instantly"
                ),
                TierFeature(
                    title = "Smart Daily Nudge notifications",
                    description = "Retention: Never forget your saves"
                ),
                TierFeature(
                    title = "AI Magic",
                    description = "Auto-tagging & Topic Clustering",
                    included = false
                ),
                TierFeature(
                    title = "Cloud Sync",
                    description = "Cross-platform access",
                    included = false
                ),
                TierFeature(
                    title = "Public Boards",
                    description = "Share collections publicly",
                    included = false
                )
            )
        )
        UserTier.ICON -> TierData(
            name = "ICON",
            icon = "⭐",
            tagline = "Ultimate power user experience",
            price = "₹499 / month",
            features = listOf(
                TierFeature(
                    title = "EVERYTHING IN PRODUCER",
                    description = "All creator features included"
                ),
                TierFeature(
                    title = "AI Magic: Auto-tagging & Topic Clustering",
                    description = "Smart organization without lifting a finger"
                ),
                TierFeature(
                    title = "Cloud Sync: Cross-platform access",
                    description = "Access your vault from anywhere"
                ),
                TierFeature(
                    title = "Public Boards: Share your collections",
                    description = "Share a link to any collection"
                ),
                TierFeature(
                    title = "Integrations: Export to Notion",
                    description = "Seamlessly connect with your workflow"
                ),
                TierFeature(
                    title = "Archive: Metadata preservation",
                    description = "Save metadata even if video gets deleted"
                ),
                TierFeature(
                    title = "Unlimited saves",
                    description = "No limits on your creativity"
                )
            )
        )
    }
}


