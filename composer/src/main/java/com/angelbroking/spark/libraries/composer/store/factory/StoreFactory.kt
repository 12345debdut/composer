package com.angelbroking.spark.libraries.composer.store.factory

import com.angelbroking.spark.libraries.composer.composer.ui.WidgetId
import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.store.Store
import com.angelbroking.spark.libraries.composer.store.StoreInitObj

/**
 * Factory interface for creating [Store] instances based on [WidgetId].
 *
 * StoreFactory is a key component that maps widget identifiers to their
 * corresponding Store implementations. The DataComposer uses this factory
 * to create and manage Stores when initializing with a widget list.
 *
 * ## Implementation Pattern
 * Typically implemented with dependency injection to provide Store dependencies:
 *
 * ```kotlin
 * class OrderPadStoreFactoryImpl @Inject constructor(
 *     private val headerStore: Provider<HeaderWidgetStore>,
 *     private val quantityStore: Provider<QuantityWidgetStore>,
 *     private val priceStore: Provider<PriceWidgetStore>,
 *     private val confirmStore: Provider<ConfirmWidgetStore>
 * ) : StoreFactory<OrderPadState, OrderPadInitModel, OrderWidgetModel> {
 *
 *     override fun get(widgetId: WidgetId): Store<OrderPadState, OrderPadInitModel, OrderWidgetModel> {
 *         return when (widgetId) {
 *             HeaderWidgetId -> headerStore.get()
 *             QuantityWidgetId -> quantityStore.get()
 *             PriceWidgetId -> priceStore.get()
 *             ConfirmWidgetId -> confirmStore.get()
 *             is GroupWidget -> get(widgetId.hostId) // Handle groups
 *             else -> throw IllegalArgumentException("Unknown widget: $widgetId")
 *         } as Store<OrderPadState, OrderPadInitModel, OrderWidgetModel>
 *     }
 * }
 * ```
 *
 * ## With Map-Based Lookup
 * For larger features, use a map for cleaner code:
 *
 * ```kotlin
 * class MyStoreFactory @Inject constructor(
 *     headerStore: HeaderStore,
 *     bodyStore: BodyStore,
 *     footerStore: FooterStore
 * ) : StoreFactory<MyState, MyInitModel, MyWidgetModel> {
 *
 *     private val storeMap: Map<WidgetId, Store<...>> = mapOf(
 *         HeaderWidgetId to headerStore,
 *         BodyWidgetId to bodyStore,
 *         FooterWidgetId to footerStore
 *     )
 *
 *     override fun get(widgetId: WidgetId): Store<...> {
 *         return storeMap[widgetId]
 *             ?: throw IllegalArgumentException("No store for: $widgetId")
 *     }
 * }
 * ```
 *
 * ## DI Module Binding
 * ```kotlin
 * @Module
 * @InstallIn(ViewModelComponent::class)
 * interface StoreModule {
 *     @Binds
 *     fun bindStoreFactory(impl: OrderPadStoreFactoryImpl): OrderPadStoreFactory
 * }
 * ```
 *
 * @param UISTATE The base UI state type for all stores in this feature
 * @param INITOBJ The initialization data type
 * @param STOREMODEL The widget model type
 *
 * @see Store
 * @see WidgetId
 * @see DataComposer
 */
interface StoreFactory<UISTATE: UIState, INITOBJ: StoreInitObj, STOREMODEL: StoreInitObj> {

    /**
     * Get or create a [Store] instance for the given [WidgetId].
     *
     * @param widgetId The identifier of the widget requiring a Store
     * @return The Store instance that manages state for this widget
     * @throws IllegalArgumentException if no Store is registered for this widgetId
     */
    fun get(widgetId: WidgetId): Store<UISTATE, INITOBJ, STOREMODEL>
}