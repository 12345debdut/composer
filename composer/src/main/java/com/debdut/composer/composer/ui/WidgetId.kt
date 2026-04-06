package com.debdut.composer.composer.ui

/**
 * Unique identifier interface for widgets in the StoreComposer architecture.
 *
 * WidgetId links UI widgets to their corresponding [Store] instances. When
 * initializing a screen, you provide a list of WidgetIds which determines:
 * - Which Stores are created
 * - The order of states in the UI state flow
 * - Target for widget-specific action dispatching
 *
 * ## Implementation Pattern
 * WidgetIds are typically implemented as singleton objects:
 *
 * ```kotlin
 * // Simple WidgetId
 * object HeaderWidgetId : WidgetId {
 *     override val id: String = "header"
 * }
 *
 * object QuantityWidgetId : WidgetId {
 *     override val id: String = "quantity"
 * }
 *
 * // For features with many widgets, use a sealed interface
 * sealed interface OrderPadWidgetId : WidgetId
 *
 * object HeaderWidgetId : OrderPadWidgetId {
 *     override val id: String = "header"
 * }
 * ```
 *
 * ## Special WidgetId Types
 * - [HostWidgetId]: For widgets that host child widgets (used with [GroupWidget])
 * - [ChildWidgetId]: For static child widgets within a group
 * - [NoStoreWidgetId]: For static widgets that don't need a Store
 * - [GroupWidget]: Groups a host with its children for visibility control
 *
 * ## Widget List Initialization
 * ```kotlin
 * val widgets = listOf(
 *     HeaderWidgetId,
 *     QuantityWidgetId,
 *     PriceWidgetId,
 *     ConfirmWidgetId
 * )
 * container.init(widgets, initModel)
 * ```
 *
 * @property id A unique string identifier for this widget
 *
 * @see StoreId
 * @see HostWidgetId
 * @see NoStoreWidgetId
 * @see GroupWidget
 */
public interface WidgetId {
    public val id: String

    public companion object {
        /** Empty WidgetId for cases where no specific widget is targeted. */
        public val Empty: WidgetId = object : WidgetId {
            override val id: String = ""
        }
    }
}