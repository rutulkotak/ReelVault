package com.reelvault.app.presentation.library

import androidx.lifecycle.viewModelScope
import com.reelvault.app.domain.model.Reel
import com.reelvault.app.domain.usecase.DeleteReelsUseCase
import com.reelvault.app.domain.usecase.GetSavedReelsUseCase
import com.reelvault.app.domain.usecase.SaveReelFromUrlUseCase
import com.reelvault.app.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * ViewModel for the Library screen.
 * Implements MVI pattern with State, Intent, and Effect.
 */
class LibraryViewModel(
    private val getSavedReelsUseCase: GetSavedReelsUseCase,
    private val saveReelFromUrlUseCase: SaveReelFromUrlUseCase,
    private val deleteReelsUseCase: DeleteReelsUseCase
) : BaseViewModel<LibraryContract.State, LibraryContract.Intent, LibraryContract.Effect>(
    initialState = LibraryContract.State()
) {

    init {
        loadReels()
    }

    override fun onIntent(intent: LibraryContract.Intent) {
        when (intent) {
            is LibraryContract.Intent.LoadReels -> loadReels()
            is LibraryContract.Intent.Refresh -> loadReels()
            is LibraryContract.Intent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is LibraryContract.Intent.UpdateSearchQuery -> onSearchQueryChanged(intent.query)
            is LibraryContract.Intent.TagSelected -> onTagSelected(intent.tag)
            is LibraryContract.Intent.TagDeselected -> onTagDeselected(intent.tag)
            is LibraryContract.Intent.ClearFilters -> onClearFilters()
            is LibraryContract.Intent.FilterByPlatform -> onFilterByPlatform(intent.platform)
            is LibraryContract.Intent.ToggleSelection -> onToggleSelection(intent.id)
            is LibraryContract.Intent.DeleteSelectedItems -> onDeleteSelectedItems()
            is LibraryContract.Intent.DeleteReel -> onDeleteReel(intent.reelId)
            is LibraryContract.Intent.ReelClicked -> onReelClicked(intent.reel)
            is LibraryContract.Intent.SaveReel -> onSaveReel(intent.url)
        }
    }

    private fun loadReels() {
        getSavedReelsUseCase()
            .onStart {
                updateState { copy(isLoading = true, errorMessage = null) }
            }
            .onEach { reels ->
                updateState { copy(isLoading = false, reels = reels) }
            }
            .catch { throwable ->
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load reels"
                    )
                }
                emitEffect(LibraryContract.Effect.ShowError(
                    throwable.message ?: "Failed to load reels"
                ))
            }
            .launchIn(viewModelScope)
    }

    private fun onSearchQueryChanged(query: String) {
        updateState { copy(searchQuery = query) }
    }

    private fun onTagSelected(tag: String) {
        updateState { copy(selectedTags = selectedTags + tag) }
    }

    private fun onTagDeselected(tag: String) {
        updateState { copy(selectedTags = selectedTags - tag) }
    }

    private fun onClearFilters() {
        updateState { copy(searchQuery = "", selectedTags = emptySet(), selectedPlatform = null) }
    }

    private fun onFilterByPlatform(platform: String?) {
        updateState { copy(selectedPlatform = platform) }
    }

    private fun onToggleSelection(id: String) {
        updateState {
            val newSelection = if (id in selectedItemIds) {
                selectedItemIds - id
            } else {
                selectedItemIds + id
            }
            copy(selectedItemIds = newSelection)
        }
    }

    private fun onDeleteSelectedItems() {
        val itemsToDelete = currentState.selectedItemIds.toList()
        if (itemsToDelete.isEmpty()) return

        viewModelScope.launch {
            val result = deleteReelsUseCase(itemsToDelete)
            if (result.isSuccess) {
                updateState { copy(selectedItemIds = emptySet()) }
                emitEffect(LibraryContract.Effect.ItemsDeleted(itemsToDelete.size))
            } else {
                emitEffect(LibraryContract.Effect.ShowError(
                    "Failed to delete items: ${result.exceptionOrNull()?.message}"
                ))
            }
        }
    }

    private fun onDeleteReel(reelId: String) {
        emitEffect(LibraryContract.Effect.ShowDeleteConfirmation(reelId))
    }

    private fun onReelClicked(reel: Reel) {
        emitEffect(LibraryContract.Effect.OpenUrl(reel.url))
    }

    /**
     * Handle saving a reel from a shared URL.
     * Shows "Capturing..." state while metadata is being fetched.
     */
    private fun onSaveReel(url: String) {
        viewModelScope.launch {
            // Show capturing state
            updateState { copy(isCapturing = true, capturingUrl = url) }

            when (val result = saveReelFromUrlUseCase(url)) {
                is SaveReelFromUrlUseCase.SaveResult.Success -> {
                    updateState { copy(isCapturing = false, capturingUrl = null) }
                    emitEffect(LibraryContract.Effect.ReelSaved(result.reel.title))
                    // Reels list will automatically update via Flow
                }
                is SaveReelFromUrlUseCase.SaveResult.AlreadyExists -> {
                    updateState { copy(isCapturing = false, capturingUrl = null) }
                    emitEffect(LibraryContract.Effect.ReelAlreadyExists)
                }
                is SaveReelFromUrlUseCase.SaveResult.Error -> {
                    updateState { copy(isCapturing = false, capturingUrl = null) }
                    emitEffect(LibraryContract.Effect.ReelSaveFailed(result.message))
                }
            }
        }
    }
}
