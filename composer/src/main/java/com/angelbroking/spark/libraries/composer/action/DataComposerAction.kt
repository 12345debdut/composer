package com.angelbroking.spark.libraries.composer.action

/**
 * Interface for actions that are handled at the DataComposer/ViewModel layer.
 *
 * DataComposerActions are used for cross-widget communication or operations that
 * need to be coordinated at a higher level than individual Stores. They are
 * received by the [DataComposerActionHandler.receiveAction] method, typically
 * implemented in the ViewModel.
 *
 * ## Common Use Cases
 * - Refreshing all widgets simultaneously
 * - Coordinating data between multiple stores
 * - Navigation decisions based on store state
 * - Analytics/logging at the screen level
 * - Triggering API calls from the ViewModel layer
 *
 * ## Usage Example
 * ```kotlin
 * // 1. Define the DataComposerAction
 * object RefreshAllWidgetsActionId : ActionId {
 *     override val id: String = "refresh_all_widgets"
 * }
 *
 * data class RefreshAllWidgetsAction(
 *     val forceRefresh: Boolean = false,
 *     override val actionId: ActionId = RefreshAllWidgetsActionId
 * ) : DataComposerAction
 *
 * // 2. Dispatch from a Store
 * class MyStore : Store<...>() {
 *     override suspend fun receive(action: StoreAction, storeId: StoreId) {
 *         when (action) {
 *             is RefreshButtonClickedAction -> {
 *                 // Notify ViewModel to refresh all widgets
 *                 suspendDispatch(RefreshAllWidgetsAction(forceRefresh = true))
 *             }
 *         }
 *     }
 * }
 *
 * // 3. Handle in ViewModel
 * class MyViewModel : ListDataComposerViewModel<...>(...), DataComposerActionHandler {
 *
 *     override suspend fun receiveAction(holder: DataComposerActionHolder) {
 *         when (val action = holder.action) {
 *             is RefreshAllWidgetsAction -> {
 *                 // Re-fetch data and re-initialize all stores
 *                 val newData = repository.fetchData(action.forceRefresh)
 *                 init(widgets = currentWidgetIds, initData = newData)
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @see ComposerAction
 * @see DataComposerActionHandler
 * @see DataComposerActionHolder
 */
interface DataComposerAction: ComposerAction