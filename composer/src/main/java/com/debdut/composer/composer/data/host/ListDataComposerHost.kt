package com.debdut.composer.composer.data.host

import com.debdut.composer.composer.data.ListDataComposer
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj

/**
 * [DataComposerHost] variant for list-based screens.
 *
 * Provides access to a [ListDataComposer] for screens with multiple widgets.
 * This is the most common host type.
 *
 * ## Usage
 * ```kotlin
 * @HiltViewModel
 * class OrderViewModel @Inject constructor(
 *     storeFactory: OrderStoreFactory
 * ) : ListDataComposerViewModel<OrderState, OrderInitModel, OrderWidgetModel>(storeFactory) {
 *     // container is provided by base class
 * }
 *
 * // In Fragment
 * override fun render(states: List<OrderState>) {
 *     adapter.submitList(states)
 * }
 * ```
 *
 * @see DataComposerHost
 * @see ListDataComposer
 * @see ListDataComposerViewModel
 */
public interface ListDataComposerHost<UISTATE: UIState, INITDATA: StoreInitObj>: DataComposerHost<UISTATE, INITDATA> {
    public override val container: ListDataComposer<UISTATE, INITDATA>
}