package com.angelbroking.spark.libraries.composer.composer.ui

import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.state.NoOpsUIState

/**
 * [WidgetId] for static widgets that don't need a [Store].
 *
 * NoStoreWidgetId is used for UI elements that have fixed content and don't
 * need to react to actions or update their state. The state is provided
 * directly by the WidgetId itself.
 *
 * ## Use Cases
 * - Dividers between widgets
 * - Static informational banners
 * - Spacers for layout purposes
 * - Decorative elements
 *
 * ## Usage Example
 * ```kotlin
 * // Define a static divider widget
 * object DividerWidgetId : NoStoreWidgetId {
 *     override val id: String = "divider"
 *     override val uiState: UIState = NoOpsUIState(
 *         type = UIStateDefaultType,
 *         visible = true,
 *         widgetId = this
 *     )
 * }
 *
 * // Define a static banner
 * object InfoBannerWidgetId : NoStoreWidgetId {
 *     override val id: String = "info_banner"
 *     override val uiState: UIState = InfoBannerState(
 *         message = "Important information here"
 *     )
 * }
 *
 * // Use in widget list
 * val widgets = listOf(
 *     HeaderWidgetId,
 *     InfoBannerWidgetId,  // Static - no Store created
 *     QuantityWidgetId,
 *     DividerWidgetId,     // Static - no Store created
 *     PriceWidgetId
 * )
 * ```
 *
 * @property uiState The static state to display for this widget
 *
 * @see WidgetId
 * @see NoOpsUIState
 * @see ChildWidgetId
 */
interface NoStoreWidgetId: WidgetId {
    val uiState: UIState
}