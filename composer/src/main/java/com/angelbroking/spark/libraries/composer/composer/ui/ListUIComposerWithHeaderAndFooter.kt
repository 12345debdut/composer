package com.angelbroking.spark.libraries.composer.composer.ui

import com.angelbroking.spark.libraries.composer.composer.data.host.ListWithHeaderAndFooterDataComposerHost
import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.store.StoreInitObj

/**
 * [UIComposer] variant for screens with header, footer, and list content.
 *
 * ListUIComposerWithHeaderAndFooter provides separate observation methods
 * for header, footer, and main content states.
 *
 * ## Usage
 * ```kotlin
 * class OrderPadFragment : ListUIComposerWithHeaderAndFooterFragment<
 *     OrderPadState, OrderPadInitModel, OrderWidgetModel
 * >(R.layout.fragment_order_pad) {
 *
 *     private val viewModel: OrderPadViewModel by viewModels()
 *
 *     override val container: ListWithHeaderAndFooterDataComposerHost<...>
 *         get() = viewModel
 *
 *     override fun render(state: List<OrderPadState>) {
 *         // Main scrollable content
 *         adapter.submitList(state)
 *     }
 *
 *     override fun renderHeader(list: List<OrderPadState>) {
 *         // Sticky header
 *         list.filterIsInstance<HeaderState>().firstOrNull()?.let { state ->
 *             binding.header.render(state)
 *         }
 *     }
 *
 *     override fun renderFooter(list: List<OrderPadState>) {
 *         // Sticky footer (e.g., confirm button)
 *         list.filterIsInstance<ConfirmButtonState>().firstOrNull()?.let { state ->
 *             binding.confirmButton.isEnabled = state.isEnabled
 *         }
 *     }
 *
 *     override fun handleUIAction(holder: UIComposerActionHolder) {
 *         when (holder.action) {
 *             is ShowBottomSheetAction -> showBottomSheet(...)
 *         }
 *     }
 * }
 * ```
 *
 * @see UIComposer
 * @see ListUIComposerWithHeaderAndFooterFragment
 * @see ListWithHeaderAndFooterDataComposerHost
 */
interface ListUIComposerWithHeaderAndFooter<UISTATE: UIState, INITDATA: StoreInitObj,STOREMODEL: StoreInitObj>: UIComposer<UISTATE, INITDATA, STOREMODEL> {
    override val container: ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA,STOREMODEL>
}