package com.debdut.composer.composer.data.impl

import com.debdut.composer.composer.data.DataComposerActionHandler
import com.debdut.composer.composer.data.ListWithHeaderAndFooterDataComposer
import com.debdut.composer.state.FooterUIStateType
import com.debdut.composer.state.HeaderUIStateType
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj
import com.debdut.composer.store.factory.StoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Internal implementation of [ListWithHeaderAndFooterDataComposer].
 *
 * Extends [ListDataComposerImpl] with automatic state separation based on
 * [UIState.type]. States are routed to different flows:
 * - [HeaderUIStateType] → [headerState] flow
 * - [FooterUIStateType] → [footerState] flow
 * - Other types → [uiStateFlow] (main list)
 *
 * ## How It Works
 * Overrides [updateList] to intercept state updates and separate them:
 * ```
 * Combined state from parent class
 *         │
 *         ▼
 * updateList() separates by type
 *         │
 *         ├── HeaderUIStateType → _headerState
 *         │
 *         ├── FooterUIStateType → _footerState
 *         │
 *         └── Other types → _uiStateFlow
 * ```
 *
 * ## Usage
 * Created via [listWithHeaderAndFooterDataComposer] factory function:
 * ```kotlin
 * val composer = listWithHeaderAndFooterDataComposer(
 *     storeFactory = factory,
 *     coroutineScope = viewModelScope,
 *     dataComposerActionHandler = handler
 * )
 * ```
 *
 * @param storeFactory Factory for creating Store instances
 * @param coroutineScope Scope for launching coroutines
 * @param dataComposerActionHandler Handler for DataComposerActions
 *
 * @see ListWithHeaderAndFooterDataComposer
 * @see ListDataComposerImpl
 * @see HeaderUIStateType
 * @see FooterUIStateType
 */
internal class ListWithHeaderFooterDataComposerImpl<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> internal constructor(
    storeFactory: StoreFactory<UISTATE, INITDATA, STOREMODEL>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler
):
    ListWithHeaderAndFooterDataComposer<UISTATE, INITDATA, STOREMODEL>,
    ListDataComposerImpl<UISTATE, INITDATA, STOREMODEL>(storeFactory = storeFactory, coroutineScope = coroutineScope, dataComposerActionHandler = dataComposerActionHandler) {

    /** Separate state flow for header widgets. */
    private val _headerState = MutableStateFlow<List<UISTATE>>(value = emptyList())
    override val headerState: StateFlow<List<UISTATE>> = _headerState.asStateFlow()

    /** Separate state flow for footer widgets. */
    private val _footerState = MutableStateFlow<List<UISTATE>>(value = emptyList())
    override val footerState: StateFlow<List<UISTATE>> = _footerState.asStateFlow()

    /**
     * Override to separate states by type before updating flows.
     *
     * Intercepts the combined state list from [ListDataComposerImpl] and
     * routes states to appropriate flows based on their [UIState.type].
     *
     * @param list The combined visible states from all stores
     */
    override fun updateList(list: List<UISTATE>) {
        val headerList = mutableListOf<UISTATE>()
        val footerList = mutableListOf<UISTATE>()
        val originalList = mutableListOf<UISTATE>()
        list.forEach {
            when (it.type) {
                is HeaderUIStateType -> headerList.add(it)
                is FooterUIStateType -> footerList.add(it)
                else -> originalList.add(it)
            }
        }
        _uiStateFlow.update { originalList }
        _headerState.update { headerList }
        _footerState.update { footerList }
    }
}