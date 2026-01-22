package com.angelbroking.spark.libraries.storecomposer.state

import com.angelbroking.spark.libraries.storecomposer.composer.ui.WidgetId

/**
 * Base interface for all UI states managed by Stores in the StoreComposer architecture.
 *
 * Every widget's state must implement this interface. The state represents the complete
 * UI data for a single widget at a point in time. States should be immutable data classes
 * to ensure predictable updates.
 *
 * ## Required Properties
 * - [type]: Categorizes the state (default, header, footer)
 * - [visible]: Controls whether the widget should be rendered
 * - [widgetId]: Links the state to its corresponding widget
 *
 * ## Implementation Pattern
 * ```kotlin
 * // Define your widget state as a data class
 * data class QuantityWidgetState(
 *     override val type: UIStateType = UIStateDefaultType,
 *     override val visible: Boolean = true,
 *     override val widgetId: WidgetId = QuantityWidgetId,
 *
 *     // Widget-specific properties
 *     val quantity: Int = 1,
 *     val minQuantity: Int = 1,
 *     val maxQuantity: Int = 999,
 *     val isEditable: Boolean = true
 * ) : UIState
 *
 * // For features with multiple widget types, use a sealed interface
 * sealed interface OrderPadState : UIState
 *
 * data class HeaderState(...) : OrderPadState
 * data class QuantityState(...) : OrderPadState
 * data class PriceState(...) : OrderPadState
 * ```
 *
 * ## Visibility Control
 * The `visible` property is used by DataComposer to filter states before emitting
 * to the UI. Hidden widgets are excluded from the state list.
 *
 * ## Header/Footer States
 * When using [ListWithHeaderAndFooterDataComposer], states with [HeaderUIStateType]
 * are routed to `headerState` flow, and states with [FooterUIStateType] go to
 * `footerState` flow.
 *
 * ## Rendering in UI
 * ```kotlin
 * override fun render(state: List<OrderPadState>) {
 *     state.forEach { uiState ->
 *         when (uiState) {
 *             is HeaderState -> renderHeader(uiState)
 *             is QuantityState -> renderQuantity(uiState)
 *             is PriceState -> renderPrice(uiState)
 *         }
 *     }
 * }
 * ```
 *
 * @property type The category of this state. Use [UIStateDefaultType] for regular widgets,
 *                [HeaderUIStateType] for header widgets, [FooterUIStateType] for footer widgets.
 * @property visible Whether this widget should be displayed. When false, the state is
 *                   filtered out before reaching the UI.
 * @property widgetId The identifier linking this state to its widget. Used for debugging
 *                    and targeted state updates.
 *
 * @see UIStateType
 * @see WidgetId
 * @see Store
 */
interface UIState {
    val type: UIStateType
    val visible: Boolean
    val widgetId: WidgetId
}