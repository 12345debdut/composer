package com.angelbroking.spark.libraries.composer.composer.data.host

import com.angelbroking.spark.libraries.composer.composer.data.SingleDataComposer
import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.store.StoreInitObj

/**
 * [DataComposerHost] variant for single-widget screens.
 *
 * Provides access to a [SingleDataComposer] with additional convenience
 * functions for observing a single state.
 *
 * ## Usage
 * ```kotlin
 * class DetailViewModel : ViewModel(), SingleDataComposerHost<...> {
 *     override val container: SingleDataComposer<...> by lazy {
 *         singleDataComposer(factory, viewModelScope, handler)
 *     }
 * }
 *
 * // In Fragment
 * container.observeState(lifecycleScope) { state ->
 *     // state is the single UIState (not a list)
 *     renderDetail(state)
 * }
 * ```
 *
 * @see DataComposerHost
 * @see SingleDataComposer
 * @see SingleDataComposerHostSyntax
 */
interface SingleDataComposerHost<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>: DataComposerHost<UISTATE, INITDATA, STOREMODEL> {
    override val container: SingleDataComposer<UISTATE, INITDATA, STOREMODEL>
}