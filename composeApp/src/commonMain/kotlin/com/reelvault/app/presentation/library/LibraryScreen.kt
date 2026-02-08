package com.reelvault.app.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.reelvault.app.data.storage.SharedDataStorage
import com.reelvault.app.presentation.collections.CollectionsScreen
import com.reelvault.app.presentation.components.EmptyLibraryState
import com.reelvault.app.presentation.components.LibraryHeader
import com.reelvault.app.presentation.components.ReelGrid
import com.reelvault.app.presentation.components.SelectionActionBar
import com.reelvault.app.presentation.detail.ReelDetailScreen
import com.reelvault.app.presentation.library.LibraryContract.Intent.UpdateReelDetails
import com.reelvault.app.presentation.settings.SettingsScreen
import com.reelvault.app.presentation.theme.AuroraColors
import com.reelvault.app.presentation.tiers.TierSelectionScreen
import com.reelvault.app.utils.AppLifecycleObserver
import com.reelvault.app.utils.PlatformUrlOpener
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Voyager Screen for the Library feature.
 * Displays the user's saved reels collection with Aurora UI.
 * 
 * @param initialCollectionId Optional ID to filter by on startup.
 */
data class LibraryScreen(val initialCollectionId: Long? = null) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: LibraryViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val navigator = LocalNavigator.currentOrThrow
        val sharedDataStorage: SharedDataStorage = koinInject()

        // Observe app lifecycle - this state changes when app resumes
        val resumeTrigger by AppLifecycleObserver.observeResume()

        // Check for pending shared URLs whenever the app resumes
        // This runs on initial load AND when activity resumes from background
        LaunchedEffect(resumeTrigger) {
            val pendingUrl = sharedDataStorage.getPendingUrl()
            if (pendingUrl != null) {
                viewModel.onIntent(LibraryContract.Intent.SaveReel(pendingUrl))
            }
        }

        // Handle initial filter
        LaunchedEffect(initialCollectionId) {
            if (initialCollectionId != null) {
                viewModel.onIntent(LibraryContract.Intent.FilterByCollection(initialCollectionId))
            }
        }

        // Handle side effects
        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    is LibraryContract.Effect.ShowError -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                    is LibraryContract.Effect.OpenUrl -> {
                        PlatformUrlOpener.openUrl(effect.url)
                    }
                    is LibraryContract.Effect.NavigateToReelDetail -> {
                        // Navigate to detail screen
                        navigator.push(
                            ReelDetailScreen(
                                reel = effect.reel,
                                collections = state.collections, // Fix: Pass collections from ViewModel state
                                onSave = { title, notes, tags, collectionId ->
                                    viewModel.onIntent(
                                        UpdateReelDetails(
                                            id = effect.reel.id,
                                            title = title,
                                            notes = notes,
                                            tags = tags,
                                            collectionId = collectionId
                                        )
                                    )
                                    navigator.pop()
                                }
                            )
                        )
                    }
                    is LibraryContract.Effect.ShowDeleteConfirmation -> {
                        // Future: Show delete dialog
                    }
                    is LibraryContract.Effect.ReelDeleted -> {
                        snackbarHostState.showSnackbar("Reel deleted")
                    }
                    is LibraryContract.Effect.ItemsDeleted -> {
                        snackbarHostState.showSnackbar("${effect.count} item(s) deleted")
                    }
                    is LibraryContract.Effect.ReelSaved -> {
                        snackbarHostState.showSnackbar("✅ Saved: ${effect.title}")
                    }
                    is LibraryContract.Effect.ReelSaveFailed -> {
                        snackbarHostState.showSnackbar("❌ Failed: ${effect.message}")
                    }
                    is LibraryContract.Effect.ReelAlreadyExists -> {
                        snackbarHostState.showSnackbar("📌 Reel already saved")
                    }
                    is LibraryContract.Effect.ReelDetailsUpdated -> {
                        snackbarHostState.showSnackbar("✅ Updated: ${effect.title}")
                    }
                    is LibraryContract.Effect.ReelCollectionUpdated -> {
                        snackbarHostState.showSnackbar("✅ Collection updated")
                    }
                    is LibraryContract.Effect.ReelsMovedToCollection -> {
                        snackbarHostState.showSnackbar("✅ ${effect.count} reel(s) moved")
                    }

                    is LibraryContract.Effect.CollectionLimitReached -> {
                        val result = snackbarHostState.showSnackbar(
                            message = "🔒 Collection limit reached (${effect.maxCollections}). Upgrade to save more!",
                            actionLabel = "UPGRADE",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            navigator.push(TierSelectionScreen())
                        }
                    }
                    is LibraryContract.Effect.ReelLimitReached -> {
                        val result = snackbarHostState.showSnackbar(
                            message = "🔒 Reel limit reached (${effect.maxReels}). Upgrade to save more!",
                            actionLabel = "UPGRADE",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            navigator.push(TierSelectionScreen())
                        }
                    }
                    is LibraryContract.Effect.UpgradeRequired -> {
                        val result = snackbarHostState.showSnackbar(
                            message = "🔒 ${effect.feature} requires ${effect.requiredTier.name} tier",
                            actionLabel = "UPGRADE",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            navigator.push(TierSelectionScreen(highlightTier = effect.requiredTier))
                        }
                    }
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
                        val title = if (state.selectedCollectionId != null) {
                            state.collections.find { it.id == state.selectedCollectionId }?.name ?: "Collection"
                        } else {
                            "ReelVault"
                        }
                        Text(
                            title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = AuroraColors.TextPrimary
                        )
                    },
                    navigationIcon = {
                        if (state.selectedCollectionId != null) {
                            IconButton(onClick = { 
                                viewModel.onIntent(LibraryContract.Intent.FilterByCollection(null)) 
                            }) {
                                Text("🔙", style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                    },
                    actions = {
                        // Collections button
                        IconButton(onClick = { navigator.push(CollectionsScreen()) }) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Collections",
                                tint = AuroraColors.SoftViolet
                            )
                        }
                        // Settings button
                        IconButton(onClick = { navigator.push(SettingsScreen()) }) {
                            Text(
                                text = "⚙️",
                                style = MaterialTheme.typography.headlineSmall
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
            LibraryContent(
                state = state,
                onIntent = viewModel::onIntent,
                navigator = navigator,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

/**
 * Main content area for the Library screen.
 */
@Composable
private fun LibraryContent(
    state: LibraryContract.State,
    onIntent: (LibraryContract.Intent) -> Unit,
    navigator: cafe.adriel.voyager.navigator.Navigator,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Search and Filters (always visible when we have reels)
            if (!state.isLoading && state.reels.isNotEmpty()) {
                LibraryHeader(
                    searchQuery = state.searchQuery,
                    onSearchQueryChange = { query ->
                        onIntent(LibraryContract.Intent.UpdateSearchQuery(query))
                    },
                    selectedPlatform = state.selectedPlatform,
                    onPlatformSelected = { platform ->
                        onIntent(LibraryContract.Intent.FilterByPlatform(platform))
                    },
                    resultsCount = state.filteredReels.size
                )
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading -> {
                        LoadingState()
                    }
                    state.errorMessage != null -> {
                        ErrorState(
                            message = state.errorMessage,
                            onRetry = { onIntent(LibraryContract.Intent.Refresh) }
                        )
                    }
                    state.filteredReels.isEmpty() -> {
                        EmptyLibraryState()
                    }
                    else -> {
                        ReelGrid(
                            reels = state.filteredReels,
                            onReelThumbnailClick = { reel ->
                                // Thumbnail click: Open in external app or toggle selection
                                if (state.selectedItemIds.isNotEmpty()) {
                                    onIntent(LibraryContract.Intent.ToggleSelection(reel.id))
                                } else {
                                    onIntent(LibraryContract.Intent.ReelClicked(reel))
                                }
                            },
                            onReelContentClick = { reel ->
                                // Content click: Navigate to detail screen or toggle selection
                                if (state.selectedItemIds.isNotEmpty()) {
                                    onIntent(LibraryContract.Intent.ToggleSelection(reel.id))
                                } else {
                                    onIntent(LibraryContract.Intent.NavigateToDetail(reel))
                                }
                            },
                            selectedItemIds = state.selectedItemIds,
                            onReelLongClick = { reel ->
                                onIntent(LibraryContract.Intent.ToggleSelection(reel.id))
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Selection Action Bar (Bottom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            SelectionActionBar(
                selectedCount = state.selectedItemIds.size,
                onDeleteClicked = {
                    onIntent(LibraryContract.Intent.DeleteSelectedItems)
                },
                onClearSelection = {
                    // Clear all selections
                    state.selectedItemIds.forEach { id ->
                        onIntent(LibraryContract.Intent.ToggleSelection(id))
                    }
                },
                isVisible = state.selectedItemIds.isNotEmpty()
            )
        }

        // Overlay for capturing state
        if (state.isCapturing) {
            CapturingOverlay(url = state.capturingUrl)
        }
    }
}

/**
 * Loading state with Aurora-themed spinner.
 */
@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = AuroraColors.SoftViolet,
            trackColor = AuroraColors.LightCharcoal
        )
    }
}

/**
 * Error state with retry button.
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            color = AuroraColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = AuroraColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        TextButton(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "Try Again",
                color = AuroraColors.BrightIndigo
            )
        }
    }
}

/**
 * Overlay shown when capturing/saving a shared reel.
 * Displays "Capturing..." with a progress indicator.
 */
@Composable
private fun CapturingOverlay(
    url: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AuroraColors.DeepIndigo
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = AuroraColors.SoftViolet,
                    trackColor = AuroraColors.LightCharcoal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Capturing...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AuroraColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fetching metadata",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuroraColors.TextSecondary
                )
                if (url != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodySmall,
                        color = AuroraColors.TextTertiary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}