package com.reelvault.app.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 16f)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    KamelImage(
                        resource = asyncPainterResource(data = reel.thumbnail),
                        contentDescription = reel.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onLoading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AuroraColors.MediumCharcoal),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = AuroraColors.SoftViolet)
                            }
                        }
                    )
                }

                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    enabled = isEditing,
                    modifier = Modifier.fillMaxWidth(),
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

                // Notes Field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    enabled = isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
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

                // Collection Picker
                OutlinedButton(
                    onClick = { if (isEditing) showCollectionPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isEditing,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AuroraColors.TextPrimary
                    )
                ) {
                    Text(
                        text = collections.find { it.id == selectedCollectionId }?.name
                            ?: "No Collection",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Collection Picker Dialog
        if (showCollectionPicker) {
            AlertDialog(
                onDismissRequest = { showCollectionPicker = false },
                title = { Text("Select Collection") },
                text = {
                    Column {
                        // None option
                        TextButton(
                            onClick = {
                                selectedCollectionId = null
                                showCollectionPicker = false
                            }
                        ) {
                            Text("None")
                        }

                        // Collections
                        collections.forEach { collection ->
                            TextButton(
                                onClick = {
                                    selectedCollectionId = collection.id
                                    showCollectionPicker = false
                                }
                            ) {
                                Text(collection.name)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCollectionPicker = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
