package com.debdut.composer.extensions

import com.debdut.composer.ExperimentalComposerApi
import com.debdut.composer.composer.data.DataComposerActionHandler
import com.debdut.composer.composer.data.ListDataComposer
import com.debdut.composer.composer.data.ListWithHeaderAndFooterDataComposer
import com.debdut.composer.composer.data.SingleDataComposer
import com.debdut.composer.composer.data.impl.ListDataComposerImpl
import com.debdut.composer.composer.data.impl.ListWithHeaderFooterDataComposerImpl
import com.debdut.composer.composer.data.impl.SingleDataComposerImpl
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj
import com.debdut.composer.store.factory.StoreFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Factory functions for creating DataComposer instances.
 *
 * These functions create the appropriate DataComposer implementation with
 * the provided dependencies. They are used by base ViewModel classes or
 * when creating custom ViewModel implementations.
 *
 * ## Usage
 * ```kotlin
 * class MyViewModel : ViewModel(), ListDataComposerHost<...> {
 *     override val container: ListDataComposer<...> by lazy {
 *         listDataComposer(
 *             storeFactory = storeFactory,
 *             coroutineScope = viewModelScope,
 *             dataComposerActionHandler = this
 *         )
 *     }
 * }
 * ```
 */

/**
 * Create a [ListDataComposer] for list-based widget screens.
 *
 * @param storeFactory Factory that creates Store instances for each WidgetId
 * @param coroutineScope Scope for coroutines (typically viewModelScope)
 * @param dataComposerActionHandler Handler for DataComposerActions
 * @param dispatcher CoroutineDispatcher for parallel operations (default: Dispatchers.Default)
 * @return A new ListDataComposer instance
 */
@ExperimentalComposerApi
public fun <UISTATE: UIState, INITDATA: StoreInitObj> listDataComposer(
    storeFactory: StoreFactory<UISTATE, INITDATA>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): ListDataComposer<UISTATE, INITDATA> = ListDataComposerImpl(
    storeFactory = storeFactory,
    coroutineScope = coroutineScope,
    dataComposerActionHandler = dataComposerActionHandler,
    dispatcher = dispatcher
)

/**
 * Create a [SingleDataComposer] for single-widget screens.
 *
 * @param storeFactory Factory that creates Store instances for each WidgetId
 * @param coroutineScope Scope for coroutines (typically viewModelScope)
 * @param dataComposerActionHandler Handler for DataComposerActions
 * @param dispatcher CoroutineDispatcher for parallel operations (default: Dispatchers.Default)
 * @return A new SingleDataComposer instance
 */
@ExperimentalComposerApi
public fun <UISTATE: UIState, INITDATA: StoreInitObj> singleDataComposer(
    storeFactory: StoreFactory<UISTATE, INITDATA>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): SingleDataComposer<UISTATE, INITDATA> = SingleDataComposerImpl(
    coroutineScope = coroutineScope,
    storeFactory = storeFactory,
    dataComposerActionHandler = dataComposerActionHandler,
    dispatcher = dispatcher
)

/**
 * Create a [ListWithHeaderAndFooterDataComposer] for screens with header/footer.
 *
 * This composer automatically separates states based on their UIStateType:
 * - HeaderUIStateType → headerState flow
 * - FooterUIStateType → footerState flow
 * - Other types → uiStateFlow
 *
 * @param storeFactory Factory that creates Store instances for each WidgetId
 * @param coroutineScope Scope for coroutines (typically viewModelScope)
 * @param dataComposerActionHandler Handler for DataComposerActions
 * @param dispatcher CoroutineDispatcher for parallel operations (default: Dispatchers.Default)
 * @return A new ListWithHeaderAndFooterDataComposer instance
 */
@ExperimentalComposerApi
public fun <UISTATE: UIState, INITDATA: StoreInitObj> listWithHeaderAndFooterDataComposer(
    storeFactory: StoreFactory<UISTATE, INITDATA>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): ListWithHeaderAndFooterDataComposer<UISTATE, INITDATA> = ListWithHeaderFooterDataComposerImpl(
    storeFactory = storeFactory,
    coroutineScope = coroutineScope,
    dataComposerActionHandler = dataComposerActionHandler,
    dispatcher = dispatcher
)