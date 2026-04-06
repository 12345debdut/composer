package com.debdut.composer.sample.counter

import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.store.StoreId

object CounterWidgetId : WidgetId {
    override val id: String = "counter-widget"
}

object CounterStoreId : StoreId {
    override val id: String = "counter-store"
}
