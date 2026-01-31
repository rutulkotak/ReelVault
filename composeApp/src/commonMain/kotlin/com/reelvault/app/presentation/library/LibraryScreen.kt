package com.reelvault.app.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.reelvault.app.presentation.components.EmptyLibraryState
import com.reelvault.app.presentation.components.ReelGrid
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
        modifier = modifier.fillMaxSize(),
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
                        onIntent(LibraryContract.Intent.ReelClicked(reel))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
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
