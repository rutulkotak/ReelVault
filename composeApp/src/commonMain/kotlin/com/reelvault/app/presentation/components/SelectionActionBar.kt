package com.reelvault.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.reelvault.app.presentation.theme.AuroraColors

/**
 * SelectionActionBar Component
 * A bottom bar that appears when items are selected.
 * Provides actions like "Delete" and "Clear Selection".
 *
 * @param selectedCount Number of selected items
 * @param onDeleteClicked Callback when delete button is clicked
 * @param onClearSelection Callback when clear selection is clicked
 * @param isVisible Whether the bar should be visible
 * @param modifier Optional modifier
 */
@Composable
fun SelectionActionBar(
    selectedCount: Int,
    onDeleteClicked: () -> Unit,
    onClearSelection: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    color = AuroraColors.DeepIndigo.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = AuroraColors.SoftViolet.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selected count and clear button
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClearSelection) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear selection",
                        tint = AuroraColors.TextSecondary
                    )
                }
                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium,
                    color = AuroraColors.TextPrimary
                )
            }

            // Delete button
            Button(
                onClick = onDeleteClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuroraColors.ErrorRed.copy(alpha = 0.2f),
                    contentColor = AuroraColors.ErrorRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
