package com.angelbroking.spark.libraries.storecomposer.extensions

import com.angelbroking.spark.libraries.storecomposer.composer.data.DataComposerActionHandler
import com.angelbroking.spark.libraries.storecomposer.composer.data.ListDataComposer
import com.angelbroking.spark.libraries.storecomposer.composer.data.ListWithHeaderAndFooterDataComposer
import com.angelbroking.spark.libraries.storecomposer.composer.data.SingleDataComposer
import com.angelbroking.spark.libraries.storecomposer.composer.data.impl.ListDataComposerImpl
import com.angelbroking.spark.libraries.storecomposer.composer.data.impl.ListWithHeaderFooterDataComposerImpl
import com.angelbroking.spark.libraries.storecomposer.composer.data.impl.SingleDataComposerImpl
import com.angelbroking.spark.libraries.storecomposer.state.UIState
import com.angelbroking.spark.libraries.storecomposer.store.StoreInitObj
import com.angelbroking.spark.libraries.storecomposer.store.factory.StoreFactory
import kotlinx.coroutines.CoroutineScope

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
 * @return A new ListDataComposer instance
 */
fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> listDataComposer(
    storeFactory: StoreFactory<UISTATE, INITDATA, STOREMODEL>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler
): ListDataComposer<UISTATE, INITDATA,STOREMODEL> = ListDataComposerImpl(
    storeFactory = storeFactory,
    coroutineScope = coroutineScope,
    dataComposerActionHandler = dataComposerActionHandler
)

/**
 * Create a [SingleDataComposer] for single-widget screens.
 *
 * @param storeFactory Factory that creates Store instances for each WidgetId
 * @param coroutineScope Scope for coroutines (typically viewModelScope)
 * @param dataComposerActionHandler Handler for DataComposerActions
 * @return A new SingleDataComposer instance
 */
fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> singleDataComposer(
    storeFactory: StoreFactory<UISTATE, INITDATA, STOREMODEL>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler
): SingleDataComposer<UISTATE, INITDATA,STOREMODEL> = SingleDataComposerImpl(
    storeFactory = storeFactory,
    coroutineScope = coroutineScope,
    dataComposerActionHandler = dataComposerActionHandler
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
 * @return A new ListWithHeaderAndFooterDataComposer instance
 */
fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> listWithHeaderAndFooterDataComposer(
    storeFactory: StoreFactory<UISTATE, INITDATA, STOREMODEL>,
    coroutineScope: CoroutineScope,
    dataComposerActionHandler: DataComposerActionHandler
): ListWithHeaderAndFooterDataComposer<UISTATE, INITDATA,STOREMODEL> = ListWithHeaderFooterDataComposerImpl(
    storeFactory = storeFactory,
    coroutineScope = coroutineScope,
    dataComposerActionHandler = dataComposerActionHandler
)