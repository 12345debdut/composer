package com.angelbroking.spark.libraries.storecomposer.composer.data

import com.angelbroking.spark.libraries.storecomposer.action.Action
import com.angelbroking.spark.libraries.storecomposer.action.DataComposerAction
import com.angelbroking.spark.libraries.storecomposer.action.holder.DataComposerActionHolder

/**
 * Callback interface for handling [DataComposerAction]s at the ViewModel level.
 *
 * ViewModels typically implement this interface to:
 * - Handle cross-widget coordination actions
 * - Process navigation/routing decisions
 * - Perform analytics/logging for all actions
 * - Trigger API calls or other side effects
 *
 * ## Implementation Example
 * ```kotlin
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     storeFactory: MyStoreFactory,
 *     private val repository: MyRepository
 * ) : ListDataComposerViewModel<MyState, MyInitModel, MyWidgetModel>(storeFactory),
 *     DataComposerActionHandler {
 *
 *     override val dataComposerActionHandler: DataComposerActionHandler = this
 *
 *     override suspend fun receiveAction(holder: DataComposerActionHolder) {
 *         when (val action = holder.action) {
 *             is RefreshAllAction -> {
 *                 val newData = repository.fetchData()
 *                 init(widgets = currentWidgetIds, initData = newData)
 *             }
 *             is NavigateToDetailsAction -> {
 *                 _navigationEvent.emit(Destination.Details(action.id))
 *             }
 *             is SaveDataAction -> {
 *                 repository.save(action.data)
 *             }
 *         }
 *     }
 *
 *     override fun receiveAllActions(action: Action) {
 *         // Called for EVERY action flowing through the system
 *         // Useful for analytics, logging, or global side effects
 *         analytics.trackAction(action.actionId.id)
 *     }
 * }
 * ```
 *
 * @see DataComposerAction
 * @see DataComposerActionHolder
 * @see ListDataComposerViewModel
 */
interface DataComposerActionHandler {

    /**
     * Handle a [DataComposerAction] dispatched from a Store.
     *
     * Called when a Store dispatches a DataComposerAction via `suspendDispatch()`.
     * Implement business logic that needs to be coordinated at the ViewModel level.
     *
     * @param dataComposerActionHolder The action holder containing the action and source store ID
     */
    suspend fun receiveAction(dataComposerActionHolder: DataComposerActionHolder)

    /**
     * Called for every action that flows through the DataComposer.
     *
     * This includes StoreActions, DataComposerActions, and UIComposerActions.
     * Use this for cross-cutting concerns like:
     * - Analytics/event tracking
     * - Logging for debugging
     * - Global state updates based on any action
     *
     * @param action The action being processed
     */
    fun receiveAllActions(action: Action)
}