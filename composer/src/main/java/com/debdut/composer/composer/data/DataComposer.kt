package com.debdut.composer.composer.data

import com.debdut.composer.action.Action
import com.debdut.composer.action.StoreAction
import com.debdut.composer.action.holder.UIComposerActionHolder
import com.debdut.composer.composer.Composer
import com.debdut.composer.composer.data.model.StoreActionWidgetIdPair
import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreId
import com.debdut.composer.store.StoreInitObj
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Core interface for managing [Store]s and coordinating state in the StoreComposer architecture.
 *
 * DataComposer is responsible for:
 * - Creating and managing Store instances via [StoreFactory]
 * - Combining state from all Stores into a single state flow
 * - Routing actions to appropriate Stores
 * - Forwarding side effects to the UI layer
 *
 * ## Key Responsibilities
 * 1. **Store Lifecycle**: Creates stores when [initializeWithWidgets] is called
 * 2. **State Combination**: Merges individual Store states into [uiStateFlow]
 * 3. **Action Routing**: Dispatches actions to matching Stores
 * 4. **Side Effect Forwarding**: Bubbles up UI actions via [uiActionHolder]
 *
 * ## Variants
 * - [SingleDataComposer]: For screens with a single widget
 * - [ListDataComposer]: For screens with multiple widgets in a list
 * - [ListWithHeaderAndFooterDataComposer]: List with separate header/footer
 *
 * ## Usage Flow
 * ```
 * 1. ViewModel.init(widgets, initData)
 *         │
 *         ▼
 * 2. DataComposer.initializeWithWidgets(widgets, initData)
 *         │
 *         ├── Creates Store for each WidgetId via StoreFactory
 *         ├── Calls Store.reset() for each Store
 *         ├── Calls Store.initialize(initData) for each Store
 *         └── Combines Store states into uiStateFlow
 *         │
 *         ▼
 * 3. Fragment observes uiStateFlow and renders UI
 *         │
 *         ▼
 * 4. User interaction triggers dispatch(action)
 *         │
 *         ▼
 * 5. DataComposer routes action to subscribed Stores
 *         │
 *         ▼
 * 6. Stores update state, new combined state emitted
 * ```
 *
 * ## State Observation
 * ```kotlin
 * // In Fragment
 * viewLifecycleOwner.lifecycleScope.launch {
 *     viewModel.container.uiStateFlow.collect { states ->
 *         renderWidgets(states)
 *     }
 * }
 * ```
 *
 * @param UISTATE The base UI state type for all widgets
 * @param INITOBJ The initialization data type
 *
 * @see SingleDataComposer
 * @see ListDataComposer
 * @see ListWithHeaderAndFooterDataComposer
 * @see Store
 */
public interface DataComposer<UISTATE: UIState, INITOBJ: StoreInitObj>: Composer {

    /** Combined state flow from all managed Stores. Emits list of visible widget states. */
    public val uiStateFlow: StateFlow<List<UISTATE>>

    /** Flow of UI actions dispatched by Stores (navigation, toasts, dialogs). */
    public val uiActionHolder: SharedFlow<UIComposerActionHolder>

    /**
     * Initialize the composer with a list of widgets.
     *
     * Creates Store instances for each widget, calls reset() and initialize() on each.
     *
     * @param widgets List of widget IDs to create stores for
     * @param initObj Initialization data passed to each Store
     */
    public suspend fun initializeWithWidgets(widgets: List<WidgetId>, initObj: INITOBJ)

    /**
     * Update existing widgets with new init data.
     *
     * Only updates stores for widgets that already exist.
     *
     * @param widgets List of widget IDs to update
     * @param initobj New initialization data
     */
    public suspend fun updateWidgets(widgets: List<WidgetId>, initobj: INITOBJ)

    /**
     * Re-initialize all existing Stores with new init data.
     *
     * Does not create new stores, only calls initialize() on existing ones.
     */
    public fun initializeWithInitModel(initObj: INITOBJ)

    /** Dispatch action to all subscribed Stores (suspending). */
    public suspend fun suspendDispatch(action: Action)

    /** Dispatch action to a specific Store by its ID (suspending). */
    public suspend fun suspendDispatchToStore(action: StoreAction, storeId: StoreId)

    /** Dispatch action to a Store associated with a widget (suspending). */
    public suspend fun suspendDispatchToWidget(action: StoreAction, widgetId: WidgetId)

    /** Dispatch multiple actions to their respective widgets efficiently (suspending). */
    public suspend fun suspendBatchDispatchToWidget(storeActionWidgetIdPairList: List<StoreActionWidgetIdPair>)

    /** Get list of currently active widget IDs. */
    public fun currentWidgetIds(): List<WidgetId>

    /** Clean up all Stores and internal resources. */
    public fun dispose()

    /** Dispatch action to all subscribed Stores (non-suspending). */
    public fun dispatch(action: Action)

    /** Dispatch action to a specific Store by its ID (non-suspending). */
    public fun dispatchToStore(action: StoreAction, storeId: StoreId)

    /** Dispatch action to a Store associated with a widget (non-suspending). */
    public fun dispatchToWidget(action: StoreAction, widgetId: WidgetId)
}