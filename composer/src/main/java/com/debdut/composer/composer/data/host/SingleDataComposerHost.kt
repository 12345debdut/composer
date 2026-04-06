package com.debdut.composer.composer.data.host

import com.debdut.composer.composer.data.SingleDataComposer
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj

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
public interface SingleDataComposerHost<UISTATE: UIState, INITDATA: StoreInitObj>: DataComposerHost<UISTATE, INITDATA> {
    public override val container: SingleDataComposer<UISTATE, INITDATA>
}