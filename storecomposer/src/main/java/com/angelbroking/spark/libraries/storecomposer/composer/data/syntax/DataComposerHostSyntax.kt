package com.angelbroking.spark.libraries.storecomposer.composer.data.syntax

import com.angelbroking.spark.libraries.storecomposer.action.Action
import com.angelbroking.spark.libraries.storecomposer.action.DataComposerAction
import com.angelbroking.spark.libraries.storecomposer.action.StoreAction
import com.angelbroking.spark.libraries.storecomposer.action.UIComposerAction
import com.angelbroking.spark.libraries.storecomposer.action.holder.UIComposerActionHolder
import com.angelbroking.spark.libraries.storecomposer.composer.data.host.DataComposerHost
import com.angelbroking.spark.libraries.storecomposer.composer.data.model.StoreActionWidgetIdPair
import com.angelbroking.spark.libraries.storecomposer.composer.ui.WidgetId
import com.angelbroking.spark.libraries.storecomposer.state.UIState
import com.angelbroking.spark.libraries.storecomposer.store.StoreId
import com.angelbroking.spark.libraries.storecomposer.store.StoreInitObj
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Extension functions for [DataComposerHost] providing a clean DSL.
 *
 * These functions simplify interaction with the underlying DataComposer,
 * used primarily in ViewModels.
 *
 * ## Action Dispatching
 * ```kotlin
 * // Dispatch to all subscribed stores
 * dispatch(RefreshAction())
 *
 * // Dispatch to specific store
 * dispatch(UpdateAction(data), QuantityStoreId)
 *
 * // Dispatch to widget's store
 * dispatch(UpdateAction(data), QuantityWidgetId)
 *
 * // Suspending variants for coroutine contexts
 * suspendDispatch(action)
 * suspendDispatch(action, storeId)
 * suspendDispatch(action, widgetId)
 *
 * // Batch dispatch
 * suspendBatchDispatch(listOf(
 *     StoreActionWidgetIdPair(action1, widget1),
 *     StoreActionWidgetIdPair(action2, widget2)
 * ))
 * ```
 *
 * ## Initialization
 * ```kotlin
 * // Initialize with widget list
 * init(widgets = widgetList, initData = initModel)
 *
 * // Update existing widgets
 * updateWidget(widgets = widgetList, initData = newInitModel)
 * ```
 *
 * ## State Access
 * ```kotlin
 * // Get current widget IDs
 * val widgets = currentWidgetIds
 *
 * // Access state flow
 * uiState.collect { states -> render(states) }
 *
 * // Observe with scope
 * observeAsState(coroutineScope) { states -> render(states) }
 * ```
 */

/**
 * Dispatch an action to all subscribed stores (non-suspending).
 */
fun <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.dispatch(
    action: Action
) = container.dispatch(action)

/** Dispatch an action to all subscribed stores (suspending). */
suspend fun <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.suspendDispatch(
    action: Action
) = container.suspendDispatch(action)

/** Get the list of currently active widget IDs. */
val <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.currentWidgetIds
    get() = container.currentWidgetIds()

/** Dispatch action to a specific store by ID (suspending). */
suspend fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.suspendDispatch(
    action: StoreAction,
    storeId: StoreId
) = container.suspendDispatchToStore(action, storeId)

/** Dispatch action to a widget's store by widget ID (suspending). */
suspend fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.suspendDispatch(
    action: StoreAction,
    widgetId: WidgetId
) = container.suspendDispatchToWidget(action, widgetId)

/** Batch dispatch multiple actions to their respective widgets (suspending). */
suspend fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.suspendBatchDispatch(
    storeActionWidgetIdPairList: List<StoreActionWidgetIdPair>
) = container.suspendBatchDispatchToWidget(storeActionWidgetIdPairList)

/** Dispatch action to a specific store by ID (non-suspending). */
fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.dispatch(
    action: StoreAction,
    storeId: StoreId
) = container.dispatchToStore(action, storeId)

/** Dispatch action to a widget's store by widget ID (non-suspending). */
fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.dispatch(
    action: StoreAction,
    widgetId: WidgetId
) = container.dispatchToWidget(action, widgetId)

/**
 * Initialize the DataComposer with a widget list.
 *
 * Creates stores for each widget and calls initialise() on each.
 *
 * @param widgets List of widget IDs defining the screen structure
 * @param initData Initialization data passed to each store
 */
suspend fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.init(
    widgets: List<WidgetId>,
    initData: INITDATA
) = container.initialiseWithWidgets(widgets, initData)

/**
 * Update existing widgets with new initialization data.
 *
 * @param widgets List of widget IDs to update
 * @param initData New initialization data
 */
suspend fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.updateWidget(
    widgets: List<WidgetId>,
    initData: INITDATA
) = container.updateWidgets(widgets = widgets, initobj = initData)

/** Access the combined UI state flow from all stores. */
val <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.uiState
    get() = container.uiStateFlow

/** Access the UI action holder flow for side effects. */
val <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.uiActionHolder
    get() = container.uiActionHolder

/**
 * Observe UI state changes within a coroutine scope.
 *
 * @param coroutineScope The scope to launch the observation in
 * @param observer Callback invoked with the state list on each update
 */
fun <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.observeAsState(
    coroutineScope: CoroutineScope,
    observer: List<UISTATE>.() -> Unit
) {
    coroutineScope.launch {
        uiState.collect(observer)
    }
}

/**
 * Observe UI actions within a coroutine scope.
 *
 * @param coroutineScope The scope to launch the observation in
 * @param observer Callback invoked for each UI action
 */
fun <UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL: StoreInitObj> DataComposerHost<UISTATE, INITDATA, STOREMODEL>.observeActions(
    coroutineScope: CoroutineScope,
    observer: UIComposerActionHolder.() -> Unit
) {
    coroutineScope.launch {
        uiActionHolder.collect(observer)
    }
}