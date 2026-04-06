package com.debdut.composer.store

/**
 * Marker interface for initialization data passed to [Store.initialise].
 *
 * StoreInitObj represents the data needed to set up a Store's initial state.
 * This typically includes configuration, default values, and context-specific
 * information required to create the widget's initial UI state.
 *
 * ## Usage Pattern
 * Create a data class implementing this interface with all the initialization data:
 *
 * ```kotlin
 * data class OrderPadInitModel(
 *     val symbol: String,
 *     val transactionType: TransactionType,
 *     val defaultQuantity: Int,
 *     val defaultPrice: Double?,
 *     val maxQuantity: Int,
 *     val lotSize: Int,
 *     val segment: Segment,
 *     val userData: UserData
 * ) : StoreInitObj
 * ```
 *
 * ## How It's Used
 * The init object flows from ViewModel through DataComposer to each Store:
 *
 * ```kotlin
 * // In ViewModel
 * val initModel = OrderPadInitModel(
 *     symbol = "RELIANCE",
 *     transactionType = TransactionType.BUY,
 *     defaultQuantity = 1,
 *     // ...
 * )
 * init(widgets = widgetList, initData = initModel)
 *
 * // In Store
 * override fun initialise(globalModel: OrderPadInitModel) {
 *     emitState {
 *         QuantityState(
 *             quantity = globalModel.defaultQuantity,
 *             maxQuantity = globalModel.maxQuantity,
 *             lotSize = globalModel.lotSize
 *         )
 *     }
 * }
 * ```
 *
 * ## Best Practices
 * - Include all data needed by any Store in your feature
 * - Keep it immutable (use data class)
 * - Consider using lazy evaluation for expensive computations
 * - Each Store extracts only the data it needs from the global model
 *
 * @see Store.initialise
 * @see StoreWidgetModel
 */
public interface StoreInitObj