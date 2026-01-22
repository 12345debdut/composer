package com.angelbroking.spark.libraries.storecomposer.composer.data.syntax

import com.angelbroking.spark.libraries.storecomposer.composer.data.host.SingleDataComposerHost
import com.angelbroking.spark.libraries.storecomposer.state.UIState
import com.angelbroking.spark.libraries.storecomposer.store.StoreInitObj
import kotlinx.coroutines.CoroutineScope

/**
 * Extension functions for [SingleDataComposerHost] providing single-state observation.
 *
 * Unlike list-based composers that provide `List<UISTATE>`, these extensions
 * unwrap the single state for direct access.
 *
 * ## Usage
 * ```kotlin
 * // In ViewModel or Fragment
 * container.observeState(viewModelScope) { state ->
 *     // state is UISTATE, not List<UISTATE>
 *     updateUI(state.title, state.content)
 * }
 * ```
 */

/**
 * Observe the single widget state.
 *
 * Extracts the first (and only) state from the list and provides it
 * directly to the observer. If no state is present, observer is not called.
 *
 * @param coroutineScope The scope to launch the observation in
 * @param observer Callback invoked with the single state on each update
 */
fun <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL: StoreInitObj> SingleDataComposerHost<UISTATE, INITDATA, STOREMODEL>.observeState(
    coroutineScope: CoroutineScope,
    observer: UISTATE.() -> Unit
) {
    observeAsState(coroutineScope = coroutineScope) {
        firstOrNull()?.observer()
    }
}

