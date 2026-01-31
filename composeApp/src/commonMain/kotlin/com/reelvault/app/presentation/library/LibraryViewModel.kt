package com.reelvault.app.presentation.library

import androidx.lifecycle.viewModelScope
import com.reelvault.app.domain.model.Reel
import com.reelvault.app.domain.usecase.GetSavedReelsUseCase
import com.reelvault.app.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * ViewModel for the Library screen.
 * Implements MVI pattern with State, Intent, and Effect.
 */
class LibraryViewModel(
    private val getSavedReelsUseCase: GetSavedReelsUseCase
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
            is LibraryContract.Intent.TagSelected -> onTagSelected(intent.tag)
            is LibraryContract.Intent.TagDeselected -> onTagDeselected(intent.tag)
            is LibraryContract.Intent.ClearFilters -> onClearFilters()
            is LibraryContract.Intent.DeleteReel -> onDeleteReel(intent.reelId)
            is LibraryContract.Intent.ReelClicked -> onReelClicked(intent.reel)
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
        updateState { copy(searchQuery = "", selectedTags = emptySet()) }
    }

    private fun onDeleteReel(reelId: String) {
        emitEffect(LibraryContract.Effect.ShowDeleteConfirmation(reelId))
    }

    private fun onReelClicked(reel: Reel) {
        emitEffect(LibraryContract.Effect.OpenUrl(reel.url))
    }
}
