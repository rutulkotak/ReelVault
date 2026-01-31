package com.reelvault.app.presentation.base

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Base interface defining the MVI contract structure.
 * All feature contracts should implement this interface.
 */
interface MviContract<State : MviContract.UiState, Intent : MviContract.UiIntent, Effect : MviContract.UiEffect> {
    
    /**
     * Marker interface for UI State.
     * Represents the current state of the UI.
     */
    interface UiState
    
    /**
     * Marker interface for UI Intent.
     * Represents user actions or events that trigger state changes.
     */
    interface UiIntent
    
    /**
     * Marker interface for UI Effect.
     * Represents one-time side effects (navigation, snackbars, etc.)
     */
    interface UiEffect
}
