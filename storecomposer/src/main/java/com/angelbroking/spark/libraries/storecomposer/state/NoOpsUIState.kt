package com.angelbroking.spark.libraries.storecomposer.state

import com.angelbroking.spark.libraries.storecomposer.composer.ui.WidgetId
import com.angelbroking.spark.libraries.storecomposer.composer.ui.NoStoreWidgetId
import com.angelbroking.spark.libraries.storecomposer.composer.ui.ChildWidgetId

/**
 * A simple [UIState] implementation for static widgets that don't need a Store.
 *
 * NoOpsUIState is used with [NoStoreWidgetId] and [ChildWidgetId] for widgets
 * that display static content and don't require state management or action handling.
 *
 * ## Common Use Cases
 * - Dividers between widgets
 * - Static spacers
 * - Decorative elements
 * - Static informational text
 * - Separators in grouped widgets
 *
 * ## Usage Example
 * ```kotlin
 * // Define a NoStoreWidgetId for a divider
 * object DividerWidgetId : NoStoreWidgetId {
 *     override val id: String = "divider"
 *     override val uiState: UIState = NoOpsUIState(
 *         type = UIStateDefaultType,
 *         visible = true,
 *         widgetId = this
 *     )
 * }
 *
 * // Include in your widget list
 * val widgets = listOf(
 *     HeaderWidgetId,
 *     DividerWidgetId,  // Static divider - no Store needed
 *     ContentWidgetId,
 *     DividerWidgetId,
 *     FooterWidgetId
 * )
 * ```
 *
 * ## With ChildWidgetId
 * ```kotlin
 * // Define child widgets for a group
 * object TopSpacerChildId : ChildWidgetId {
 *     override val id: String = "top_spacer"
 *     override val uiState: UIState = NoOpsUIState(
 *         type = UIStateDefaultType,
 *         visible = true,
 *         widgetId = this
 *     )
 * }
 *
 * // Use in GroupWidget
 * data class SectionGroup(
 *     override val hostId: HostWidgetId = SectionHeaderId,
 *     override val topChildren: List<ChildWidgetId> = listOf(TopSpacerChildId),
 *     override val bottomChildren: List<ChildWidgetId> = emptyList(),
 *     override val id: String = "section_group"
 * ) : GroupWidget
 * ```
 *
 * @property type The state type category
 * @property visible Whether this widget should be displayed
 * @property widgetId The identifier for this widget
 *
 * @see NoStoreWidgetId
 * @see ChildWidgetId
 * @see UIState
 */
data class NoOpsUIState(
    override val type: UIStateType,
    override val visible: Boolean = true,
    override val widgetId: WidgetId
): UIState
