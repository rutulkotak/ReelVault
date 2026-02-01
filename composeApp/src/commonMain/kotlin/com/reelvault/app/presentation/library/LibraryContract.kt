package com.reelvault.app.presentation.library

import com.reelvault.app.domain.model.Reel
import com.reelvault.app.presentation.base.MviContract

/**
 * MVI Contract for the Library feature.
 * Defines the State, Intent, and Effect for the Library screen.
 */
object LibraryContract {

    /**
     * UI State representing the current state of the Library screen.
     */
    data class State(
        val isLoading: Boolean = true,
        val isCapturing: Boolean = false,
        val capturingUrl: String? = null,
        val reels: List<Reel> = emptyList(),
        val errorMessage: String? = null,
        val searchQuery: String = "",
        val selectedTags: Set<String> = emptySet(),
        val selectedPlatform: String? = null,  // null = "All"
        val selectedItemIds: Set<String> = emptySet()  // For multi-selection
    ) : MviContract.UiState {

        /**
         * Filtered reels based on search query and selected tags.
         */
        val filteredReels: List<Reel>
            get() = reels.filter { reel ->
                val matchesSearch = searchQuery.isBlank() ||
                    reel.title.contains(searchQuery, ignoreCase = true) ||
                    reel.tags.any { it.contains(searchQuery, ignoreCase = true) }
                val matchesTags = selectedTags.isEmpty() ||
                    reel.tags.any { it in selectedTags }
                val matchesPlatform = selectedPlatform == null ||
                    reel.url.contains(selectedPlatform, ignoreCase = true)
                matchesSearch && matchesTags && matchesPlatform
            }

        /**
         * All unique tags from the reel collection.
         */
        val availableTags: Set<String>
            get() = reels.flatMap { it.tags }.toSet()
    }

    /**
     * User intents that can be dispatched to the ViewModel.
     */
    sealed interface Intent : MviContract.UiIntent {
        data object LoadReels : Intent
        data object Refresh : Intent
        data class SearchQueryChanged(val query: String) : Intent
        data class UpdateSearchQuery(val query: String) : Intent  // Alias for SearchQueryChanged
        data class TagSelected(val tag: String) : Intent
        data class TagDeselected(val tag: String) : Intent
        data object ClearFilters : Intent
        data class FilterByPlatform(val platform: String?) : Intent  // null = "All"
        data class ToggleSelection(val id: String) : Intent
        data object DeleteSelectedItems : Intent
        data class DeleteReel(val reelId: String) : Intent
        data class ReelClicked(val reel: Reel) : Intent

        /**
         * Intent for saving a reel from a shared URL.
         * Triggers metadata scraping and persistence.
         */
        data class SaveReel(val url: String) : Intent
    }

    /**
     * One-time side effects for UI actions.
     */
    sealed interface Effect : MviContract.UiEffect {
        data class ShowError(val message: String) : Effect
        data class NavigateToReelDetail(val reel: Reel) : Effect
        data class ShowDeleteConfirmation(val reelId: String) : Effect
        data object ReelDeleted : Effect
        data class ItemsDeleted(val count: Int) : Effect
        data class OpenUrl(val url: String) : Effect

        /**
         * Effects for Share Sheet capture flow.
         */
        data class ReelSaved(val title: String) : Effect
        data class ReelSaveFailed(val message: String) : Effect
        data object ReelAlreadyExists : Effect
    }
}
