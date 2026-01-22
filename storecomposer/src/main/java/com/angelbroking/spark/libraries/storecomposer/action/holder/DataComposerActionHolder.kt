package com.angelbroking.spark.libraries.storecomposer.action.holder

import com.angelbroking.spark.libraries.storecomposer.action.DataComposerAction
import com.angelbroking.spark.libraries.storecomposer.composer.data.DataComposerActionHandler
import com.angelbroking.spark.libraries.storecomposer.store.StoreId

/**
 * ActionHolder implementation for [DataComposerAction]s.
 *
 * DataComposerActionHolders wrap actions that should be handled at the ViewModel/DataComposer
 * level. They are delivered to [DataComposerActionHandler.receiveAction].
 *
 * ## How It's Created
 * When a Store dispatches a DataComposerAction via `suspendDispatch()`, the system
 * automatically wraps it in a DataComposerActionHolder with the Store's ID.
 *
 * ## Usage Example
 * ```kotlin
 * // In ViewModel implementing DataComposerActionHandler
 * override suspend fun receiveAction(holder: DataComposerActionHolder) {
 *     when (val action = holder.action) {
 *         is NavigateToDetailsAction -> {
 *             _navigationEvent.emit(NavigationTarget.Details(action.itemId))
 *         }
 *         is RefreshAllAction -> {
 *             // The storeId tells us which store requested the refresh
 *             log("Refresh requested by: ${holder.storeId.id}")
 *             refreshAllWidgets()
 *         }
 *     }
 * }
 * ```
 *
 * @property action The [DataComposerAction] being transported
 * @property storeId The ID of the Store that dispatched this action.
 *                   Defaults to [StoreId.Empty] if not dispatched from a Store.
 *
 * @see ActionHolder
 * @see DataComposerAction
 * @see DataComposerActionHandler
 */
data class DataComposerActionHolder(
    override val action: DataComposerAction,
    override val storeId: StoreId = StoreId.Empty
): ActionHolder
