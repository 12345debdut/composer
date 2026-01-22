package com.angelbroking.spark.libraries.composer.composer.data.host

import com.angelbroking.spark.libraries.composer.composer.data.ListDataComposer
import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.store.StoreInitObj

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
interface ListDataComposerHost<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>: DataComposerHost<UISTATE, INITDATA,STOREMODEL> {
    override val container: ListDataComposer<UISTATE, INITDATA, STOREMODEL>
}