package com.debdut.composer.composer.ui.syntax

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.debdut.composer.action.DataComposerAction
import com.debdut.composer.action.StoreAction
import com.debdut.composer.action.holder.UIComposerActionHolder
import com.debdut.composer.composer.data.syntax.dispatch
import com.debdut.composer.composer.data.syntax.uiActionHolder
import com.debdut.composer.composer.data.syntax.uiState
import com.debdut.composer.composer.ui.UIComposer
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreId
import com.debdut.composer.store.StoreInitObj
import kotlinx.coroutines.launch

/**
 * Extension functions for [UIComposer] providing lifecycle-aware observation and dispatching.
 *
 * These functions are used in Fragments that implement UIComposer interfaces.
 *
 * ## Action Dispatching (from UI layer)
 * ```kotlin
 * // Dispatch to all subscribed stores
 * dispatch(RefreshAction())
 *
 * // Dispatch to specific store
 * dispatch(UpdateAction(data), QuantityStoreId)
 *
 * // Dispatch DataComposerAction directly to ViewModel
 * dispatch(NavigateBackDataAction())
 * ```
 *
 * ## State Observation (lifecycle-aware)
 * ```kotlin
 * observeAsState(viewLifecycleOwner) { states ->
 *     adapter.submitList(states)
 * }
 *
 * // With custom lifecycle state
 * observeAsState(viewLifecycleOwner, Lifecycle.State.RESUMED) { states ->
 *     // Only receive updates when resumed
 * }
 * ```
 *
 * ## Action Observation
 * ```kotlin
 * observeAction(viewLifecycleOwner) { holder ->
 *     when (val action = holder.action) {
 *         is ShowToastAction -> showToast(action.message)
 *         is NavigateAction -> navigate(action.destination)
 *     }
 * }
 * ```
 */

/**
 * Dispatch a [StoreAction] to all subscribed stores.
 *
 * Called from UI components (Fragments) to trigger state changes.
 */
public fun <UISTATE: UIState, INITDATA: StoreInitObj> UIComposer<UISTATE, INITDATA>.dispatch(
    action: StoreAction
): Unit = container.dispatch(action = action)

/**
 * Dispatch a [StoreAction] to a specific store.
 *
 * @param action The action to dispatch
 * @param storeId The target store's ID
 */
public fun <UISTATE: UIState, INITDATA: StoreInitObj> UIComposer<UISTATE, INITDATA>.dispatch(
    action: StoreAction,
    storeId: StoreId
): Unit = container.dispatch(action = action, storeId = storeId)

/**
 * Dispatch a [DataComposerAction] directly to the ViewModel.
 *
 * Bypasses stores and goes directly to [DataComposerActionHandler.receiveAction].
 */
public fun <UISTATE: UIState, INITDATA: StoreInitObj> UIComposer<UISTATE, INITDATA>.dispatch(
    action: DataComposerAction,
): Unit = container.dispatch(action = action)

/**
 * Observe UI state with lifecycle awareness.
 *
 * Observation starts when the lifecycle reaches [lifecycleState] and stops when
 * it falls below. Automatically restarts when the lifecycle state rises again.
 *
 * @param lifecycleOwner The lifecycle owner (typically viewLifecycleOwner in Fragments)
 * @param lifecycleState Minimum lifecycle state for observation (default: STARTED)
 * @param observer Callback invoked with the state list on each update
 */
public fun <UISTATE: UIState, INITDATA: StoreInitObj> UIComposer<UISTATE, INITDATA>.observeAsState(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observer: List<UISTATE>.() -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(lifecycleState) {
            container.uiState.collect(observer)
        }
    }
}

/**
 * Observe UI actions with lifecycle awareness.
 *
 * UI actions represent side effects dispatched by stores (navigation, toasts, etc.).
 *
 * @param lifecycleOwner The lifecycle owner (typically viewLifecycleOwner in Fragments)
 * @param lifecycleState Minimum lifecycle state for observation (default: STARTED)
 * @param observer Callback invoked for each UI action holder
 */
public fun <UISTATE: UIState, INITDATA: StoreInitObj> UIComposer<UISTATE, INITDATA>.observeAction(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observer: UIComposerActionHolder.() -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(lifecycleState) {
            container.uiActionHolder.collect(observer)
        }
    }
}