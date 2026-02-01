package com.reelvault.app.presentation.settings

import com.reelvault.app.presentation.base.MviContract

/**
 * MVI Contract for Settings Screen.
 */
object SettingsContract {

    data class State(
        val isDailyNudgeEnabled: Boolean = true,
        val isHeritageVaultEnabled: Boolean = false,
        val totalVideosSaved: Int = 0,
        val lastBackupTime: String? = null
    ) : MviContract.UiState

    sealed interface Intent : MviContract.UiIntent {
        data object LoadSettings : Intent
        data class ToggleDailyNudge(val enabled: Boolean) : Intent
        data class ToggleHeritageVault(val enabled: Boolean) : Intent
        data object RequestNotificationPermission : Intent
        data object OpenHeritageVaultSetup : Intent
    }

    sealed interface Effect : MviContract.UiEffect {
        data class ShowMessage(val message: String) : Effect
        data object NavigateToHeritageVaultSetup : Effect
        data class ShowPermissionDialog(val granted: Boolean) : Effect
    }
}
