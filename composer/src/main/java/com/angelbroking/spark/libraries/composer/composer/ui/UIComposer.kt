package com.angelbroking.spark.libraries.composer.composer.ui

import com.angelbroking.spark.libraries.composer.composer.Composer
import com.angelbroking.spark.libraries.composer.composer.data.host.DataComposerHost
import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.store.StoreInitObj

/**
 * UI layer composer interface that wraps a [DataComposerHost].
 *
 * UIComposer is implemented by Fragments to integrate with the StoreComposer
 * architecture. It provides lifecycle-aware state observation and action dispatching
 * through extension functions in `UIComposerSyntax.kt`.
 *
 * ## Implementation Pattern
 * Fragments implement UIComposer (or its variants) and provide the container:
 *
 * ```kotlin
 * class MyFragment : Fragment(), ListUIComposer<MyState, MyInitModel, MyWidgetModel> {
 *
 *     private val viewModel: MyViewModel by viewModels()
 *
 *     override val container: ListDataComposerHost<...>
 *         get() = viewModel
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *
 *         // Observe states with lifecycle awareness
 *         observeAsState(viewLifecycleOwner) { states ->
 *             renderWidgets(states)
 *         }
 *
 *         // Observe UI actions
 *         observeAction(viewLifecycleOwner) { holder ->
 *             handleAction(holder.action)
 *         }
 *     }
 *
 *     private fun onButtonClick() {
 *         // Dispatch action
 *         dispatch(ButtonClickedAction())
 *     }
 * }
 * ```
 *
 * ## Base Fragments
 * For convenience, use the base fragment classes that handle observation setup:
 * - [ListUIComposerFragment]
 * - [ListUIComposerWithHeaderAndFooterFragment]
 *
 * ## Variants
 * - [SingleUIComposer]: For single-widget screens
 * - [ListUIComposer]: For list-based screens
 * - [ListUIComposerWithHeaderAndFooter]: With header/footer support
 *
 * @property container The [DataComposerHost] (typically the ViewModel)
 *
 * @see ListUIComposer
 * @see SingleUIComposer
 * @see UIComposerSyntax
 */
interface UIComposer<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>: Composer {
    val container: DataComposerHost<UISTATE, INITDATA, STOREMODEL>
}