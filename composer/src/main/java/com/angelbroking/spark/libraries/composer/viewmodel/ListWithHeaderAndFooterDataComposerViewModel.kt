package com.angelbroking.spark.libraries.composer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelbroking.spark.libraries.composer.composer.data.DataComposerActionHandler
import com.angelbroking.spark.libraries.composer.composer.data.ListWithHeaderAndFooterDataComposer
import com.angelbroking.spark.libraries.composer.composer.data.host.ListWithHeaderAndFooterDataComposerHost
import com.angelbroking.spark.libraries.composer.extensions.listWithHeaderAndFooterDataComposer
import com.angelbroking.spark.libraries.composer.state.UIState
import com.angelbroking.spark.libraries.composer.state.HeaderUIStateType
import com.angelbroking.spark.libraries.composer.state.FooterUIStateType
import com.angelbroking.spark.libraries.composer.store.StoreInitObj
import com.angelbroking.spark.libraries.composer.store.factory.StoreFactory

/**
 * Base ViewModel for screens with header, footer, and scrollable content.
 *
 * Extends the functionality of [ListDataComposerViewModel] with automatic
 * state separation for header and footer widgets based on their [UIState.type]:
 * - [HeaderUIStateType] → `container.headerState`
 * - [FooterUIStateType] → `container.footerState`
 * - Other types → `container.uiStateFlow`
 *
 * ## Implementation Pattern
 * ```kotlin
 * @HiltViewModel
 * class OrderPadViewModel @Inject constructor(
 *     storeFactory: OrderPadStoreFactory,
 *     private val repository: OrderRepository
 * ) : ListWithHeaderAndFooterDataComposerViewModel<
 *     OrderPadState,
 *     OrderPadInitModel,
 *     OrderWidgetModel
 * >(storeFactory), DataComposerActionHandler {
 *
 *     override val dataComposerActionHandler: DataComposerActionHandler = this
 *
 *     fun initialize(symbol: String, transactionType: TransactionType) {
 *         viewModelScope.launch {
 *             val widgets = listOf(
 *                 // Header widgets (sticky top)
 *                 ScreenHeaderWidgetId,
 *
 *                 // Main content widgets (scrollable)
 *                 QuantityWidgetId,
 *                 PriceWidgetId,
 *                 OrderTypeWidgetId,
 *                 ValidityWidgetId,
 *
 *                 // Footer widgets (sticky bottom)
 *                 MarginInfoWidgetId,
 *                 ConfirmButtonWidgetId
 *             )
 *
 *             val initModel = OrderPadInitModel(
 *                 symbol = symbol,
 *                 transactionType = transactionType,
 *                 // ... other init data
 *             )
 *
 *             init(widgets, initModel)
 *         }
 *     }
 *
 *     override suspend fun receiveAction(holder: DataComposerActionHolder) {
 *         when (val action = holder.action) {
 *             is PlaceOrderAction -> placeOrder()
 *             is RefreshMarginAction -> refreshMargin()
 *         }
 *     }
 *
 *     override fun receiveAllActions(action: Action) {
 *         // Optional: analytics, logging
 *     }
 *
 *     private suspend fun placeOrder() {
 *         val result = repository.placeOrder(...)
 *         // Handle result
 *     }
 * }
 * ```
 *
 * ## State Flow Access
 * The container provides three separate state flows:
 * ```kotlin
 * container.uiStateFlow   // Main list content
 * container.headerState   // Header widgets
 * container.footerState   // Footer widgets
 * ```
 *
 * ## Fragment Integration
 * Use with [ListUIComposerWithHeaderAndFooterFragment] for automatic observation:
 * ```kotlin
 * class OrderPadFragment : ListUIComposerWithHeaderAndFooterFragment<...>(...) {
 *     override val container get() = viewModel
 *     override fun render(state: List<...>) { /* main list */ }
 *     override fun renderHeader(list: List<...>) { /* sticky header */ }
 *     override fun renderFooter(list: List<...>) { /* sticky footer */ }
 * }
 * ```
 *
 * @param UISTATE The base UI state type for all widgets
 * @param INITDATA The initialization data type
 * @param STOREMODEL The widget model type
 * @param storeFactory Factory that creates Store instances
 *
 * @see ListDataComposerViewModel
 * @see ListWithHeaderAndFooterDataComposerHost
 * @see ListUIComposerWithHeaderAndFooterFragment
 */
abstract class ListWithHeaderAndFooterDataComposerViewModel<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>(
    storeFactory: StoreFactory<UISTATE, INITDATA, STOREMODEL>
) : ViewModel(), ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA,STOREMODEL> {

    /**
     * Handler for [DataComposerAction]s dispatched by Stores.
     *
     * Typically set to `this` when the ViewModel implements [DataComposerActionHandler].
     */
    protected abstract val dataComposerActionHandler: DataComposerActionHandler

    /**
     * The [ListWithHeaderAndFooterDataComposer] that manages Stores and state.
     *
     * Provides access to:
     * - `uiStateFlow`: Main list content
     * - `headerState`: States with [HeaderUIStateType]
     * - `footerState`: States with [FooterUIStateType]
     */
    override val container: ListWithHeaderAndFooterDataComposer<UISTATE, INITDATA,STOREMODEL> by lazy {
        listWithHeaderAndFooterDataComposer(
            storeFactory = storeFactory,
            coroutineScope = viewModelScope,
            dataComposerActionHandler = dataComposerActionHandler
        )
    }
}