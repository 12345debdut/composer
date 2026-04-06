package com.debdut.composer.composer.data.impl

import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.composer.ui.NoStoreWidgetId
import com.debdut.composer.state.UIState
import com.debdut.composer.store.Store
import com.debdut.composer.store.StoreInitObj

/**
 * Internal model that pairs a [WidgetId] with its associated [Store].
 *
 * Used internally by [ListDataComposerImpl] to track the relationship between
 * widgets and their stores. The store may be null for [NoStoreWidgetId] widgets
 * that don't require state management.
 *
 * ## Internal Usage
 * ```kotlin
 * // Inside ListDataComposerImpl
 * private var stores: List<IntermediateWidgetStoreModel<...>> = emptyList()
 *
 * // Create model for each widget
 * val model = IntermediateWidgetStoreModel(
 *     widgetId = widgetId,
 *     store = if (widgetId is NoStoreWidgetId) null else storeFactory.get(widgetId)
 * )
 * ```
 *
 * @property widgetId The widget identifier
 * @property store The Store instance, or null for static widgets
 */
internal data class IntermediateWidgetStoreModel<UISTATE: UIState, INITMODEL: StoreInitObj, STOREMODEL: StoreInitObj>(
    val widgetId: WidgetId,
    val store: Store<UISTATE, INITMODEL, STOREMODEL>?
)
