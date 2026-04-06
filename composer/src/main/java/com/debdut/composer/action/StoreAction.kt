package com.debdut.composer.action

/**
 * Interface for actions that are dispatched to and handled by [Store]s.
 *
 * StoreActions are the most common action type in the StoreComposer architecture.
 * They represent user interactions, data updates, or any event that should trigger
 * a state change in one or more Stores.
 *
 * ## How StoreActions Work
 * 1. UI dispatches a StoreAction via `dispatch(action)` or `suspendDispatch(action)`
 * 2. DataComposer routes the action to all Stores
 * 3. Each Store checks if the action's [ActionId] is in its [Store.subscribedStoreAction] set
 * 4. If subscribed, the Store's `receive()` method is called
 * 5. Store updates its state using `updateState { ... }`
 *
 * ## Usage Example
 * ```kotlin
 * // 1. Define the ActionId
 * object IncrementQuantityActionId : ActionId {
 *     override val id: String = "increment_quantity"
 * }
 *
 * // 2. Define the StoreAction
 * data class IncrementQuantityAction(
 *     val amount: Int = 1,
 *     override val actionId: ActionId = IncrementQuantityActionId
 * ) : StoreAction
 *
 * // 3. Store subscribes to and handles the action
 * class QuantityStore : Store<QuantityState, InitModel, WidgetModel>() {
 *
 *     override val subscribedStoreAction: Set<ActionId> = setOf(
 *         IncrementQuantityActionId
 *     )
 *
 *     override suspend fun receive(action: StoreAction, storeId: StoreId) {
 *         when (action) {
 *             is IncrementQuantityAction -> {
 *                 updateState {
 *                     copy(quantity = quantity + action.amount)
 *                 }
 *             }
 *         }
 *     }
 * }
 *
 * // 4. Dispatch from UI or ViewModel
 * viewModel.dispatch(IncrementQuantityAction(amount = 5))
 * ```
 *
 * ## Dispatching Variants
 * - `dispatch(action)` - Send to all subscribed stores
 * - `dispatch(action, storeId)` - Send to specific store by ID
 * - `dispatch(action, widgetId)` - Send to store associated with widget
 *
 * @see Action
 * @see Store.subscribedStoreAction
 * @see Store.receive
 */
public interface StoreAction: Action