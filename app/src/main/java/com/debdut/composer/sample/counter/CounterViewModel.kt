package com.debdut.composer.sample.counter

import androidx.lifecycle.viewModelScope
import com.debdut.composer.action.Action
import com.debdut.composer.action.holder.DataComposerActionHolder
import com.debdut.composer.composer.data.DataComposerActionHandler
import com.debdut.composer.composer.data.syntax.init
import com.debdut.composer.viewmodel.ListDataComposerViewModel
import kotlinx.coroutines.launch

class CounterViewModel : ListDataComposerViewModel<CounterState, CounterInitObj, CounterInitObj>(
    storeFactory = CounterStoreFactory()
) {

    override val dataComposerActionHandler: DataComposerActionHandler = object : DataComposerActionHandler {
        override suspend fun receiveAction(dataComposerActionHolder: DataComposerActionHolder) {
            // Handle DataComposerActions here (e.g., cross-widget coordination)
        }

        override fun receiveAllActions(action: Action) {
            // Called for every action — useful for analytics/logging
        }
    }

    fun initialize() {
        viewModelScope.launch {
            init(
                widgets = listOf(CounterWidgetId),
                initData = CounterInitObj(initialCount = 0, label = "Composer Counter")
            )
        }
    }
}
