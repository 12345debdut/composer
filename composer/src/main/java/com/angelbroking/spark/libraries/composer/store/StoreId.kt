package com.angelbroking.spark.libraries.composer.store

/**
 * Unique identifier interface for [Store] instances.
 *
 * StoreId is used to:
 * - Uniquely identify stores for targeted action dispatch
 * - Track action origins in [ActionHolder] wrappers
 * - Enable inter-store communication
 *
 * ## Implementation Pattern
 * StoreIds are typically implemented as singleton objects:
 *
 * ```kotlin
 * // Define StoreIds as objects
 * object HeaderStoreId : StoreId {
 *     override val id: String = "header_store"
 * }
 *
 * object QuantityStoreId : StoreId {
 *     override val id: String = "quantity_store"
 * }
 *
 * // For features with many stores, use a sealed interface
 * sealed interface OrderPadStoreId : StoreId
 *
 * object HeaderStoreId : OrderPadStoreId {
 *     override val id: String = "header"
 * }
 *
 * object PriceStoreId : OrderPadStoreId {
 *     override val id: String = "price"
 * }
 * ```
 *
 * ## Targeted Dispatch
 * Use StoreId to send actions to specific stores:
 * ```kotlin
 * // In ViewModel
 * dispatch(RefreshAction(), HeaderStoreId)
 *
 * // In Store (to another store)
 * suspendDispatch(NotifyAction(), PriceStoreId)
 * ```
 *
 * @property id A unique string identifier for this store
 *
 * @see Store.storeId
 * @see WidgetId
 */
interface StoreId {
    val id: String

    companion object {
        /**
         * Empty StoreId used when no specific store context is available.
         *
         * Used as default in [DataComposerActionHolder] when actions are not
         * dispatched from a specific Store.
         */
        val Empty = object : StoreId {
            override val id: String = ""
        }
    }
}