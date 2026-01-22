package com.angelbroking.spark.libraries.storecomposer.composer.ui

/**
 * [WidgetId] marker for widgets that can host child widgets.
 *
 * HostWidgetId is used with [GroupWidget] to create hierarchical widget
 * relationships. When a host widget is hidden, all its children are also
 * hidden automatically.
 *
 * ## Use Case
 * Use when you have a collapsible section where hiding the header should
 * also hide all content items below it.
 *
 * ## Usage Example
 * ```kotlin
 * // Define a host widget ID
 * object AdvancedOptionsHeaderId : HostWidgetId {
 *     override val id: String = "advanced_options_header"
 * }
 *
 * // Create a group widget linking host to children
 * data class AdvancedOptionsGroup(
 *     override val hostId: HostWidgetId = AdvancedOptionsHeaderId,
 *     override val topChildren: List<ChildWidgetId> = emptyList(),
 *     override val bottomChildren: List<ChildWidgetId> = listOf(
 *         Option1ChildId,
 *         Option2ChildId,
 *         SeparatorChildId
 *     ),
 *     override val id: String = "advanced_options_group"
 * ) : GroupWidget
 *
 * // In widget list
 * val widgets = listOf(
 *     AdvancedOptionsGroup(),  // When header is hidden, children are too
 *     OtherWidgetId
 * )
 * ```
 *
 * @see WidgetId
 * @see ChildWidgetId
 * @see GroupWidget
 */
interface HostWidgetId: WidgetId