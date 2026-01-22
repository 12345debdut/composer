package com.angelbroking.spark.libraries.composer.state

/**
 * Marker interface for header widget states.
 *
 * When using [ListWithHeaderAndFooterDataComposer], states whose [UIState.type]
 * implements HeaderUIStateType are automatically routed to the `headerState` flow
 * instead of the main `uiStateFlow`.
 *
 * ## Usage Example
 * ```kotlin
 * // Define a header state type (can be an object or class)
 * object MyHeaderType : HeaderUIStateType
 *
 * // Use it in your header widget state
 * data class ScreenHeaderState(
 *     override val type: UIStateType = MyHeaderType,
 *     override val visible: Boolean = true,
 *     override val widgetId: WidgetId = ScreenHeaderWidgetId,
 *     val title: String = "",
 *     val subtitle: String = "",
 *     val showBackButton: Boolean = true
 * ) : UIState
 * ```
 *
 * ## Rendering Header
 * In your Fragment, override `renderHeader()` to handle header states:
 * ```kotlin
 * class MyFragment : ListUIComposerWithHeaderAndFooterFragment<...>(...) {
 *
 *     override fun renderHeader(list: List<MyState>) {
 *         list.filterIsInstance<ScreenHeaderState>().firstOrNull()?.let { state ->
 *             binding.headerTitle.text = state.title
 *             binding.headerSubtitle.text = state.subtitle
 *             binding.backButton.isVisible = state.showBackButton
 *         }
 *     }
 * }
 * ```
 *
 * @see UIStateType
 * @see FooterUIStateType
 * @see ListWithHeaderAndFooterDataComposer
 */
interface HeaderUIStateType: UIStateType