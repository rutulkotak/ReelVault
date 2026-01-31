package com.reelvault.app.presentation.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

/**
 * Voyager Screen for the Library feature.
 * Displays the user's saved reels collection.
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
                    is LibraryContract.Effect.NavigateToReelDetail -> {
                        // TODO: Navigate to reel detail screen
                    }
                    is LibraryContract.Effect.ShowDeleteConfirmation -> {
                        // TODO: Show delete confirmation dialog
                    }
                    is LibraryContract.Effect.ReelDeleted -> {
                        snackbarHostState.showSnackbar("Reel deleted")
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Library") }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            LibraryContent(
                state = state,
                onIntent = viewModel::onIntent,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

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
                CircularProgressIndicator()
            }
            state.errorMessage != null -> {
                ErrorContent(
                    message = state.errorMessage,
                    onRetry = { onIntent(LibraryContract.Intent.Refresh) }
                )
            }
            state.filteredReels.isEmpty() -> {
                EmptyLibraryContent()
            }
            else -> {
                ReelGrid(
                    reels = state.filteredReels,
                    onReelClick = { reel -> onIntent(LibraryContract.Intent.ReelClicked(reel)) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ReelGrid(
    reels: List<com.reelvault.app.domain.model.Reel>,
    onReelClick: (com.reelvault.app.domain.model.Reel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = reels,
            key = { it.id }
        ) { reel ->
            ReelGridItem(
                reel = reel,
                onClick = { onReelClick(reel) }
            )
        }
    }
}

@Composable
private fun ReelGridItem(
    reel: com.reelvault.app.domain.model.Reel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Implement reel card UI with thumbnail, title, and tags
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = reel.title,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun EmptyLibraryContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Your library is empty",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Save reels to see them here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        // TODO: Add retry button
    }
}
