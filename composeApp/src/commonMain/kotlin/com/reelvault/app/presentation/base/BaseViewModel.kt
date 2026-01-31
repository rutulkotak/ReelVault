package com.reelvault.app.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel implementing the MVI pattern.
 * Provides standard state management, intent handling, and effect emission.
 *
 * @param State The UI state type
 * @param Intent The user intent type
 * @param Effect The side effect type
 * @param initialState The initial state of the UI
 */
abstract class BaseViewModel<State : MviContract.UiState, Intent : MviContract.UiIntent, Effect : MviContract.UiEffect>(
    initialState: State
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    protected val currentState: State
        get() = _uiState.value

    /**
     * Process an incoming intent from the UI.
     * Subclasses must implement this to handle user actions.
     */
    abstract fun onIntent(intent: Intent)

    /**
     * Update the UI state using a reducer function.
     * @param reduce Function that takes current state and returns new state.
     */
    protected fun updateState(reduce: State.() -> State) {
        _uiState.value = currentState.reduce()
    }

    /**
     * Emit a one-time side effect to the UI.
     * @param effect The effect to emit.
     */
    protected fun emitEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
