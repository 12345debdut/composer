package com.debdut.composer.composer.ui

/**
 * [WidgetId] that groups a host widget with its dependent child widgets.
 *
 * GroupWidget creates a hierarchical relationship where the host widget
 * controls the visibility of all its children. When the host widget's
 * `visible` property is `false`, all children are automatically hidden.
 *
 * ## Structure
 * ```
 * GroupWidget
 * ├── topChildren[]     ─── Static widgets ABOVE the host
 * ├── hostId           ─── The main widget with a Store
 * └── bottomChildren[] ─── Static widgets BELOW the host
 * ```
 *
 * ## Use Cases
 * - Collapsible sections with header and content
 * - Expandable panels with dividers
 * - Conditional UI sections that show/hide together
 *
 * ## Usage Example
 * ```kotlin
 * // Define child widget IDs
 * object SectionDividerChildId : ChildWidgetId {
 *     override val id: String = "section_divider"
 *     override val uiState: UIState = DividerState(...)
 * }
 *
 * object SpacerChildId : ChildWidgetId {
 *     override val id: String = "spacer"
 *     override val uiState: UIState = SpacerState(height = 16)
 * }
 *
 * // Define the group
 * data class AdvancedOptionsGroup(
 *     override val id: String = "advanced_options_group",
 *     override val hostId: HostWidgetId = AdvancedOptionsHeaderId,
 *     override val topChildren: List<ChildWidgetId> = listOf(
 *         SectionDividerChildId
 *     ),
 *     override val bottomChildren: List<ChildWidgetId> = listOf(
 *         SpacerChildId
 *     )
 * ) : GroupWidget
 *
 * // Use in widget list
 * val widgets = listOf(
 *     MainContentWidgetId,
 *     AdvancedOptionsGroup(),  // Divider -> Header -> Spacer (all hide together)
 *     FooterWidgetId
 * )
 * ```
 *
 * ## Visibility Behavior
 * When the host widget emits a state with `visible = false`:
 * - All `topChildren` are hidden
 * - The host widget is hidden
 * - All `bottomChildren` are hidden
 *
 * @property hostId The main widget ID that controls visibility
 * @property topChildren Static widgets displayed above the host
 * @property bottomChildren Static widgets displayed below the host
 *
 * @see HostWidgetId
 * @see ChildWidgetId
 * @see WidgetId
 */
public interface GroupWidget: WidgetId {
    public val hostId: HostWidgetId
    public val topChildren: List<ChildWidgetId>
    public val bottomChildren: List<ChildWidgetId>
}