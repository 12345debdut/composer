package com.angelbroking.spark.libraries.composer.action

/**
 * Base interface for all actions in the StoreComposer architecture.
 *
 * Actions are immutable data objects that describe "what happened" in the system.
 * They flow through the architecture to trigger state changes and side effects.
 *
 * ## Action Hierarchy
 * ```
 * Action
 * ├── StoreAction        - Actions handled by Stores
 * └── ComposerAction     - Actions handled by Composers
 *     ├── DataComposerAction   - Actions for DataComposer layer
 *     └── UIComposerAction     - Actions that bubble up to UI
 * ```
 *
 * ## Usage Example
 * ```kotlin
 * // Define an action ID
 * object RefreshDataActionId : ActionId {
 *     override val id: String = "refresh_data"
 * }
 *
 * // Define the action
 * data class RefreshDataAction(
 *     val forceRefresh: Boolean = false,
 *     override val actionId: ActionId = RefreshDataActionId
 * ) : StoreAction
 * ```
 *
 * @property actionId Unique identifier for this action type. Used by Stores to filter
 *                    which actions they should respond to via [Store.subscribedStoreAction].
 *
 * @see ActionId
 * @see StoreAction
 * @see ComposerAction
 */
interface Action {
    val actionId: ActionId
}