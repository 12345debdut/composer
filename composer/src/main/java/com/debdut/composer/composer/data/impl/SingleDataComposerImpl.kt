package com.debdut.composer.composer.data.impl

import com.debdut.composer.action.Action
import com.debdut.composer.action.DataComposerAction
import com.debdut.composer.action.StoreAction
import com.debdut.composer.action.UIComposerAction
import com.debdut.composer.action.holder.DataComposerActionHolder
import com.debdut.composer.action.holder.UIComposerActionHolder
import com.debdut.composer.composer.data.DataComposerActionHandler
import com.debdut.composer.composer.data.SingleDataComposer
import com.debdut.composer.composer.data.model.StoreActionWidgetIdPair
import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.state.UIState
import com.debdut.composer.store.Store
import com.debdut.composer.store.StoreId
import com.debdut.composer.store.StoreInitObj
import com.debdut.composer.store.factory.StoreFactory
import com.debdut.composer.store.syntax.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Internal implementation of [SingleDataComposer] for single-widget screens.
 *
 * Manages exactly one [Store] instance and provides simplified state management
 * for screens that don't need list-based composition.
 *
 * ## Internal Responsibilities
 * - Create and manage a single Store via [StoreFactory]
 * - Collect and emit the single store's UI state
 * - Route actions to the store
 * - Forward side effects to UI layer and action handler
 *
 * ## Key Differences from ListDataComposerImpl
 * - Only manages one store (not a list)
 * - Simpler state combination logic
 * - Optimized for single-widget use cases
 *
 * @param coroutineScope The scope for launching coroutines
 * @param storeFactory Factory for creating the Store
 * @param dataComposerActionHandler Handler for DataComposerActions
 *
 * @see SingleDataComposer
 * @see ListDataComposerImpl
 */
internal class SingleDataComposerImpl<UISTATE : UIState, INITDATA : StoreInitObj, STOREMODEL : StoreInitObj>(
    private val coroutineScope: CoroutineScope,
    private val storeFactory: StoreFactory<UISTATE, INITDATA, STOREMODEL>,
    private val dataComposerActionHandler: DataComposerActionHandler
) : SingleDataComposer<UISTATE, INITDATA, STOREMODEL> {
    private val _uiStateFlow: MutableStateFlow<List<UISTATE>> = MutableStateFlow(emptyList())
    override val uiStateFlow: StateFlow<List<UISTATE>> = _uiStateFlow.asStateFlow()

    private val _uiComposerActionHolder = MutableSharedFlow<UIComposerActionHolder>()
    override val uiActionHolder: SharedFlow<UIComposerActionHolder> =
        _uiComposerActionHolder.asSharedFlow()

    private var store: Store<UISTATE, INITDATA, STOREMODEL>? = null

    private var widgetId: WidgetId? = null

    private val disposables = mutableListOf<Job>()

    override suspend fun suspendDispatch(action: Action) {
        when (action) {
            is StoreAction -> {
                store?.send(action)
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
    }

    override suspend fun suspendDispatchToWidget(action: StoreAction, widgetId: WidgetId) {
        if (widgetId != this.widgetId) return
        store?.send(action)
    }

    override suspend fun suspendBatchDispatchToWidget(storeActionWidgetIdPairList: List<StoreActionWidgetIdPair>) {
        withContext(Dispatchers.Default) {
            storeActionWidgetIdPairList
                .filter { data ->
                    data.widgetId == this@SingleDataComposerImpl.widgetId
                }.mapNotNull { data ->
                    store?.let { s -> async { s.send(data.action) } }
                }.awaitAll()
        }
    }

    override suspend fun suspendDispatchToStore(action: StoreAction, storeId: StoreId) {
        store?.send(action)
    }

    override fun currentWidgetIds(): List<WidgetId> {
        return listOfNotNull(widgetId)
    }

    override fun dispose() {
        disposables.forEach { it.cancel() }
        store = null
        widgetId = null
    }

    override fun dispatch(action: Action) {
        coroutineScope.launch {
            suspendDispatch(action)
        }
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

    override suspend fun initialiseWithWidgets(
        widgets: List<WidgetId>,
        initObj: INITDATA
    ) {
        reloadWithTemplatesInternal(widgets) {
            initialise(initObj)
        }
    }

    override suspend fun updateWidgets(widgets: List<WidgetId>, initobj: INITDATA) {
        val widgetId = widgets.firstOrNull() ?: return
        if (widgetId != this.widgetId) return
        store?.initialise(globalModel = initobj)
    }

    override fun initialiseWithInitModel(initObj: INITDATA) {
        store?.initialise(initObj)
    }
    private fun reloadWithTemplatesInternal(
        widgets: List<WidgetId>,
        updateCallBack: Store<UISTATE, INITDATA, STOREMODEL>.() -> Unit
    ) {
        val widgetId = widgets.firstOrNull() ?: return
        this.widgetId = widgetId
        val store = storeFactory.get(widgetId).apply {
            updateCoroutineScope(this@SingleDataComposerImpl.coroutineScope)

        }
        disposables.forEach { it.cancel() }
        disposables.clear()
        store.updateCallBack()
        this.store = store
        combineStates(store)
        combineUISideEffects(store)
        combineComposerSideEffects(store)
    }

    private fun combineStates(store: Store<UISTATE, INITDATA, STOREMODEL>) {
        disposables.add(coroutineScope.launch {
            store.uiStateFlow.filterNotNull().collect { uiState ->
                _uiStateFlow.update {
                    listOf(uiState)
                }
            }
        })
    }

    private fun combineUISideEffects(store: Store<UISTATE, INITDATA, STOREMODEL>) {
        disposables.add(
            coroutineScope.launch {
                store.uiSideEffects.collect {
                    _uiComposerActionHolder.emit(it)
                }
            }
        )
    }

    private fun combineComposerSideEffects(store: Store<UISTATE, INITDATA, STOREMODEL>) {
        disposables.add(
            coroutineScope.launch {
                store.composerSideEffects.collect {
                    dataComposerActionHandler.receiveAction(it)
                }
            }
        )
    }
}