package com.debdut.composer.composer.data.host

import com.debdut.composer.composer.data.ListWithHeaderAndFooterDataComposer
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj

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
public interface ListWithHeaderAndFooterDataComposerHost<UISTATE: UIState, INITDATA: StoreInitObj>: ListDataComposerHost<UISTATE, INITDATA> {
    public override val container: ListWithHeaderAndFooterDataComposer<UISTATE, INITDATA>
}