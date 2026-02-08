package com.reelvault.app.presentation.library

import androidx.lifecycle.viewModelScope
import com.reelvault.app.domain.featuregate.FeatureGate
import com.reelvault.app.domain.model.Reel
import com.reelvault.app.domain.usecase.DeleteReelsUseCase
import com.reelvault.app.domain.usecase.GetCollectionsUseCase
import com.reelvault.app.domain.usecase.GetSavedReelsUseCase
import com.reelvault.app.domain.usecase.MoveReelsToCollectionUseCase
import com.reelvault.app.domain.usecase.SaveReelFromUrlUseCase
import com.reelvault.app.domain.usecase.UpdateReelDetailsUseCase
import com.reelvault.app.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val saveReelFromUrlUseCase: SaveReelFromUrlUseCase,
    private val deleteReelsUseCase: DeleteReelsUseCase,
    private val updateReelDetailsUseCase: UpdateReelDetailsUseCase,
    private val moveReelsToCollectionUseCase: MoveReelsToCollectionUseCase,
    private val featureGate: FeatureGate
) : BaseViewModel<LibraryContract.State, LibraryContract.Intent, LibraryContract.Effect>(
    initialState = LibraryContract.State()
) {

    init {
        initializeFeatureGateState()
        loadData()
    }

    /**
     * Initialize state with feature gate properties.
     */
    private fun initializeFeatureGateState() {
        updateState {
            copy(
                userTier = featureGate.currentTier,
                hasAIAccess = featureGate.hasAIAccess,
                hasCloudAccess = featureGate.hasCloudAccess,
                hasAdvancedSearchAccess = featureGate.hasAdvancedSearchAccess
            )
        }
    }

    override fun onIntent(intent: LibraryContract.Intent) {
        when (intent) {
            is LibraryContract.Intent.LoadReels -> loadData()
            is LibraryContract.Intent.Refresh -> loadData()
            is LibraryContract.Intent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is LibraryContract.Intent.UpdateSearchQuery -> onSearchQueryChanged(intent.query)
            is LibraryContract.Intent.TagSelected -> onTagSelected(intent.tag)
            is LibraryContract.Intent.TagDeselected -> onTagDeselected(intent.tag)
            is LibraryContract.Intent.ClearFilters -> onClearFilters()
            is LibraryContract.Intent.FilterByPlatform -> onFilterByPlatform(intent.platform)
            is LibraryContract.Intent.ToggleSelection -> onToggleSelection(id = intent.id)
            is LibraryContract.Intent.DeleteSelectedItems -> onDeleteSelectedItems()
            is LibraryContract.Intent.DeleteReel -> onDeleteReel(intent.reelId)
            is LibraryContract.Intent.ReelClicked -> onReelClicked(intent.reel)
            is LibraryContract.Intent.SaveReel -> onSaveReel(intent.url)
            is LibraryContract.Intent.FilterByCollection -> onFilterByCollection(intent.collectionId)
            is LibraryContract.Intent.UpdateReelDetails -> onUpdateReelDetails(
                intent.id, intent.title, intent.notes, intent.tags, intent.collectionId
            )
            is LibraryContract.Intent.UpdateReelCollection -> onUpdateReelCollection(intent.reelId, intent.collectionId)
            is LibraryContract.Intent.MoveToCollection -> onMoveToCollection(intent.reelIds, intent.collectionId)
            is LibraryContract.Intent.NavigateToDetail -> onNavigateToDetail(intent.reel)
        }
    }

    private fun loadData() {
        combine(
            getSavedReelsUseCase(),
            getCollectionsUseCase()
        ) { reels, collections ->
            reels to collections
        }
        .onStart {
            updateState { copy(isLoading = true, errorMessage = null) }
        }
        .onEach { (reels, collections) ->
            updateState {
                copy(
                    isLoading = false,
                    reels = reels,
                    collections = collections,
                    canSaveMoreReels = featureGate.canSaveReel(reels.size),
                    canCreateMoreCollections = featureGate.canCreateCollection(collections.size),
                    remainingReelSaves = featureGate.remainingReelSaves(reels.size),
                    remainingCollections = featureGate.remainingCollections(collections.size)
                )
            }
        }
        .catch { throwable ->
            updateState {
                copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "Failed to load library data"
                )
            }
            emitEffect(LibraryContract.Effect.ShowError(
                throwable.message ?: "Failed to load library data"
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
     * Checks feature gate limits before attempting to save.
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
                is SaveReelFromUrlUseCase.SaveResult.LimitReached -> {
                    updateState { copy(isCapturing = false, capturingUrl = null) }
                    emitEffect(LibraryContract.Effect.ReelLimitReached(result.maxReels))
                }
            }
        }
    }

    private fun onFilterByCollection(collectionId: Long?) {
        updateState { copy(selectedCollectionId = collectionId) }
    }

    private fun onUpdateReelDetails(
        id: String,
        title: String,
        notes: String?,
        tags: List<String>,
        collectionId: Long?
    ) {
        viewModelScope.launch {
            val result = updateReelDetailsUseCase(id, title, notes, tags, collectionId)
            if (result.isSuccess) {
                emitEffect(LibraryContract.Effect.ReelDetailsUpdated(title))
            } else {
                emitEffect(LibraryContract.Effect.ShowError(
                    "Failed to update reel: ${result.exceptionOrNull()?.message}"
                ))
            }
        }
    }

    private fun onMoveToCollection(reelIds: List<String>, collectionId: Long?) {
        viewModelScope.launch {
            val result = moveReelsToCollectionUseCase(reelIds, collectionId)
            if (result.isSuccess) {
                updateState { copy(selectedItemIds = emptySet()) }
                emitEffect(LibraryContract.Effect.ReelsMovedToCollection(reelIds.size))
            } else {
                emitEffect(LibraryContract.Effect.ShowError(
                    "Failed to move reels: ${result.exceptionOrNull()?.message}"
                ))
            }
        }
    }

    /**
     * Handle updating a single reel's collection assignment.
     * This is used when the user changes collection from the detail screen.
     */
    private fun onUpdateReelCollection(reelId: String, collectionId: Long?) {
        viewModelScope.launch {
            val result = moveReelsToCollectionUseCase(listOf(reelId), collectionId)
            if (result.isSuccess) {
                emitEffect(LibraryContract.Effect.ReelCollectionUpdated(reelId))
            } else {
                emitEffect(LibraryContract.Effect.ShowError(
                    "Failed to update collection: ${result.exceptionOrNull()?.message}"
                ))
            }
        }
    }

    private fun onNavigateToDetail(reel: Reel) {
        emitEffect(LibraryContract.Effect.NavigateToReelDetail(reel))
    }
}