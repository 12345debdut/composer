package com.debdut.composer.action.holder

import com.debdut.composer.action.StoreAction
import com.debdut.composer.store.StoreId

/**
 * ActionHolder implementation for [StoreAction]s.
 *
 * StoreActionHolders wrap actions that are being dispatched to Stores.
 * They pair the action with a target [StoreId] for routing purposes.
 *
 * ## Usage Context
 * This holder is primarily used internally by the StoreComposer framework
 * when routing actions to specific stores. External code typically works
 * directly with [StoreAction] instances.
 *
 * ## Internal Flow
 * ```
 * dispatch(action, storeId)
 *         │
 *         ▼
 * StoreActionHolder(action, storeId)
 *         │
 *         ▼
 * DataComposer routes to Store with matching ID
 * ```
 *
 * @property action The [StoreAction] being transported
 * @property storeId The target Store's ID for routing
 *
 * @see ActionHolder
 * @see StoreAction
 */
public data class StoreActionHolder(
    override val action: StoreAction,
    override val storeId: StoreId
): ActionHolder
