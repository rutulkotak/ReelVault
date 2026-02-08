package com.reelvault.app.presentation.tiers

import com.reelvault.app.domain.featuregate.UserTier
import com.reelvault.app.presentation.base.MviContract

/**
 * MVI Contract for Tier Selection Screen.
 */
object TierSelectionContract {

    data class State(
        val currentTier: UserTier = UserTier.SCOUTER,
        val selectedPageIndex: Int = 0,
        val isLoading: Boolean = false
    ) : MviContract.UiState

    sealed interface Intent : MviContract.UiIntent {
        data class PageChanged(val index: Int) : Intent
        data class UpgradeToTier(val tier: UserTier) : Intent
        data object DismissScreen : Intent
    }

    sealed interface Effect : MviContract.UiEffect {
        data class ShowMessage(val message: String) : Effect
        data object NavigateBack : Effect
        data class UpgradeSuccess(val tier: UserTier) : Effect
    }
}

