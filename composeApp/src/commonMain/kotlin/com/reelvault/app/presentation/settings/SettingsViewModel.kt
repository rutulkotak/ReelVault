package com.reelvault.app.presentation.settings

import androidx.lifecycle.viewModelScope
import com.reelvault.app.data.settings.AppSettings
import com.reelvault.app.domain.usecase.CheckDailyNudgeUseCase
import com.reelvault.app.domain.usecase.GetSavedReelsUseCase
import com.reelvault.app.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for Settings Screen.
 * Manages app settings including Daily Nudge and Heritage Vault.
 */
class SettingsViewModel(
    private val appSettings: AppSettings,
    private val checkDailyNudgeUseCase: CheckDailyNudgeUseCase,
    private val getSavedReelsUseCase: GetSavedReelsUseCase
) : BaseViewModel<SettingsContract.State, SettingsContract.Intent, SettingsContract.Effect>(
    initialState = SettingsContract.State()
) {

    init {
        onIntent(SettingsContract.Intent.LoadSettings)
    }

    override fun onIntent(intent: SettingsContract.Intent) {
        when (intent) {
            is SettingsContract.Intent.LoadSettings -> loadSettings()
            is SettingsContract.Intent.ToggleDailyNudge -> toggleDailyNudge(intent.enabled)
            is SettingsContract.Intent.ToggleHeritageVault -> toggleHeritageVault(intent.enabled)
            is SettingsContract.Intent.RequestNotificationPermission -> requestNotificationPermission()
            is SettingsContract.Intent.OpenHeritageVaultSetup -> openHeritageVaultSetup()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val videoCount = getSavedReelsUseCase().first().size

            updateState {
                copy(
                    isDailyNudgeEnabled = appSettings.isDailyNudgeEnabled,
                    isHeritageVaultEnabled = appSettings.isHeritageVaultEnabled,
                    totalVideosSaved = videoCount
                )
            }
        }
    }

    private fun toggleDailyNudge(enabled: Boolean) {
        viewModelScope.launch {
            appSettings.isDailyNudgeEnabled = enabled
            updateState { copy(isDailyNudgeEnabled = enabled) }

            if (enabled) {
                // Request permissions when enabling
                val granted = checkDailyNudgeUseCase.requestPermissions()
                if (!granted) {
                    emitEffect(SettingsContract.Effect.ShowMessage("Notification permission denied"))
                    appSettings.isDailyNudgeEnabled = false
                    updateState { copy(isDailyNudgeEnabled = false) }
                } else {
                    emitEffect(SettingsContract.Effect.ShowMessage("Daily nudges enabled"))
                }
            } else {
                emitEffect(SettingsContract.Effect.ShowMessage("Daily nudges disabled"))
            }
        }
    }

    private fun toggleHeritageVault(enabled: Boolean) {
        appSettings.isHeritageVaultEnabled = enabled
        updateState { copy(isHeritageVaultEnabled = enabled) }

        if (enabled) {
            emitEffect(SettingsContract.Effect.ShowMessage("Heritage Vault enabled (Coming Soon)"))
        }
    }

    private fun requestNotificationPermission() {
        viewModelScope.launch {
            val granted = checkDailyNudgeUseCase.requestPermissions()
            emitEffect(SettingsContract.Effect.ShowPermissionDialog(granted))
        }
    }

    private fun openHeritageVaultSetup() {
        emitEffect(SettingsContract.Effect.NavigateToHeritageVaultSetup)
    }
}
