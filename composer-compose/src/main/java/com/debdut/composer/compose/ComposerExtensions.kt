package com.debdut.composer.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.debdut.composer.action.holder.UIComposerActionHolder
import com.debdut.composer.composer.data.host.DataComposerHost
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Collect the DataComposerHost's combined UI state as Compose [State].
 *
 * This is the primary way to observe state from a DataComposerHost in
 * Jetpack Compose. The state is collected with lifecycle awareness.
 *
 * ## Example
 * ```kotlin
 * @Composable
 * fun CounterScreen(viewModel: CounterViewModel) {
 *     val states by viewModel.collectAsState()
 *
 *     states.filterIsInstance<CounterState>().firstOrNull()?.let { state ->
 *         Text("Count: ${state.count}")
 *     }
 * }
 * ```
 *
 * @return A [State] containing the current list of UI states
 */
@Composable
public fun <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL : StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.collectAsState(): State<List<UISTATE>> {
    return container.uiStateFlow.collectAsStateWithLifecycle()
}

/**
 * Collect the DataComposerHost's combined UI state as Compose [State] without lifecycle awareness.
 *
 * Use [collectAsState] (lifecycle-aware) when possible. This variant is useful
 * in contexts where lifecycle is not available.
 *
 * @return A [State] containing the current list of UI states
 */
@Composable
public fun <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL : StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.collectAsStateNoLifecycle(): State<List<UISTATE>> {
    return container.uiStateFlow.collectAsState()
}

/**
 * Access the raw UI state [StateFlow] from a DataComposerHost.
 *
 * Useful when you need the flow directly rather than as Compose state.
 */
public val <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL : StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.uiStateFlow: StateFlow<List<UISTATE>>
    get() = container.uiStateFlow

/**
 * Access the UI action holder [SharedFlow] from a DataComposerHost.
 *
 * Use with [CollectSideEffect] to handle side effects in Compose.
 */
public val <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL : StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.uiActionFlow: SharedFlow<UIComposerActionHolder>
    get() = container.uiActionHolder
