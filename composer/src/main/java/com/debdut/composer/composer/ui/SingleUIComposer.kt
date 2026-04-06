package com.debdut.composer.composer.ui

import com.debdut.composer.composer.data.host.SingleDataComposerHost
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj

/**
 * [UIComposer] variant for single-widget screens.
 *
 * SingleUIComposer is implemented by Fragments that display a single widget
 * without list-based rendering.
 *
 * ## Usage
 * ```kotlin
 * class DetailFragment : Fragment(), SingleUIComposer<DetailState, DetailInitModel> {
 *
 *     private val viewModel: DetailViewModel by viewModels()
 *
 *     override val container: SingleDataComposerHost<...>
 *         get() = viewModel
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *
 *         // Single state observation (not a list)
 *         container.observeState(lifecycleScope) { state ->
 *             binding.title.text = state.title
 *             binding.content.text = state.content
 *         }
 *     }
 * }
 * ```
 *
 * @see UIComposer
 * @see SingleDataComposerHost
 */
public interface SingleUIComposer<UISTATE: UIState, INITDATA: StoreInitObj>: UIComposer<UISTATE, INITDATA> {
    public override val container: SingleDataComposerHost<UISTATE, INITDATA>
}