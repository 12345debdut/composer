package com.angelbroking.spark.libraries.composer.composer.data

import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.state.HeaderUIStateType
import com.angelbroking.spark.libraries.composer.state.FooterUIStateType
import com.angelbroking.spark.libraries.composer.store.StoreInitObj
import kotlinx.coroutines.flow.StateFlow

/**
 * [ListDataComposer] variant with separate state flows for header and footer.
 *
 * This composer automatically separates states based on their [UIState.type]:
 * - States with [HeaderUIStateType] → [headerState] flow
 * - States with [FooterUIStateType] → [footerState] flow
 * - Other states → [uiStateFlow] (main list)
 *
 * ## Use Cases
 * - Order pads with sticky header/footer buttons
 * - Screens with fixed top navigation and bottom actions
 * - Any UI with scrollable content between fixed sections
 *
 * ## State Routing
 * ```
 * All widget states
 *         │
 *         ├── HeaderUIStateType → headerState
 *         │
 *         ├── FooterUIStateType → footerState
 *         │
 *         └── UIStateDefaultType → uiStateFlow
 * ```
 *
 * ## Usage
 * ```kotlin
 * // Define header state
 * data class ScreenHeaderState(
 *     override val type: UIStateType = object : HeaderUIStateType {},
 *     override val visible: Boolean = true,
 *     override val widgetId: WidgetId = HeaderWidgetId,
 *     val title: String = ""
 * ) : UIState
 *
 * // Define footer state
 * data class ConfirmButtonState(
 *     override val type: UIStateType = object : FooterUIStateType {},
 *     override val visible: Boolean = true,
 *     override val widgetId: WidgetId = ConfirmWidgetId,
 *     val buttonText: String = "Confirm",
 *     val isEnabled: Boolean = true
 * ) : UIState
 *
 * // Fragment
 * override fun renderHeader(list: List<MyState>) {
 *     list.filterIsInstance<ScreenHeaderState>().firstOrNull()?.let { state ->
 *         binding.header.title = state.title
 *     }
 * }
 *
 * override fun renderFooter(list: List<MyState>) {
 *     list.filterIsInstance<ConfirmButtonState>().firstOrNull()?.let { state ->
 *         binding.confirmButton.text = state.buttonText
 *         binding.confirmButton.isEnabled = state.isEnabled
 *     }
 * }
 * ```
 *
 * @property headerState Flow of states with [HeaderUIStateType]
 * @property footerState Flow of states with [FooterUIStateType]
 *
 * @see ListDataComposer
 * @see HeaderUIStateType
 * @see FooterUIStateType
 * @see ListWithHeaderAndFooterDataComposerHost
 */
interface ListWithHeaderAndFooterDataComposer<UISTATE: UIState, INITOBJ: StoreInitObj, STOREMODEL: StoreInitObj>: ListDataComposer<UISTATE, INITOBJ, STOREMODEL> {

    /** State flow for header widgets (states with [HeaderUIStateType]). */
    val headerState: StateFlow<List<UISTATE>>

    /** State flow for footer widgets (states with [FooterUIStateType]). */
    val footerState: StateFlow<List<UISTATE>>
}
