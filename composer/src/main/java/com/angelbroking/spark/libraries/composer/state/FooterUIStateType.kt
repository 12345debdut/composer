package com.angelbroking.spark.libraries.composer.state

/**
 * Marker interface for footer widget states.
 *
 * When using [ListWithHeaderAndFooterDataComposer], states whose [UIState.type]
 * implements FooterUIStateType are automatically routed to the `footerState` flow
 * instead of the main `uiStateFlow`.
 *
 * ## Usage Example
 * ```kotlin
 * // Define a footer state type
 * object ConfirmButtonFooterType : FooterUIStateType
 *
 * // Use it in your footer widget state
 * data class ConfirmButtonState(
 *     override val type: UIStateType = ConfirmButtonFooterType,
 *     override val visible: Boolean = true,
 *     override val widgetId: WidgetId = ConfirmButtonWidgetId,
 *     val buttonText: String = "Confirm",
 *     val isEnabled: Boolean = true,
 *     val isLoading: Boolean = false
 * ) : UIState
 * ```
 *
 * ## Rendering Footer
 * In your Fragment, override `renderFooter()` to handle footer states:
 * ```kotlin
 * class MyFragment : ListUIComposerWithHeaderAndFooterFragment<...>(...) {
 *
 *     override fun renderFooter(list: List<MyState>) {
 *         list.filterIsInstance<ConfirmButtonState>().firstOrNull()?.let { state ->
 *             binding.confirmButton.text = state.buttonText
 *             binding.confirmButton.isEnabled = state.isEnabled
 *             binding.progressBar.isVisible = state.isLoading
 *         }
 *     }
 * }
 * ```
 *
 * ## Common Footer Use Cases
 * - Confirm/Submit buttons
 * - Price summary bars
 * - Navigation bars
 * - Action buttons that should stay visible while scrolling
 *
 * @see UIStateType
 * @see HeaderUIStateType
 * @see ListWithHeaderAndFooterDataComposer
 */
interface FooterUIStateType: UIStateType