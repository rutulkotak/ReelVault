package com.reelvault.app.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.reelvault.app.domain.model.Collection
import com.reelvault.app.domain.model.Reel
import com.reelvault.app.presentation.theme.AuroraColors
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

/**
 * ReelDetailScreen - Detail view for a reel with editing capabilities.
 * Allows editing title, notes, tags, and collection assignment.
 *
 * @param reel The reel to display and edit
 * @param collections Available collections for assignment
 * @param onSave Callback when changes are saved
 */
data class ReelDetailScreen(
    val reel: Reel,
    val collections: List<Collection> = emptyList(),
    val onSave: (String, String?, List<String>, Long?) -> Unit = { _, _, _, _ -> }
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var isEditing by remember { mutableStateOf(false) }
        var title by remember { mutableStateOf(reel.title) }
        var notes by remember { mutableStateOf(reel.notes ?: "") }
        var tags by remember { mutableStateOf(reel.tags.joinToString(", ")) }
        var selectedCollectionId by remember { mutableStateOf(reel.collectionId) }
        var showCollectionPicker by remember { mutableStateOf(false) }

        // Bottom sheet state
        val sheetState = rememberModalBottomSheetState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Reel Details") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        if (isEditing) {
                            IconButton(
                                onClick = {
                                    onSave(
                                        title,
                                        notes.ifBlank { null },
                                        tags.split(",").map { it.trim() }.filter { it.isNotBlank() },
                                        selectedCollectionId
                                    )
                                    isEditing = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save"
                                )
                            }
                        } else {
                            IconButton(onClick = { isEditing = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AuroraColors.DarkCharcoal,
                        titleContentColor = AuroraColors.TextPrimary,
                        navigationIconContentColor = AuroraColors.TextPrimary,
                        actionIconContentColor = AuroraColors.SoftViolet
                    )
                )
            },
            containerColor = AuroraColors.DarkCharcoal
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Compact Preview Header (200dp height)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    KamelImage(
                        resource = asyncPainterResource(data = reel.thumbnail),
                        contentDescription = reel.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onLoading = { _ ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AuroraColors.MediumCharcoal),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = AuroraColors.SoftViolet)
                            }
                        },
                        onFailure = { _ ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AuroraColors.MediumCharcoal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🎬",
                                    style = MaterialTheme.typography.displayMedium
                                )
                            }
                        }
                    )
                }

                // Form Fields - immediately visible
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title Field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        enabled = isEditing,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AuroraColors.SoftViolet,
                            unfocusedBorderColor = AuroraColors.TextSecondary,
                            disabledBorderColor = AuroraColors.TextSecondary,
                            focusedLabelColor = AuroraColors.SoftViolet,
                            unfocusedLabelColor = AuroraColors.TextSecondary,
                            disabledTextColor = AuroraColors.TextPrimary,
                            focusedTextColor = AuroraColors.TextPrimary,
                            unfocusedTextColor = AuroraColors.TextPrimary
                        )
                    )

                    // Collection Picker - Clickable OutlinedBox
                    CollectionPickerBox(
                        selectedCollection = collections.find { it.id == selectedCollectionId },
                        enabled = isEditing,
                        onClick = { if (isEditing) showCollectionPicker = true }
                    )

                    // Notes Field
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        enabled = isEditing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AuroraColors.SoftViolet,
                            unfocusedBorderColor = AuroraColors.TextSecondary,
                            disabledBorderColor = AuroraColors.TextSecondary,
                            focusedLabelColor = AuroraColors.SoftViolet,
                            unfocusedLabelColor = AuroraColors.TextSecondary,
                            disabledTextColor = AuroraColors.TextPrimary,
                            focusedTextColor = AuroraColors.TextPrimary,
                            unfocusedTextColor = AuroraColors.TextPrimary
                        )
                    )

                    // Tags Field
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Tags (comma-separated)") },
                        enabled = isEditing,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("fitness, motivation, workout") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AuroraColors.SoftViolet,
                            unfocusedBorderColor = AuroraColors.TextSecondary,
                            disabledBorderColor = AuroraColors.TextSecondary,
                            focusedLabelColor = AuroraColors.SoftViolet,
                            unfocusedLabelColor = AuroraColors.TextSecondary,
                            disabledTextColor = AuroraColors.TextPrimary,
                            focusedTextColor = AuroraColors.TextPrimary,
                            unfocusedTextColor = AuroraColors.TextPrimary
                        )
                    )

                    // Bottom spacing
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Collection Picker Modal Bottom Sheet
        if (showCollectionPicker) {
            ModalBottomSheet(
                onDismissRequest = { showCollectionPicker = false },
                sheetState = sheetState,
                containerColor = AuroraColors.DeepIndigo,
                contentColor = AuroraColors.TextPrimary
            ) {
                CollectionPickerContent(
                    collections = collections,
                    selectedCollectionId = selectedCollectionId,
                    onCollectionSelected = { collectionId ->
                        selectedCollectionId = collectionId
                        showCollectionPicker = false
                    },
                    onDismiss = { showCollectionPicker = false }
                )
            }
        }
    }
}

/**
 * Collection Picker Box - Clickable OutlinedBox that shows current collection.
 */
@Composable
private fun CollectionPickerBox(
    selectedCollection: Collection?,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = if (enabled) AuroraColors.TextSecondary else AuroraColors.TextSecondary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Collection",
                    style = MaterialTheme.typography.bodySmall,
                    color = AuroraColors.TextSecondary
                )
                Text(
                    text = selectedCollection?.name ?: "No Collection",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedCollection != null) AuroraColors.TextPrimary else AuroraColors.TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (enabled) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select collection",
                    tint = AuroraColors.TextSecondary
                )
            }
        }
    }
}

/**
 * Collection Picker Bottom Sheet Content.
 */
@Composable
private fun CollectionPickerContent(
    collections: List<Collection>,
    selectedCollectionId: Long?,
    onCollectionSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // Header
        Text(
            text = "Select Collection",
            style = MaterialTheme.typography.titleLarge,
            color = AuroraColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        HorizontalDivider(color = AuroraColors.LightCharcoal)

        // None option
        CollectionPickerItem(
            name = "No Collection",
            icon = "📂",
            isSelected = selectedCollectionId == null,
            onClick = { onCollectionSelected(null) }
        )

        // Collections list
        collections.forEach { collection ->
            CollectionPickerItem(
                name = collection.name,
                icon = collection.icon,
                isSelected = collection.id == selectedCollectionId,
                onClick = { onCollectionSelected(collection.id) }
            )
        }

        // Empty state
        if (collections.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No collections yet.\nCreate one from the Collections screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AuroraColors.TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/**
 * Individual collection item in the picker.
 */
@Composable
private fun CollectionPickerItem(
    name: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) AuroraColors.SoftViolet.copy(alpha = 0.2f)
                else androidx.compose.ui.graphics.Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = AuroraColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = AuroraColors.SoftViolet
            )
        }
    }
}
