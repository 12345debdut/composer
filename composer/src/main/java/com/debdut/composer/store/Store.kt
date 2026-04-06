package com.debdut.composer.store

import com.debdut.composer.action.ActionId
import com.debdut.composer.action.StoreAction
import com.debdut.composer.action.holder.DataComposerActionHolder
import com.debdut.composer.action.holder.UIComposerActionHolder
import com.debdut.composer.state.UIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import com.debdut.composer.store.syntax.updateState
import kotlinx.coroutines.CoroutineScope

/**
 * Core business unit that manages the state for a single widget in the StoreComposer architecture.
 *
 * A Store is responsible for:
 * - Holding and managing the UI state for one widget
 * - Processing [StoreAction]s and updating state accordingly
 * - Dispatching side effects to parent layers ([DataComposerAction], [UIComposerAction])
 *
 * ## Type Parameters
 * - [UISTATE]: The UI state type this Store manages (must extend [UIState])
 * - [INITMODEL]: The initialization data type passed during setup
 * - [STOREMODEL]: Additional model type for widget configuration
 *
 * ## Key Abstract Members
 * - [storeId]: Unique identifier for this Store
 * - [subscribedStoreAction]: Set of [ActionId]s this Store responds to
 * - [initialise]: Sets up the initial state from the init model
 * - [receive]: Handles incoming [StoreAction]s
 *
 * ## State Management
 * Use the extension functions from `StoreSyntax.kt`:
 * ```kotlin
 * // Update state with transformation
 * updateState {
 *     copy(quantity = quantity + 1)
 * }
 *
 * // Emit a completely new state
 * emitState {
 *     QuantityState(quantity = 1)
 * }
 *
 * // Access current state
 * val current = currentState
 * ```
 *
 * ## Action Dispatching
 * Stores can dispatch actions to parent layers:
 * ```kotlin
 * // Dispatch to UI layer (shows toast, navigates, etc.)
 * suspendDispatch(ShowToastUIAction("Success!"))
 *
 * // Dispatch to DataComposer layer (ViewModel handles it)
 * suspendDispatch(RefreshAllWidgetsDataAction())
 * ```
 *
 * ## Implementation Example
 * ```kotlin
 * class QuantityWidgetStore @Inject constructor(
 *     private val repository: QuantityRepository
 * ) : Store<QuantityState, OrderInitModel, OrderWidgetModel>() {
 *
 *     override val storeId: StoreId = QuantityStoreId
 *
 *     override val subscribedStoreAction: Set<ActionId> = setOf(
 *         IncrementActionId,
 *         DecrementActionId,
 *         SetQuantityActionId
 *     )
 *
 *     override fun initialise(globalModel: OrderInitModel) {
 *         emitState {
 *             QuantityState(
 *                 quantity = globalModel.defaultQuantity,
 *                 maxQuantity = globalModel.maxAllowed
 *             )
 *         }
 *     }
 *
 *     override suspend fun receive(action: StoreAction, storeId: StoreId) {
 *         when (action) {
 *             is IncrementAction -> {
 *                 updateState {
 *                     copy(quantity = (quantity + 1).coerceAtMost(maxQuantity))
 *                 }
 *             }
 *             is DecrementAction -> {
 *                 updateState {
 *                     copy(quantity = (quantity - 1).coerceAtLeast(1))
 *                 }
 *             }
 *             is SetQuantityAction -> {
 *                 updateState {
 *                     copy(quantity = action.value.coerceIn(1, maxQuantity))
 *                 }
 *             }
 *         }
 *     }
 *
 *     override fun reset() {
 *         // Subscribe to external updates
 *         repository.quantityUpdates.onEach { newQty ->
 *             updateState { copy(quantity = newQty) }
 *         }.launchIn(storeScope)
 *     }
 *
 *     override fun clear() {
 *         // Cleanup resources
 *     }
 * }
 * ```
 *
 * ## Lifecycle
 * 1. Store is created by [StoreFactory]
 * 2. [reset] is called (for subscriptions)
 * 3. [initialise] is called with init data
 * 4. [receive] is called for each matching action
 * 5. [clear] is called on dispose
 *
 * @see StoreId
 * @see StoreFactory
 * @see UIState
 * @see StoreAction
 */
public abstract class Store<UISTATE: UIState, INITMODEL: StoreInitObj, STOREMODEL: StoreInitObj> {

    /** Internal mutable state flow. Use [updateState] or [emitState] syntax extensions instead. */
    internal val mutableUiStateFlow: MutableStateFlow<UISTATE?> = MutableStateFlow(value = null)

    /** Read-only state flow exposed to DataComposer. */
    internal val uiStateFlow: StateFlow<UISTATE?> = mutableUiStateFlow.asStateFlow()

    /** Flow for UI-layer actions (navigation, toasts, etc.) */
    internal val mutableUiSideEffects: MutableSharedFlow<UIComposerActionHolder> = MutableSharedFlow()
    internal val uiSideEffects = mutableUiSideEffects.asSharedFlow()

    /** Flow for DataComposer-layer actions */
    internal val mutableComposerSideEffects: MutableSharedFlow<DataComposerActionHolder> = MutableSharedFlow()
    internal val composerSideEffects = mutableComposerSideEffects.asSharedFlow()

    /**
     * Unique identifier for this Store.
     *
     * Used for:
     * - Targeted action dispatching via `dispatch(action, storeId)`
     * - Tracking action origin in ActionHolders
     * - Debugging and logging
     */
    public abstract val storeId: StoreId

    /**
     * Set of [ActionId]s that this Store responds to.
     *
     * When an action is dispatched, the Store's [receive] method is only called
     * if the action's [ActionId] is contained in this set.
     *
     * ```kotlin
     * override val subscribedStoreAction: Set<ActionId> = setOf(
     *     RefreshActionId,
     *     UpdateDataActionId,
     *     ClearActionId
     * )
     * ```
     */
    public abstract val subscribedStoreAction: Set<ActionId>

    /** CoroutineScope provided by DataComposer. Access via [storeScope] extension. */
    internal lateinit var coroutineScope: CoroutineScope

    /** Internal method to inject the coroutine scope. */
    internal fun updateCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    /**
     * Initialize the Store with the provided global model.
     *
     * Called when the widget list is initialized or updated. Typically used to
     * set up the initial state using [emitState].
     *
     * @param globalModel The initialization data passed from ViewModel
     */
    public abstract fun initialise(globalModel: INITMODEL)

    /**
     * Handle an incoming [StoreAction].
     *
     * Called when an action matching one of the [subscribedStoreAction] IDs is dispatched.
     * Implement business logic and state updates here.
     *
     * @param action The action to process
     * @param storeId The ID of the Store that originally dispatched this action
     *                (useful for inter-store communication)
     */
    public abstract suspend fun receive(action: StoreAction, storeId: StoreId)

    /**
     * Called after state is updated via [updateState].
     *
     * Override to perform side effects when state changes, such as:
     * - Analytics tracking
     * - Persistence
     * - Conditional action dispatching
     *
     * @param prevState The previous state before update
     * @param currentState The new state after update
     */
    public open fun invokeOnStateUpdate(prevState: UISTATE?, currentState: UISTATE?) {}

    /**
     * Called when the Store is being disposed.
     *
     * Override to clean up resources such as:
     * - Cancelling ongoing operations
     * - Removing listeners
     * - Releasing references
     */
    public open fun clear() {}

    /**
     * Called after Store creation but before [initialise].
     *
     * Override to set up subscriptions or listeners that should persist
     * across state re-initializations. This is called once per Store instance.
     *
     * Common use cases:
     * - Subscribing to external data sources
     * - Setting up form field listeners
     * - Registering broadcast receivers
     */
    public open fun reset() {}
}