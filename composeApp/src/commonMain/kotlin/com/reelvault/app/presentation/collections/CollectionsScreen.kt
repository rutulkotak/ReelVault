package com.reelvault.app.presentation.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.reelvault.app.domain.model.Collection
import com.reelvault.app.presentation.base.ObserveEffect
import com.reelvault.app.presentation.library.LibraryScreen
import com.reelvault.app.presentation.theme.AuroraColors
import com.reelvault.app.presentation.tiers.TierSelectionScreen
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

/**
 * CollectionsScreen - Displays all collections and allows management.
 * Users can create, view, and delete collections.
 */
class CollectionsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: CollectionsViewModel = koinViewModel()
        val state by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        var showCreateDialog by remember { mutableStateOf(false) }

        // Handle effects
        ObserveEffect(viewModel.effect) { effect ->
            when (effect) {
                is CollectionsContract.Effect.ShowError -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(effect.message)
                    }
                }
                is CollectionsContract.Effect.CollectionCreated -> {
                    scope.launch {
                        snackBarHostState.showSnackbar("Collection '${effect.name}' created")
                    }
                }
                is CollectionsContract.Effect.NavigateToCollectionDetail -> {
                    // Navigate to Library with filter
                    // We use replaceAll to reset stack and ensure LibraryScreen handles the filter
                    navigator.replaceAll(LibraryScreen(initialCollectionId = effect.collection.id))
                }
                is CollectionsContract.Effect.CollectionLimitReached -> {
                    scope.launch {
                        val result = snackBarHostState.showSnackbar(
                            message = "🔒 Collection limit reached (${effect.maxCollections}). Upgrade to create more!",
                            actionLabel = "UPGRADE",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            navigator.push(TierSelectionScreen())
                        }
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Collections") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AuroraColors.DarkCharcoal,
                        titleContentColor = AuroraColors.TextPrimary
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Text("🔙", style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = AuroraColors.SoftViolet
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Collection",
                        tint = Color.White
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackBarHostState) },
            containerColor = AuroraColors.DarkCharcoal
        ) { paddingValues ->
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AuroraColors.SoftViolet)
                    }
                }
                state.collections.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No collections yet\nTap + to create one",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AuroraColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.collections) { collection ->
                            CollectionCard(
                                collection = collection,
                                onClick = {
                                    viewModel.onIntent(
                                        CollectionsContract.Intent.CollectionClicked(collection)
                                    )
                                },
                                onDelete = {
                                    viewModel.onIntent(
                                        CollectionsContract.Intent.DeleteCollection(collection.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // Create Collection Dialog
        if (showCreateDialog) {
            CreateCollectionDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, color, icon ->
                    viewModel.onIntent(
                        CollectionsContract.Intent.CreateCollection(name, color, icon)
                    )
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun CollectionCard(
    collection: Collection,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AuroraColors.MediumCharcoal
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon with color
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = parseHexColor(collection.color),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = collection.icon,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = collection.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = AuroraColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${collection.reelCount} reels",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuroraColors.TextSecondary
                )
            }

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = AuroraColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, color: String, icon: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF6B9D") }
    var selectedIcon by remember { mutableStateOf("📁") }

    val colorOptions = listOf(
        "#FF6B9D", "#6B9DFF", "#9DFF6B", "#FFD76B", "#B76BFF", "#6BFFD7"
    )
    val iconOptions = listOf("📁", "💪", "🍳", "🎬", "🎨", "📚", "✈️", "💼")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Collection") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Color picker
                Text("Color", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    colorOptions.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(parseHexColor(color))
                                .clickable { selectedColor = color }
                        )
                    }
                }

                // Icon picker
                Text("Icon", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    iconOptions.forEach { icon ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (icon == selectedIcon) AuroraColors.SoftViolet else AuroraColors.MediumCharcoal)
                                .clickable { selectedIcon = icon },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = icon, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, selectedColor, selectedIcon) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper function to parse hex color strings
private fun parseHexColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    val color = cleanHex.toLongOrNull(16) ?: 0xFF6B9D
    return Color(0xFF000000 or color)
}
