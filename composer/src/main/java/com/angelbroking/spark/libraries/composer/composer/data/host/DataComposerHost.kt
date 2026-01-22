package com.angelbroking.spark.libraries.composer.composer.data.host

import com.angelbroking.spark.libraries.composer.composer.data.DataComposer
import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.store.StoreInitObj

/**
 * Interface for classes that host a [DataComposer].
 *
 * DataComposerHost is typically implemented by ViewModels to provide access
 * to the underlying DataComposer. The extension functions in `DataComposerHostSyntax.kt`
 * provide a clean DSL for interacting with the composer.
 *
 * ## Implementation Pattern
 * ViewModels extend the base ViewModel classes that implement this interface:
 *
 * ```kotlin
 * @HiltViewModel
 * class MyViewModel @Inject constructor(
 *     storeFactory: MyStoreFactory
 * ) : ListDataComposerViewModel<MyState, MyInitModel, MyWidgetModel>(storeFactory) {
 *     // container is provided by the base class
 * }
 * ```
 *
 * ## Manual Implementation
 * If not using base ViewModels:
 *
 * ```kotlin
 * class CustomViewModel : ViewModel(), DataComposerHost<MyState, MyInitModel, MyWidgetModel> {
 *
 *     override val container: DataComposer<MyState, MyInitModel, MyWidgetModel> by lazy {
 *         listDataComposer(storeFactory, viewModelScope, actionHandler)
 *     }
 * }
 * ```
 *
 * ## DSL Access via Syntax Extensions
 * ```kotlin
 * // Initialize
 * container.init(widgets, initData)
 *
 * // Dispatch actions
 * container.dispatch(action)
 * container.suspendDispatch(action, widgetId)
 *
 * // Observe state
 * container.observeAsState(scope) { states -> render(states) }
 * ```
 *
 * @property container The underlying [DataComposer] instance
 *
 * @see SingleDataComposerHost
 * @see ListDataComposerHost
 * @see ListWithHeaderAndFooterDataComposerHost
 * @see DataComposerHostSyntax
 */
interface DataComposerHost<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> {
    val container: DataComposer<UISTATE, INITDATA,STOREMODEL>
}