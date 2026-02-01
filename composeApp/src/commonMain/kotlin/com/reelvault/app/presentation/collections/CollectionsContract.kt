package com.reelvault.app.presentation.collections

import com.reelvault.app.domain.model.Collection
import com.reelvault.app.presentation.base.MviContract

/**
 * MVI Contract for the Collections feature.
 * Defines the State, Intent, and Effect for the Collections screen.
 */
object CollectionsContract {

    /**
     * UI State representing the current state of the Collections screen.
     */
    data class State(
        val isLoading: Boolean = true,
        val collections: List<Collection> = emptyList(),
        val errorMessage: String? = null
    ) : MviContract.UiState

    /**
     * User intents that can be dispatched to the ViewModel.
     */
    sealed interface Intent : MviContract.UiIntent {
        data object LoadCollections : Intent
        data class CreateCollection(val name: String, val color: String, val icon: String) : Intent
        data class DeleteCollection(val collectionId: Long) : Intent
        data class CollectionClicked(val collection: Collection) : Intent
    }

    /**
     * One-time side effects for UI actions.
     */
    sealed interface Effect : MviContract.UiEffect {
        data class ShowError(val message: String) : Effect
        data class CollectionCreated(val name: String) : Effect
        data class NavigateToCollectionDetail(val collection: Collection) : Effect
    }
}
