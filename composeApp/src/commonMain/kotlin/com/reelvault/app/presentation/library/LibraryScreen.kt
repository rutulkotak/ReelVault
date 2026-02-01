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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.reelvault.app.presentation.components.EmptyLibraryState
import com.reelvault.app.presentation.components.LibraryHeader
import com.reelvault.app.presentation.components.ReelGrid
import com.reelvault.app.presentation.components.SelectionActionBar
import com.reelvault.app.presentation.settings.SettingsScreen
import com.reelvault.app.presentation.theme.AuroraColors
import com.reelvault.app.utils.PlatformUrlOpener
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * Voyager Screen for the Library feature.
 * Displays the user's saved reels collection with Aurora UI.
 */
class LibraryScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: LibraryViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val navigator = LocalNavigator.current

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
                        // Future: Navigate to detail screen
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
                            "ReelVault",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AuroraColors.TextPrimary
                        )
                    },
                    actions = {
                        IconButton(onClick = { navigator?.push(SettingsScreen()) }) {
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
                            onReelClick = { reel ->
                                // If in selection mode, toggle selection, otherwise open URL
                                if (state.selectedItemIds.isNotEmpty()) {
                                    onIntent(LibraryContract.Intent.ToggleSelection(reel.id))
                                } else {
                                    onIntent(LibraryContract.Intent.ReelClicked(reel))
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
