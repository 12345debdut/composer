package com.angelbroking.spark.libraries.storecomposer.composer.ui

import com.angelbroking.spark.libraries.storecomposer.composer.data.host.ListDataComposerHost
import com.angelbroking.spark.libraries.storecomposer.state.UIState
import com.angelbroking.spark.libraries.storecomposer.store.StoreInitObj

/**
 * [UIComposer] variant for list-based screens.
 *
 * ListUIComposer is implemented by Fragments that display multiple widgets
 * in a list format (e.g., RecyclerView).
 *
 * ## Usage
 * ```kotlin
 * class OrderFragment : ListUIComposerFragment<OrderState, OrderInitModel, OrderWidgetModel>(
 *     R.layout.fragment_order
 * ), ListUIComposer<OrderState, OrderInitModel, OrderWidgetModel> {
 *
 *     private val viewModel: OrderViewModel by viewModels()
 *
 *     override val container: ListDataComposerHost<...>
 *         get() = viewModel
 *
 *     override fun render(state: List<OrderState>) {
 *         adapter.submitList(state)
 *     }
 *
 *     override fun handleUIAction(holder: UIComposerActionHolder) {
 *         when (holder.action) {
 *             is ShowToastAction -> showToast(...)
 *         }
 *     }
 * }
 * ```
 *
 * @see UIComposer
 * @see ListUIComposerFragment
 * @see ListDataComposerHost
 */
interface ListUIComposer<UISTATE: UIState, INITDATA: StoreInitObj,STOREMODEL: StoreInitObj>: UIComposer<UISTATE, INITDATA, STOREMODEL> {
    override val container: ListDataComposerHost<UISTATE, INITDATA, STOREMODEL>
}