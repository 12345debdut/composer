package com.debdut.composer.composer.data.syntax

import com.debdut.composer.composer.data.host.ListWithHeaderAndFooterDataComposerHost
import com.debdut.composer.state.UIState
import com.debdut.composer.state.HeaderUIStateType
import com.debdut.composer.state.FooterUIStateType
import com.debdut.composer.store.StoreInitObj
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Extension functions for [ListWithHeaderAndFooterDataComposerHost].
 *
 * These extensions provide access to the separate header and footer state flows,
 * enabling independent observation of different UI sections.
 *
 * ## Usage
 * ```kotlin
 * // Observe header separately
 * container.observeHeaderState(viewModelScope) { headerStates ->
 *     headerStates.filterIsInstance<HeaderWidgetState>().firstOrNull()?.let {
 *         updateHeader(it)
 *     }
 * }
 *
 * // Observe footer separately
 * container.observeFooterState(viewModelScope) { footerStates ->
 *     footerStates.filterIsInstance<ConfirmButtonState>().firstOrNull()?.let {
 *         updateConfirmButton(it)
 *     }
 * }
 *
 * // Direct access to state flows
 * container.headerState.collect { ... }
 * container.footerState.collect { ... }
 * ```
 */

/**
 * Observe header states within a coroutine scope.
 *
 * Receives states whose type implements [HeaderUIStateType].
 *
 * @param coroutineScope The scope to launch the observation in
 * @param observer Callback invoked with header states on each update
 */
public fun <UISTATE : UIState, INITDATA : StoreInitObj> ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA>.observeHeaderState(
    coroutineScope: CoroutineScope,
    observer: List<UISTATE>.() -> Unit
) {
    coroutineScope.launch {
        headerState.collect(observer)
    }
}

/**
 * Observe footer states within a coroutine scope.
 *
 * Receives states whose type implements [FooterUIStateType].
 *
 * @param coroutineScope The scope to launch the observation in
 * @param observer Callback invoked with footer states on each update
 */
public fun <UISTATE : UIState, INITDATA : StoreInitObj> ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA>.observeFooterState(
    coroutineScope: CoroutineScope,
    observer: List<UISTATE>.() -> Unit
) {
    coroutineScope.launch {
        footerState.collect(observer)
    }
}

/** Access the header state flow (states with [HeaderUIStateType]). */
public val <UISTATE : UIState, INITDATA : StoreInitObj> ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA>.headerState: StateFlow<List<UISTATE>>
    get() = container.headerState

/** Access the footer state flow (states with [FooterUIStateType]). */
public val <UISTATE : UIState, INITDATA : StoreInitObj> ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA>.footerState: StateFlow<List<UISTATE>>
    get() = container.footerState
