package com.debdut.composer.composer.data

import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj

/**
 * [DataComposer] variant for screens with a single widget.
 *
 * SingleDataComposer manages exactly one [Store] and is optimized for
 * simple screens that don't need list-based state management.
 *
 * ## Use Cases
 * - Detail screens with one main widget
 * - Simple form screens
 * - Settings panels with a single configuration widget
 *
 * ## Usage
 * ```kotlin
 * // ViewModel
 * class DetailViewModel @Inject constructor(
 *     storeFactory: DetailStoreFactory
 * ) : ViewModel(), SingleDataComposerHost<DetailState, DetailInitModel, DetailWidgetModel> {
 *
 *     override val container: SingleDataComposer<...> by lazy {
 *         singleDataComposer(storeFactory, viewModelScope, actionHandler)
 *     }
 *
 *     fun init(itemId: String) {
 *         viewModelScope.launch {
 *             container.init(listOf(DetailWidgetId), DetailInitModel(itemId))
 *         }
 *     }
 * }
 *
 * // Fragment - observe single state
 * container.observeState(lifecycleScope) { state ->
 *     renderDetail(state)
 * }
 * ```
 *
 * ## State Observation
 * Use [SingleDataComposerHostSyntax.observeState] for convenient single-state observation.
 *
 * @see DataComposer
 * @see ListDataComposer
 * @see SingleDataComposerHost
 */
public interface SingleDataComposer<UISTATE: UIState, INITOBJ: StoreInitObj, STOREMODEL: StoreInitObj>: DataComposer<UISTATE, INITOBJ, STOREMODEL>