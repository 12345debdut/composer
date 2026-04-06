package com.debdut.composer.sample.counter

import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.store.Store
import com.debdut.composer.store.factory.StoreFactory

class CounterStoreFactory : StoreFactory<CounterState, CounterInitObj, CounterInitObj> {

    override fun get(widgetId: WidgetId): Store<CounterState, CounterInitObj, CounterInitObj> {
        return when (widgetId) {
            CounterWidgetId -> CounterStore()
            else -> throw IllegalArgumentException("Unknown widget: $widgetId")
        }
    }
}
