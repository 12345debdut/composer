package com.angelbroking.spark.libraries.storecomposer.composer.data.syntax

import com.angelbroking.spark.libraries.storecomposer.composer.data.host.ListWithHeaderAndFooterDataComposerHost
import com.angelbroking.spark.libraries.storecomposer.state.UIState
import com.angelbroking.spark.libraries.storecomposer.state.HeaderUIStateType
import com.angelbroking.spark.libraries.storecomposer.state.FooterUIStateType
import com.angelbroking.spark.libraries.storecomposer.store.StoreInitObj
import kotlinx.coroutines.CoroutineScope
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
fun <UISTATE : UIState, INITDATA : StoreInitObj,STOREMODEL: StoreInitObj> ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA, STOREMODEL>.observeHeaderState(
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
fun <UISTATE : UIState, INITDATA : StoreInitObj,STOREMODEL: StoreInitObj> ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA,STOREMODEL>.observeFooterState(
    coroutineScope: CoroutineScope,
    observer: List<UISTATE>.() -> Unit
) {
    coroutineScope.launch {
        footerState.collect(observer)
    }
}

/** Access the header state flow (states with [HeaderUIStateType]). */
val <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL: StoreInitObj> ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA, STOREMODEL>.headerState
    get() = container.headerState

/** Access the footer state flow (states with [FooterUIStateType]). */
val <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL: StoreInitObj> ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA, STOREMODEL>.footerState
    get() = container.footerState
