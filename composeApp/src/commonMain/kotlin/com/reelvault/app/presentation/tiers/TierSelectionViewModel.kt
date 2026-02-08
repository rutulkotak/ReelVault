package com.reelvault.app.presentation.tiers

import com.reelvault.app.domain.featuregate.FeatureGate
import com.reelvault.app.domain.featuregate.UserTier
import com.reelvault.app.presentation.base.BaseViewModel

/**
 * ViewModel for Tier Selection Screen.
 * Implements MVI pattern with State, Intent, and Effect.
 */
class TierSelectionViewModel(
    private val featureGate: FeatureGate
) : BaseViewModel<TierSelectionContract.State, TierSelectionContract.Intent, TierSelectionContract.Effect>(
    initialState = TierSelectionContract.State(currentTier = featureGate.currentTier)
) {

    override fun onIntent(intent: TierSelectionContract.Intent) {
        when (intent) {
            is TierSelectionContract.Intent.PageChanged -> onPageChanged(intent.index)
            is TierSelectionContract.Intent.UpgradeToTier -> onUpgradeToTier(intent.tier)
            is TierSelectionContract.Intent.DismissScreen -> onDismissScreen()
        }
    }

    private fun onPageChanged(index: Int) {
        updateState { copy(selectedPageIndex = index) }
    }

    private fun onUpgradeToTier(tier: UserTier) {
        // TODO: Implement actual upgrade logic with payment integration
        // For now, just show a message
        when (tier) {
            UserTier.SCOUTER -> {
                emitEffect(TierSelectionContract.Effect.ShowMessage("You're already on the free tier!"))
            }
            UserTier.PRODUCER -> {
                emitEffect(TierSelectionContract.Effect.ShowMessage("Upgrade to PRODUCER - Coming Soon!"))
            }
            UserTier.ICON -> {
                emitEffect(TierSelectionContract.Effect.ShowMessage("Upgrade to ICON - Coming Soon!"))
            }
        }
    }

    private fun onDismissScreen() {
        emitEffect(TierSelectionContract.Effect.NavigateBack)
    }
}

