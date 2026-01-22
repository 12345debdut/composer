package com.angelbroking.spark.libraries.composer.store.syntax

import com.angelbroking.spark.libraries.composer.action.ComposerAction
import com.angelbroking.spark.libraries.composer.action.DataComposerAction
import com.angelbroking.spark.libraries.composer.action.StoreAction
import com.angelbroking.spark.libraries.composer.action.UIComposerAction
import com.angelbroking.spark.libraries.composer.action.holder.ActionHolder
import com.angelbroking.spark.libraries.composer.action.holder.DataComposerActionHolder
import com.angelbroking.spark.libraries.composer.action.holder.UIComposerActionHolder
import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.store.Store
import com.angelbroking.spark.libraries.composer.store.StoreId
import com.angelbroking.spark.libraries.composer.store.StoreInitObj
import kotlinx.coroutines.flow.update

/**
 * Extension functions for [Store] operations.
 *
 * This file provides the DSL for working with Stores, including:
 * - State updates: [updateState], [emitState], [currentState]
 * - Action dispatching: [suspendDispatch], [dispatch], [send]
 * - Scope access: [storeScope]
 *
 * ## Usage in Store
 * ```kotlin
 * class MyStore : Store<MyState, InitModel, WidgetModel>() {
 *
 *     override suspend fun receive(action: StoreAction, storeId: StoreId) {
 *         when (action) {
 *             is IncrementAction -> {
 *                 // Update state using copy pattern
 *                 updateState {
 *                     copy(count = count + 1)
 *                 }
 *             }
 *             is RefreshAction -> {
 *                 // Launch async work
 *                 storeScope.launch {
 *                     val data = fetchData()
 *                     updateState { copy(data = data) }
 *                 }
 *             }
 *             is CompleteAction -> {
 *                 // Dispatch to UI layer
 *                 suspendDispatch(ShowToastUIAction("Done!"))
 *             }
 *         }
 *     }
 * }
 * ```
 */

/**
 * Update the Store's state using a transformation function.
 *
 * The lambda receives the current state as receiver and should return
 * the new state (typically using `copy()`). If current state is null,
 * the lambda is not called.
 *
 * After update, [Store.invokeOnStateUpdate] is called with previous and new states.
 *
 * ## Example
 * ```kotlin
 * updateState {
 *     copy(
 *         quantity = quantity + 1,
 *         isValid = quantity + 1 <= maxQuantity
 *     )
 * }
 * ```
 *
 * @param block Transformation function that receives current state and returns new state
 * @return The new state after update, or null if current state was null
 */
fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> Store<UISTATE, INITDATA, STOREMODEL>.updateState(block: UISTATE.() -> UISTATE?): UISTATE? {
    val prevState = mutableUiStateFlow.value
    val newState = prevState?.block()
    mutableUiStateFlow.update {
        newState
    }.also {
        invokeOnStateUpdate(prevState = prevState, currentState = newState)
    }
    return newState
}

/**
 * Get the current state of the Store.
 *
 * Returns null if the state has not been initialized yet.
 *
 * ## Example
 * ```kotlin
 * val current = currentState ?: return
 * if (current.quantity >= current.maxQuantity) {
 *     suspendDispatch(ShowErrorUIAction("Maximum reached"))
 * }
 * ```
 */
val <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> Store<UISTATE, INITDATA, STOREMODEL>.currentState
    get() = mutableUiStateFlow.value

/**
 * Emit a completely new state, replacing the current state.
 *
 * Unlike [updateState], this doesn't transform existing state - it replaces it entirely.
 * Typically used in [Store.initialise] to set up initial state.
 *
 * ## Example
 * ```kotlin
 * override fun initialise(globalModel: InitModel) {
 *     emitState {
 *         MyWidgetState(
 *             value = globalModel.defaultValue,
 *             isEnabled = globalModel.isEditable
 *         )
 *     }
 * }
 * ```
 *
 * @param block Factory function that creates the new state
 */
fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> Store<UISTATE, INITDATA, STOREMODEL>.emitState(block: () -> UISTATE) {
    mutableUiStateFlow.update {
        block.invoke()
    }
}

/**
 * Get the CoroutineScope associated with this Store.
 *
 * Use this scope to launch coroutines that should be cancelled when
 * the Store is disposed. The scope is tied to the ViewModel's lifecycle.
 *
 * ## Example
 * ```kotlin
 * override suspend fun receive(action: StoreAction, storeId: StoreId) {
 *     when (action) {
 *         is LoadDataAction -> {
 *             storeScope.launch {
 *                 updateState { copy(isLoading = true) }
 *                 val data = repository.fetchData()
 *                 updateState { copy(isLoading = false, data = data) }
 *             }
 *         }
 *     }
 * }
 *
 * override fun reset() {
 *     // Subscribe to external updates
 *     externalSource.updates
 *         .onEach { updateState { copy(externalData = it) } }
 *         .launchIn(storeScope)
 * }
 * ```
 */
val <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> Store<UISTATE, INITDATA, STOREMODEL>.storeScope
    get() = coroutineScope

/**
 * Dispatch a [ComposerAction] to parent layers (suspending version).
 *
 * Use this to send actions that should be handled outside the Store:
 * - [UIComposerAction]: Handled by Fragment (navigation, toasts, dialogs)
 * - [DataComposerAction]: Handled by ViewModel (cross-widget coordination)
 *
 * ## Example
 * ```kotlin
 * // Dispatch UI action
 * suspendDispatch(ShowToastUIAction("Saved!"))
 * suspendDispatch(NavigateToDetailsUIAction(itemId))
 *
 * // Dispatch to DataComposer/ViewModel
 * suspendDispatch(RefreshAllWidgetsDataAction())
 * ```
 *
 * @param action The composer action to dispatch
 */
suspend fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> Store<UISTATE, INITDATA,STOREMODEL>.suspendDispatch(action: ComposerAction) {
    when (val actionHolder = action.toActionHolder(storeId = storeId)) {
        is UIComposerActionHolder -> mutableUiSideEffects.emit(actionHolder)
        is DataComposerActionHolder -> mutableComposerSideEffects.emit(actionHolder)
    }
}

/**
 * Dispatch a [ComposerAction] to parent layers (non-suspending version).
 *
 * Uses `tryEmit` which may drop the action if the buffer is full.
 * Prefer [suspendDispatch] when possible.
 *
 * @param action The composer action to dispatch
 * @return true if the action was emitted, false if buffer was full
 */
fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> Store<UISTATE, INITDATA, STOREMODEL>.dispatch(action: ComposerAction): Boolean {
    return when (val actionHolder = action.toActionHolder(storeId = storeId)) {
        is UIComposerActionHolder -> mutableUiSideEffects.tryEmit(actionHolder)
        is DataComposerActionHolder -> mutableComposerSideEffects.tryEmit(actionHolder)
        else -> false
    }
}

/**
 * Internal helper to wrap ComposerAction in appropriate ActionHolder.
 */
private fun ComposerAction.toActionHolder(storeId: StoreId): ActionHolder? {
    return when (this) {
        is UIComposerAction -> UIComposerActionHolder(action = this, storeId = storeId)
        is DataComposerAction -> DataComposerActionHolder(action = this, storeId = storeId)
        else -> null
    }
}

/**
 * Send a [StoreAction] directly to this Store.
 *
 * The action will be processed if its [ActionId] is in [Store.subscribedStoreAction].
 * Used internally by DataComposer for action routing.
 *
 * @param action The store action to process
 */
suspend fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> Store<UISTATE, INITDATA, STOREMODEL>.send(action: StoreAction) {
    if (action.actionId in subscribedStoreAction) receive(action, StoreId.Empty)
}

/**
 * Send a [StoreAction] directly to this Store with source StoreId.
 *
 * The storeId parameter indicates which Store originally dispatched this action,
 * useful for inter-store communication patterns.
 *
 * @param action The store action to process
 * @param storeId The ID of the Store that dispatched this action
 */
suspend fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> Store<UISTATE, INITDATA, STOREMODEL>.send(action: StoreAction, storeId: StoreId) {
    if (action.actionId in subscribedStoreAction) receive(action, storeId)
}
