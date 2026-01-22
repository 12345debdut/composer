package com.angelbroking.spark.libraries.storecomposer.action.holder

import com.angelbroking.spark.libraries.storecomposer.action.Action
import com.angelbroking.spark.libraries.storecomposer.store.StoreId

/**
 * Wrapper interface that pairs an [Action] with its originating [StoreId].
 *
 * ActionHolders are used to transport actions between layers while preserving
 * context about which Store dispatched the action. This is useful for:
 * - Debugging: Knowing which Store initiated an action
 * - Targeted responses: ViewModel can respond differently based on source
 * - Logging/Analytics: Tracking action flow through the system
 *
 * ## ActionHolder Types
 * ```
 * ActionHolder
 * ├── DataComposerActionHolder  - Wraps DataComposerAction
 * ├── StoreActionHolder         - Wraps StoreAction
 * └── UIComposerActionHolder    - Wraps UIComposerAction
 * ```
 *
 * ## Usage Example
 * ```kotlin
 * // In DataComposerActionHandler (ViewModel)
 * override suspend fun receiveAction(holder: DataComposerActionHolder) {
 *     val action = holder.action
 *     val sourceStore = holder.storeId
 *
 *     when (action) {
 *         is RefreshRequestAction -> {
 *             // Log which store requested the refresh
 *             analytics.log("Refresh requested by ${sourceStore.id}")
 *             performRefresh()
 *         }
 *     }
 * }
 *
 * // In Fragment (handling UI actions)
 * override fun handleUIAction(holder: UIComposerActionHolder) {
 *     val action = holder.action
 *     val sourceStore = holder.storeId
 *
 *     when (action) {
 *         is ShowToastAction -> showToast(action.message)
 *     }
 * }
 * ```
 *
 * @property action The wrapped action instance
 * @property storeId The ID of the Store that dispatched this action (may be [StoreId.Empty])
 *
 * @see DataComposerActionHolder
 * @see StoreActionHolder
 * @see UIComposerActionHolder
 */
interface ActionHolder {
    val action: Action
    val storeId: StoreId
}