package com.angelbroking.spark.libraries.storecomposer.composer.data

import com.angelbroking.spark.libraries.storecomposer.state.UIState
import com.angelbroking.spark.libraries.storecomposer.store.StoreInitObj

/**
 * [DataComposer] variant for screens with multiple widgets displayed as a list.
 *
 * ListDataComposer manages multiple [Store]s and combines their states into
 * a single ordered list. This is the most common composer type for complex screens.
 *
 * ## Use Cases
 * - Form screens with multiple input widgets
 * - Order pads with quantity, price, and confirmation sections
 * - Settings screens with multiple configuration sections
 * - Any screen composed of multiple independent widgets
 *
 * ## Widget Order
 * The state list order matches the widget list order passed to [initialiseWithWidgets].
 * Hidden widgets (where `visible = false`) are filtered out before emission.
 *
 * ## Usage
 * ```kotlin
 * // ViewModel
 * class OrderViewModel @Inject constructor(
 *     storeFactory: OrderStoreFactory
 * ) : ListDataComposerViewModel<OrderState, OrderInitModel, OrderWidgetModel>(storeFactory) {
 *
 *     fun initialize(orderData: OrderData) {
 *         viewModelScope.launch {
 *             val widgets = listOf(
 *                 HeaderWidgetId,
 *                 QuantityWidgetId,
 *                 PriceWidgetId,
 *                 ConfirmWidgetId
 *             )
 *             init(widgets, OrderInitModel(orderData))
 *         }
 *     }
 * }
 *
 * // Fragment
 * override fun render(state: List<OrderState>) {
 *     adapter.submitList(state)
 * }
 * ```
 *
 * ## GroupWidget Support
 * ListDataComposer supports [GroupWidget] for grouped visibility control:
 * when a host widget is hidden, its children are also hidden.
 *
 * @see DataComposer
 * @see SingleDataComposer
 * @see ListWithHeaderAndFooterDataComposer
 * @see ListDataComposerHost
 */
interface ListDataComposer<UISTATE : UIState, INITOBJ : StoreInitObj, STOREMODEL: StoreInitObj>: DataComposer<UISTATE, INITOBJ, STOREMODEL>