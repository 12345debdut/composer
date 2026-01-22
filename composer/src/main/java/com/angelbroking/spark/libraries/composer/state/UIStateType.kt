package com.angelbroking.spark.libraries.composer.state

/**
 * Marker interface for categorizing [UIState] types.
 *
 * UIStateType is used to route states to different UI sections when using
 * [ListWithHeaderAndFooterDataComposer]. States are automatically separated
 * based on their type:
 * - [UIStateDefaultType]: Regular list items (body content)
 * - [HeaderUIStateType]: Header section (sticky at top)
 * - [FooterUIStateType]: Footer section (sticky at bottom)
 *
 * ## Usage Example
 * ```kotlin
 * // Regular widget state - goes to main list
 * data class ItemWidgetState(
 *     override val type: UIStateType = UIStateDefaultType,
 *     override val visible: Boolean = true,
 *     override val widgetId: WidgetId = ItemWidgetId,
 *     val title: String = ""
 * ) : UIState
 *
 * // Header widget state - goes to header section
 * data class HeaderWidgetState(
 *     override val type: UIStateType = object : HeaderUIStateType {},
 *     override val visible: Boolean = true,
 *     override val widgetId: WidgetId = HeaderWidgetId,
 *     val title: String = ""
 * ) : UIState
 * ```
 *
 * ## Custom Types
 * You can create custom UIStateTypes for advanced routing scenarios:
 * ```kotlin
 * interface StickyUIStateType : UIStateType
 * interface FloatingUIStateType : UIStateType
 * ```
 *
 * @see UIState.type
 * @see UIStateDefaultType
 * @see HeaderUIStateType
 * @see FooterUIStateType
 */
interface UIStateType

/**
 * Default state type for regular widget states.
 *
 * States with this type are included in the main `uiStateFlow` list and
 * rendered in the body/content area of the screen.
 *
 * ## Usage
 * ```kotlin
 * data class MyWidgetState(
 *     override val type: UIStateType = UIStateDefaultType,  // Default type
 *     override val visible: Boolean = true,
 *     override val widgetId: WidgetId = MyWidgetId,
 *     val data: String = ""
 * ) : UIState
 * ```
 *
 * @see UIStateType
 * @see HeaderUIStateType
 * @see FooterUIStateType
 */
data object UIStateDefaultType: UIStateType