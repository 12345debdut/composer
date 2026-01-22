package com.angelbroking.spark.libraries.storecomposer.composer.data.host

import com.angelbroking.spark.libraries.storecomposer.composer.data.ListWithHeaderAndFooterDataComposer
import com.angelbroking.spark.libraries.storecomposer.state.UIState
import com.angelbroking.spark.libraries.storecomposer.store.StoreInitObj

/**
 * [ListDataComposerHost] variant with header and footer support.
 *
 * Provides access to [ListWithHeaderAndFooterDataComposer] for screens
 * that need separate header and footer sections in addition to the main list.
 *
 * ## Usage
 * ```kotlin
 * @HiltViewModel
 * class OrderPadViewModel @Inject constructor(
 *     storeFactory: OrderPadStoreFactory
 * ) : ListWithHeaderAndFooterDataComposerViewModel<...>(storeFactory) {
 *     // container provides headerState and footerState
 * }
 *
 * // In Fragment
 * override fun render(states: List<OrderPadState>) {
 *     adapter.submitList(states)  // Main list content
 * }
 *
 * override fun renderHeader(list: List<OrderPadState>) {
 *     // Render sticky header
 * }
 *
 * override fun renderFooter(list: List<OrderPadState>) {
 *     // Render sticky footer (confirm button, etc.)
 * }
 * ```
 *
 * @see ListDataComposerHost
 * @see ListWithHeaderAndFooterDataComposer
 * @see ListWithHeaderAndFooterDataComposerViewModel
 */
interface ListWithHeaderAndFooterDataComposerHost<UISTATE: UIState, INITDATA: StoreInitObj,STOREMODEL: StoreInitObj>: ListDataComposerHost<UISTATE, INITDATA,STOREMODEL> {
    override val container: ListWithHeaderAndFooterDataComposer<UISTATE, INITDATA,STOREMODEL>
}