package com.debdut.composer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debdut.composer.composer.data.DataComposerActionHandler
import com.debdut.composer.composer.data.ListDataComposer
import com.debdut.composer.composer.data.host.ListDataComposerHost
import com.debdut.composer.extensions.listDataComposer
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj
import com.debdut.composer.store.factory.StoreFactory

/**
 * Base ViewModel for list-based StoreComposer screens.
 *
 * ListDataComposerViewModel provides the integration between Android's ViewModel
 * and the StoreComposer architecture. It automatically creates and manages
 * a [ListDataComposer] using the ViewModel's lifecycle.
 *
 * ## Implementation Pattern
 * ```kotlin
 * @HiltViewModel
 * class OrderViewModel @Inject constructor(
 *     storeFactory: OrderStoreFactory,
 *     private val repository: OrderRepository
 * ) : ListDataComposerViewModel<OrderState, OrderInitModel, OrderWidgetModel>(
 *     storeFactory = storeFactory
 * ), DataComposerActionHandler {
 *
 *     // Point to self as the action handler
 *     override val dataComposerActionHandler: DataComposerActionHandler = this
 *
 *     // Navigation events exposed to Fragment
 *     private val _navigationEvent = MutableSharedFlow<NavigationTarget>()
 *     val navigationEvent = _navigationEvent.asSharedFlow()
 *
 *     fun initialize(orderData: OrderData) {
 *         viewModelScope.launch {
 *             val widgets = listOf(
 *                 HeaderWidgetId,
 *                 QuantityWidgetId,
 *                 PriceWidgetId,
 *                 ConfirmWidgetId
 *             )
 *             val initModel = OrderInitModel(
 *                 symbol = orderData.symbol,
 *                 quantity = orderData.quantity
 *             )
 *             init(widgets, initModel)
 *         }
 *     }
 *
 *     // Handle DataComposerActions from Stores
 *     override suspend fun receiveAction(holder: DataComposerActionHolder) {
 *         when (val action = holder.action) {
 *             is OrderSuccessAction -> {
 *                 _navigationEvent.emit(NavigationTarget.Success(action.orderId))
 *             }
 *             is RefreshDataAction -> {
 *                 val newData = repository.refresh()
 *                 init(currentWidgetIds, newData.toInitModel())
 *             }
 *         }
 *     }
 *
 *     override fun receiveAllActions(action: Action) {
 *         // Optional: Log all actions for debugging
 *         Timber.d("Action: ${action.actionId.id}")
 *     }
 * }
 * ```
 *
 * ## Key Components
 * - **storeFactory**: Factory that creates Store instances for each widget
 * - **dataComposerActionHandler**: Handler for DataComposerActions (usually `this`)
 * - **container**: The ListDataComposer instance, created lazily
 *
 * ## DSL Methods (via DataComposerHostSyntax)
 * ```kotlin
 * // Initialize with widgets
 * init(widgets, initModel)
 *
 * // Dispatch actions
 * dispatch(action)
 * dispatch(action, storeId)
 * dispatch(action, widgetId)
 *
 * // Batch dispatch
 * suspendBatchDispatch(actionWidgetPairs)
 *
 * // Get current widgets
 * val widgets = currentWidgetIds
 * ```
 *
 * @param UISTATE The base UI state type for all widgets
 * @param INITDATA The initialization data type
 * @param STOREMODEL The widget model type
 * @param storeFactory Factory that creates Store instances
 *
 * @see ListWithHeaderAndFooterDataComposerViewModel
 * @see ListDataComposerHost
 * @see DataComposerActionHandler
 */
public abstract class ListDataComposerViewModel<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>(
    storeFactory: StoreFactory<UISTATE, INITDATA, STOREMODEL>
) : ViewModel(), ListDataComposerHost<UISTATE, INITDATA,STOREMODEL> {

    /**
     * Handler for [DataComposerAction]s dispatched by Stores.
     *
     * Typically set to `this` when the ViewModel implements [DataComposerActionHandler].
     */
    public abstract val dataComposerActionHandler: DataComposerActionHandler

    /**
     * The [ListDataComposer] that manages Stores and state.
     *
     * Created lazily using the provided [storeFactory] and [viewModelScope].
     */
    public override val container: ListDataComposer<UISTATE, INITDATA,STOREMODEL> by lazy {
        listDataComposer(
            storeFactory = storeFactory,
            coroutineScope = viewModelScope,
            dataComposerActionHandler = dataComposerActionHandler
        )
    }
}