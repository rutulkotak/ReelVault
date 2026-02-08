package com.reelvault.app.presentation.collections

import androidx.lifecycle.viewModelScope
import com.reelvault.app.domain.featuregate.FeatureGate
import com.reelvault.app.domain.model.Collection
import com.reelvault.app.domain.usecase.CreateCollectionUseCase
import com.reelvault.app.domain.usecase.DeleteCollectionUseCase
import com.reelvault.app.domain.usecase.GetCollectionsUseCase
import com.reelvault.app.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * ViewModel for the Collections screen.
 * Implements MVI pattern with State, Intent, and Effect.
 */
class CollectionsViewModel(
    private val getCollectionsUseCase: GetCollectionsUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
    private val featureGate: FeatureGate
) : BaseViewModel<CollectionsContract.State, CollectionsContract.Intent, CollectionsContract.Effect>(
    initialState = CollectionsContract.State()
) {

    init {
        loadCollections()
    }

    override fun onIntent(intent: CollectionsContract.Intent) {
        when (intent) {
            is CollectionsContract.Intent.LoadCollections -> loadCollections()
            is CollectionsContract.Intent.CreateCollection -> onCreateCollection(
                intent.name, intent.color, intent.icon
            )
            is CollectionsContract.Intent.DeleteCollection -> onDeleteCollection(intent.collectionId)
            is CollectionsContract.Intent.CollectionClicked -> onCollectionClicked(intent.collection)
        }
    }

    private fun loadCollections() {
        getCollectionsUseCase()
            .onStart {
                updateState { copy(isLoading = true, errorMessage = null) }
            }
            .onEach { collections ->
                updateState { copy(isLoading = false, collections = collections) }
            }
            .catch { throwable ->
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load collections"
                    )
                }
                emitEffect(CollectionsContract.Effect.ShowError(
                    throwable.message ?: "Failed to load collections"
                ))
            }
            .launchIn(viewModelScope)
    }

    private fun onCreateCollection(name: String, color: String, icon: String) {
        // Check if user can create more collections
        if (!featureGate.canCreateCollection(currentState.collections.size)) {
            featureGate.maxCollections?.let { maxCollections ->
                emitEffect(CollectionsContract.Effect.CollectionLimitReached(maxCollections))
            }
            return
        }

        viewModelScope.launch {
            val result = createCollectionUseCase(name, color, icon)
            if (result.isSuccess) {
                emitEffect(CollectionsContract.Effect.CollectionCreated(name))
            } else {
                emitEffect(CollectionsContract.Effect.ShowError(
                    "Failed to create collection: ${result.exceptionOrNull()?.message}"
                ))
            }
        }
    }

    private fun onDeleteCollection(collectionId: Long) {
        viewModelScope.launch {
            val result = deleteCollectionUseCase(collectionId)
            if (result.isFailure) {
                emitEffect(CollectionsContract.Effect.ShowError(
                    "Failed to delete collection: ${result.exceptionOrNull()?.message}"
                ))
            }
        }
    }

    private fun onCollectionClicked(collection: Collection) {
        emitEffect(CollectionsContract.Effect.NavigateToCollectionDetail(collection))
    }
}
