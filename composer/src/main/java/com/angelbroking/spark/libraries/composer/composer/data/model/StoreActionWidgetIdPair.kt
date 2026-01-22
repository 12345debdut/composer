package com.angelbroking.spark.libraries.composer.composer.data.model

import androidx.annotation.Keep
import com.angelbroking.spark.libraries.composer.action.StoreAction
import com.angelbroking.spark.libraries.composer.composer.ui.WidgetId

/**
 * Data class pairing a [StoreAction] with its target [WidgetId].
 *
 * Used for batch dispatching multiple actions to different widgets efficiently
 * via [DataComposer.suspendBatchDispatchToWidget].
 *
 * ## Use Case
 * When you need to update multiple widgets at once (e.g., after receiving
 * broadcast data), batch dispatching is more efficient than individual dispatches.
 *
 * ## Usage Example
 * ```kotlin
 * // Batch update multiple widgets with broadcast data
 * suspend fun handleBroadcast(data: BroadcastData) {
 *     val updates = listOf(
 *         StoreActionWidgetIdPair(
 *             action = UpdatePriceAction(data.price),
 *             widgetId = PriceWidgetId
 *         ),
 *         StoreActionWidgetIdPair(
 *             action = UpdateQuantityAction(data.quantity),
 *             widgetId = QuantityWidgetId
 *         ),
 *         StoreActionWidgetIdPair(
 *             action = UpdateStatusAction(data.status),
 *             widgetId = StatusWidgetId
 *         )
 *     )
 *     suspendBatchDispatch(updates)
 * }
 * ```
 *
 * @property action The [StoreAction] to dispatch
 * @property widgetId The target widget that should receive the action
 *
 * @see DataComposer.suspendBatchDispatchToWidget
 */
@Keep
data class StoreActionWidgetIdPair(
    val action: StoreAction,
    val widgetId: WidgetId
)
