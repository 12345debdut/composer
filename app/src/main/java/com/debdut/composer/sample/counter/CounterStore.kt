package com.debdut.composer.sample.counter

import com.debdut.composer.action.ActionId
import com.debdut.composer.action.StoreAction
import com.debdut.composer.store.Store
import com.debdut.composer.store.StoreId
import com.debdut.composer.store.syntax.currentState
import com.debdut.composer.store.syntax.emitState
import com.debdut.composer.store.syntax.suspendDispatch
import com.debdut.composer.store.syntax.updateState

class CounterStore : Store<CounterState, CounterInitObj, CounterInitObj>() {

    override val storeId: StoreId = CounterStoreId

    override val subscribedStoreAction: Set<ActionId> = setOf(
        IncrementActionId,
        DecrementActionId,
        ResetActionId
    )

    override fun initialise(globalModel: CounterInitObj) {
        emitState {
            CounterState(
                count = globalModel.initialCount,
                label = globalModel.label
            )
        }
    }

    override suspend fun receive(action: StoreAction, storeId: StoreId) {
        when (action) {
            is IncrementAction -> {
                val newState = updateState { copy(count = count + 1) }
                // Demonstrate side effect dispatch
                newState?.let { suspendDispatch(ShowToastAction("Count: ${it.count}")) }
            }
            is DecrementAction -> {
                updateState { copy(count = (count - 1).coerceAtLeast(0)) }
            }
            is ResetAction -> {
                updateState { copy(count = 0) }
            }
        }
    }
}
