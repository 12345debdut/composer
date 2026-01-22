package com.angelbroking.spark.libraries.composer.composer.ui

/**
 * [WidgetId] for child widgets within a [GroupWidget].
 *
 * ChildWidgetId extends [NoStoreWidgetId] - child widgets are static and don't
 * have their own Store. Their visibility is controlled by the parent [HostWidgetId].
 *
 * ## Use Cases
 * - Dividers that appear between grouped items
 * - Spacers for visual separation
 * - Static labels or decorations within a group
 *
 * ## Usage Example
 * ```kotlin
 * // Define a child widget for a divider
 * object SectionDividerChildId : ChildWidgetId {
 *     override val id: String = "section_divider"
 *     override val uiState: UIState = DividerState(
 *         type = UIStateDefaultType,
 *         visible = true,
 *         widgetId = this
 *     )
 * }
 *
 * // Define a spacer child widget
 * object SpacerChildId : ChildWidgetId {
 *     override val id: String = "spacer"
 *     override val uiState: UIState = NoOpsUIState(
 *         type = UIStateDefaultType,
 *         visible = true,
 *         widgetId = this
 *     )
 * }
 *
 * // Use in a GroupWidget
 * data class AdvancedOptionsGroup(
 *     override val id: String = "advanced_group",
 *     override val hostId: HostWidgetId = AdvancedOptionsHeaderId,
 *     override val topChildren: List<ChildWidgetId> = listOf(SectionDividerChildId),
 *     override val bottomChildren: List<ChildWidgetId> = listOf(SpacerChildId)
 * ) : GroupWidget
 * ```
 *
 * ## Visibility Behavior
 * When the parent [HostWidgetId] is hidden, all ChildWidgetIds in the group
 * are automatically hidden as well.
 *
 * @see NoStoreWidgetId
 * @see GroupWidget
 * @see HostWidgetId
 */
interface ChildWidgetId: NoStoreWidgetId