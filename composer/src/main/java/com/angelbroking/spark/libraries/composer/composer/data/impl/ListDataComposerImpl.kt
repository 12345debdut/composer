package com.angelbroking.spark.libraries.composer.composer.data.impl

import com.angelbroking.spark.libraries.composer.action.Action
import com.angelbroking.spark.libraries.composer.action.DataComposerAction
import com.angelbroking.spark.libraries.composer.action.StoreAction
import com.angelbroking.spark.libraries.composer.action.UIComposerAction
import com.angelbroking.spark.libraries.composer.action.holder.DataComposerActionHolder
import com.angelbroking.spark.libraries.composer.action.holder.UIComposerActionHolder
import com.angelbroking.spark.libraries.composer.composer.data.DataComposerActionHandler
import com.angelbroking.spark.libraries.composer.composer.data.ListDataComposer
import com.angelbroking.spark.libraries.composer.composer.data.model.StoreActionWidgetIdPair
import com.angelbroking.spark.libraries.composer.composer.ui.ChildWidgetId
import com.angelbroking.spark.libraries.composer.composer.ui.GroupWidget
import com.angelbroking.spark.libraries.composer.composer.ui.HostWidgetId
import com.angelbroking.spark.libraries.composer.composer.ui.NoStoreWidgetId
import com.angelbroking.spark.libraries.composer.composer.ui.WidgetId
import com.angelbroking.spark.libraries.composer.extensions.collectActionHolder
import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.store.Store
import com.angelbroking.spark.libraries.composer.store.StoreId
import com.angelbroking.spark.libraries.composer.store.StoreInitObj
import com.angelbroking.spark.libraries.composer.store.factory.StoreFactory
import com.angelbroking.spark.libraries.composer.store.syntax.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * Internal implementation of [ListDataComposer] for multi-widget screens.
 *
 * This is the core implementation that manages multiple [Store]s, combines their
 * states, routes actions, and handles side effects. It's used by
 * [ListDataComposerViewModel] and extended by [ListWithHeaderFooterDataComposerImpl].
 *
 * ## Key Responsibilities
 * 1. **Store Management**: Creates and manages Store instances for each widget
 * 2. **State Combination**: Merges individual Store states into ordered list
 * 3. **Action Routing**: Routes actions to specific or all subscribed Stores
 * 4. **Side Effect Forwarding**: Collects and forwards UI/DataComposer actions
 * 5. **Visibility Handling**: Filters hidden widgets from the emitted state
 * 6. **GroupWidget Support**: Handles hierarchical widget visibility
 *
 * ## Widget Types Supported
 * - Regular widgets with Stores
 * - [NoStoreWidgetId]: Static widgets without Stores
 * - [GroupWidget]: Hierarchical groups with host and children
 * - [ChildWidgetId]: Static children within groups
 *
 * ## Thread Safety
 * Uses Mutex and ConcurrentHashMap for thread-safe state management when
 * multiple coroutines update state simultaneously.
 *
 * @param storeFactory Factory for creating Store instances
 * @param coroutineScope Scope for launching coroutines (typically viewModelScope)
 * @param dataComposerActionHandler Handler for DataComposerActions
 *
 * @see ListDataComposer
 * @see ListWithHeaderFooterDataComposerImpl
 * @see SingleDataComposerImpl
 */
internal open class ListDataComposerImpl<UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL: StoreInitObj> internal constructor(
    private val storeFactory: StoreFactory<UISTATE, INITDATA, STOREMODEL>,
    private val coroutineScope: CoroutineScope,
    private val dataComposerActionHandler: DataComposerActionHandler
) : ListDataComposer<UISTATE, INITDATA, STOREMODEL> {

    protected val _uiStateFlow = MutableStateFlow<List<UISTATE>>(emptyList())
    override val uiStateFlow: StateFlow<List<UISTATE>> = _uiStateFlow.asStateFlow()

    private val _uiComposerActionHolder = MutableSharedFlow<UIComposerActionHolder>()
    override val uiActionHolder: SharedFlow<UIComposerActionHolder> =
        _uiComposerActionHolder
            .onEach {
                dataComposerActionHandler.receiveAllActions(it.action)
            }.shareIn(coroutineScope, started = SharingStarted.WhileSubscribed())

    private val stores: MutableMap<StoreId, MutableList<Store<UISTATE, INITDATA, STOREMODEL>>> = mutableMapOf()

    private val widgetIdToStoreId: ConcurrentHashMap<WidgetId, StoreId> = ConcurrentHashMap()

    private val groupInfoMap: ConcurrentHashMap<HostWidgetId, GroupInfo> = ConcurrentHashMap()

    data class GroupInfo(
        val topChildren: List<ChildWidgetId> = emptyList(),
        val bottomChildren: List<ChildWidgetId> = emptyList()
    )

    private val disposables = mutableListOf<Job>()

    private val storeMutex = Mutex()

    override suspend fun initialiseWithWidgets(widgets: List<WidgetId>, initObj: INITDATA) {
        reloadWithWidgetsInternal(widgets) {
            initialise(initObj)
        }
    }

    override suspend fun updateWidgets(widgets: List<WidgetId>, initobj: INITDATA) {
        val currentWidgetIds = widgetIdToStoreId.keys.toSet()
        widgets.forEach { widgetId ->
            if (widgetId in currentWidgetIds) {
                val storeId = widgetIdToStoreId[widgetId] ?: return@forEach
                val stores = storeMutex.withLock {
                    stores[storeId]?.toList().orEmpty()
                }
                if (stores.isEmpty()) return@forEach
                stores.forEach { it.initialise(initobj) }
            }
        }
    }

    override fun initialiseWithInitModel(initObj: INITDATA) {
        coroutineScope.launch {
            safeStoresFlatten().forEach {
                it.initialise(initObj)
            }
        }
    }

    private suspend fun reloadWithWidgetsInternal(
        widgets: List<WidgetId>,
        updateCallBack: Store<UISTATE, INITDATA, STOREMODEL>.() -> Unit
    ) = storeMutex.withLock {
        groupInfoMap.clear()
        stores.forEach {
            for(store in it.value) {
                store.clear()
            }
        }
        val newStores = mutableListOf<IntermediateWidgetStoreModel<UISTATE, INITDATA, STOREMODEL>>()
        val newStoreToWidgetId = ConcurrentHashMap<WidgetId, StoreId>()
        widgets.forEach { widgetId ->
            when (widgetId) {
                is NoStoreWidgetId -> {
                    newStores.add(
                        IntermediateWidgetStoreModel(
                            widgetId = widgetId,
                            store = null
                        )
                    )
                }
                is GroupWidget -> {
                    val store = storeFactory.get(widgetId.hostId).apply {
                        updateCoroutineScope(this@ListDataComposerImpl.coroutineScope)
                        updateCallBack()
                    }
                    groupInfoMap[widgetId.hostId] = GroupInfo(topChildren = widgetId.topChildren, bottomChildren = widgetId.bottomChildren)
                    newStores.add(
                        IntermediateWidgetStoreModel(
                            widgetId = widgetId,
                            store = store
                        )
                    )
                    newStoreToWidgetId[widgetId.hostId] = store.storeId
                }
                else -> {
                    val store = storeFactory.get(widgetId).apply {
                        updateCoroutineScope(this@ListDataComposerImpl.coroutineScope)
                        updateCallBack()
                    }
                    newStores.add(
                        IntermediateWidgetStoreModel(
                            widgetId = widgetId,
                            store = store
                        )
                    )
                    newStoreToWidgetId[widgetId] = store.storeId
                }
            }
        }
        stores.clear()
        newStoreToWidgetId.forEach { (_, storeId) ->
            newStores.firstOrNull { it.store?.storeId == storeId }?.store?.let {
                stores.getOrPut(storeId) { mutableListOf() }.add(it)
            }
        }
        widgetIdToStoreId.clear()
        widgetIdToStoreId.putAll(newStoreToWidgetId)
        disposables.forEach { it.cancel() }
        disposables.clear()
        callResetFunctionForAllStores(newStores)
        combineStates(newStores)
        combineUISideEffects(newStores)
        combineGlobalSideEffects(newStores)
    }

    private fun callResetFunctionForAllStores(newStores: List<IntermediateWidgetStoreModel<UISTATE, INITDATA, STOREMODEL>>) {
        for (newStore in newStores) {
            newStore.store?.reset()
        }
    }

    private suspend fun updateUISideEffects(actionHolder: UIComposerActionHolder) {
        _uiComposerActionHolder.emit(actionHolder)
    }

    override fun dispatch(action: Action) {
        coroutineScope.launch {
            suspendDispatch(action)
        }
    }

    private suspend fun safeStoresFlatten(): List<Store<UISTATE, INITDATA, STOREMODEL>> = storeMutex.withLock {
        stores.values.flatten().toList()
    }

    private suspend fun safeGetStoresForId(storeId: StoreId): List<Store<UISTATE, INITDATA, STOREMODEL>> = storeMutex.withLock {
        stores[storeId]?.toList().orEmpty()
    }

    override suspend fun suspendDispatch(action: Action) {
        when (action) {
            is StoreAction -> {
                val storeSnapshot = safeStoresFlatten()
                internalDispatchToStores(stores = storeSnapshot, action = action)
            }
            is UIComposerAction -> {
                _uiComposerActionHolder.emit(
                    UIComposerActionHolder(
                        action = action,
                        storeId = StoreId.Empty
                    )
                )
            }

            is DataComposerAction -> {
                dataComposerActionHandler.receiveAction(DataComposerActionHolder(action = action))
            }
        }
        dataComposerActionHandler.receiveAllActions(action)
    }

    override suspend fun suspendDispatchToStore(action: StoreAction, storeId: StoreId) {
        val storeSnapshot = safeGetStoresForId(storeId)
        internalDispatchToStores(action = action, stores = storeSnapshot)
        dataComposerActionHandler.receiveAllActions(action)
    }

    override suspend fun suspendDispatchToWidget(action: StoreAction, widgetId: WidgetId) {
        val storeId = widgetIdToStoreId[widgetId] ?: return
        val storeSnapshot = safeGetStoresForId(storeId)
        internalDispatchToStores(stores = storeSnapshot, action = action)
        dataComposerActionHandler.receiveAllActions(action)
    }

    override suspend fun suspendBatchDispatchToWidget(storeActionWidgetIdPairList: List<StoreActionWidgetIdPair>) {
        withContext(Dispatchers.Default) {
            val localCopyStores: Map<StoreId, List<Store<UISTATE, INITDATA, STOREMODEL>>> = storeMutex.withLock {
                stores.mapValues { (_, list) -> list.toList() }
            }
            if (localCopyStores.isEmpty()) return@withContext

            val jobs = storeActionWidgetIdPairList.mapNotNull { actionWidgetId ->
                widgetIdToStoreId[actionWidgetId.widgetId]?.let { storeId ->
                    localCopyStores[storeId]?.takeIf { it.isNotEmpty() }?.map { store ->
                        async { store.send(actionWidgetId.action) }
                    }
                }
            }.flatten()
            jobs.awaitAll()
        }
    }

    override fun currentWidgetIds(): List<WidgetId> {
        return widgetIdToStoreId.entries.map { it.key }
    }

    override fun dispose() {
        disposables.forEach { it.cancel() }
        disposables.clear()
        stores.values.flatten().forEach { it.clear() }
        stores.clear()
        widgetIdToStoreId.clear()
        groupInfoMap.clear()
    }

    override fun dispatchToStore(action: StoreAction, storeId: StoreId) {
        coroutineScope.launch {
            suspendDispatchToStore(action, storeId)
        }
    }

    override fun dispatchToWidget(action: StoreAction, widgetId: WidgetId) {
        coroutineScope.launch {
            suspendDispatchToWidget(action, widgetId)
        }
    }

    private suspend fun internalDispatchToStores(stores: List<Store<UISTATE, INITDATA, STOREMODEL>>, action: StoreAction) = coroutineScope {
        if (stores.isEmpty()) return@coroutineScope
        stores.map { store -> async { store.send(action) } }.awaitAll()
    }

    protected open fun updateList(list: List<UISTATE>) {
        _uiStateFlow.update { list }
    }

    private fun combineStates(stores: List<IntermediateWidgetStoreModel<UISTATE, INITDATA, STOREMODEL>>) {
        disposables.add(coroutineScope.launch {
            val stateFlows: List<StateFlow<UIState?>> = buildList {
                stores.forEach { (widgetId, store) ->
                    when (widgetId) {
                        is NoStoreWidgetId -> add(MutableStateFlow(widgetId.uiState))
                        is GroupWidget -> {
                            widgetId.topChildren.forEach { childWidgetId ->
                                add(MutableStateFlow(childWidgetId.uiState))
                            }
                            store?.let { add(store.uiStateFlow) }
                            widgetId.bottomChildren.forEach { childWidgetId ->
                                add(MutableStateFlow(childWidgetId.uiState))
                            }
                        }
                        else -> store?.let { add(store.uiStateFlow) }
                    }
                }
            }
            combine(stateFlows) { array ->
                array.toList()
            }
                .map { listOfState ->
                    val mutableList = listOfState.toMutableList()
                    // Iterate in reverse order (highest index first) to avoid index invalidation
                    // This eliminates the need for sorting and intermediate list creation
                    for (index in listOfState.indices.reversed()) {
                        val state = listOfState[index] ?: continue
                        when (val widgetId = state.widgetId) {
                            is HostWidgetId -> {
                                if (!(state.visible)) {
                                    val child = groupInfoMap[widgetId] ?: continue
                                    mutableList.removeAroundGroup(
                                        groupIndex = index,
                                        topChildrenCount = child.topChildren.size,
                                        bottomChildrenCount = child.bottomChildren.size
                                    )
                                }
                            }
                            else -> { /* no-op */ }
                        }
                    }
                    mutableList.filter { it?.visible == true }
                }
                .filterIsInstance<List<UISTATE>>()
                .collect { filteredStates ->
                    updateList(filteredStates)
                }
        })
    }

    private fun <T> MutableList<T>.removeAroundGroup(
        groupIndex: Int,
        topChildrenCount: Int,
        bottomChildrenCount: Int
    ) {
        if (groupIndex !in indices) return

        // 1. Remove items AFTER the group (bottom children)
        val bottomStart = groupIndex + 1
        val bottomEndExclusive = minOf(size, bottomStart + bottomChildrenCount)
        if (bottomStart < bottomEndExclusive && bottomChildrenCount > 0) {
            subList(bottomStart, bottomEndExclusive).clear()
        }

        // 2. Remove items BEFORE the group (top children)
        //    After removing bottom children, the groupIndex remains valid (items before it haven't changed).
        //    However, we need to ensure the groupIndex is still within bounds.
        if (topChildrenCount > 0 && groupIndex < size) {
            val topStart = maxOf(0, groupIndex - topChildrenCount)
            if (topStart < groupIndex) {
                subList(topStart, groupIndex).clear()
            }
        }
    }

    private fun combineUISideEffects(list: List<IntermediateWidgetStoreModel<UISTATE, INITDATA, STOREMODEL>>) {
        disposables.add(coroutineScope.collectActionHolder(
                list.mapNotNull { it.store },
                { uiSideEffects },
                ::updateUISideEffects
            ))
    }

    private fun combineGlobalSideEffects(list: List<IntermediateWidgetStoreModel<UISTATE, INITDATA, STOREMODEL>>) {
        disposables.add(
            coroutineScope.collectActionHolder(
                list.mapNotNull { it.store },
                { composerSideEffects },
                dataComposerActionHandler::receiveAction
            )
        )
    }
}