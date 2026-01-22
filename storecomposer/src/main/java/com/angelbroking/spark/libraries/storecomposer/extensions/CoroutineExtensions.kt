package com.angelbroking.spark.libraries.storecomposer.extensions

import com.angelbroking.spark.libraries.storecomposer.action.holder.ActionHolder
import com.angelbroking.spark.libraries.storecomposer.state.UIState
import com.angelbroking.spark.libraries.storecomposer.store.Store
import com.angelbroking.spark.libraries.storecomposer.store.StoreInitObj
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Internal utility for collecting [ActionHolder]s from multiple [Store]s.
 *
 * This extension function merges side effect flows from multiple stores into
 * a single collection point. Used internally by DataComposer implementations
 * to aggregate UI and DataComposer side effects.
 *
 * ## Internal Usage
 * ```kotlin
 * // In ListDataComposerImpl
 * private fun combineUISideEffects(stores: List<Store<...>>) {
 *     disposables.add(
 *         coroutineScope.collectActionHolder(
 *             list = stores,
 *             property = { uiSideEffects },
 *             block = { holder -> _uiComposerActionHolder.emit(holder) }
 *         )
 *     )
 * }
 * ```
 *
 * @param T The ActionHolder type to collect
 * @param list List of Stores to collect from
 * @param property Selector function to get the SharedFlow from each Store
 * @param block Handler function called for each collected ActionHolder
 * @return A Job that can be cancelled to stop collection
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T: ActionHolder, UISTATE: UIState, INITOBJ: StoreInitObj, STOREMODEL: StoreInitObj> CoroutineScope.collectActionHolder(
    list: List<Store<UISTATE, INITOBJ, STOREMODEL>>,
    property: Store<UISTATE, INITOBJ, STOREMODEL>.() -> SharedFlow<ActionHolder>,
    block: suspend (T) -> Unit
): Job {
    val sideEffects = list.map(property)
    return launch {
        produce {
            sideEffects.forEach { sideEffect ->
                launch {
                    sideEffect.collect {
                        channel.send(it)
                    }
                }
            }
        }.receiveAsFlow().collect { actionHolder ->
            (actionHolder as? T)?.let {
                block.invoke(actionHolder)
            }
        }
    }
}