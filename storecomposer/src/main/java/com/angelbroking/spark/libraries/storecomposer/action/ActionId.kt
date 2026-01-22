package com.angelbroking.spark.libraries.storecomposer.action

/**
 * Unique identifier interface for [Action] types.
 *
 * Each action type should have a corresponding ActionId object that uniquely identifies it.
 * Stores use ActionIds in their [Store.subscribedStoreAction] set to declare which actions
 * they respond to.
 *
 * ## Implementation Pattern
 * ActionIds are typically implemented as singleton objects:
 *
 * ```kotlin
 * // Define an ActionId as an object
 * object QuantityChangedActionId : ActionId {
 *     override val id: String = "quantity_changed"
 * }
 *
 * // Use it in the corresponding action
 * data class QuantityChangedAction(
 *     val newQuantity: Int,
 *     override val actionId: ActionId = QuantityChangedActionId
 * ) : StoreAction
 *
 * // Store declares it can handle this action
 * class QuantityStore : Store<...>() {
 *     override val subscribedStoreAction: Set<ActionId> = setOf(
 *         QuantityChangedActionId
 *     )
 * }
 * ```
 *
 * ## Best Practices
 * - Use descriptive, unique ID strings (e.g., "widget_name_action_type")
 * - Keep ActionId objects close to their corresponding Action classes
 * - Consider using sealed interfaces to group related ActionIds
 *
 * @property id A unique string identifier for this action type
 *
 * @see Action
 * @see Store.subscribedStoreAction
 */
interface ActionId {
    val id: String
}