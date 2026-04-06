package com.debdut.composer.uicomponents

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.debdut.composer.action.holder.UIComposerActionHolder
import com.debdut.composer.composer.ui.ListUIComposer
import com.debdut.composer.composer.ui.syntax.observeAction
import com.debdut.composer.composer.ui.syntax.observeAsState
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj

/**
 * Base Fragment for list-based StoreComposer screens.
 *
 * ListUIComposerFragment provides automatic setup for:
 * - Observing UI state from the DataComposer
 * - Handling UI actions dispatched by Stores
 *
 * Extend this class to quickly create screens that display multiple widgets.
 *
 * ## Implementation Pattern
 * ```kotlin
 * class OrderFragment : ListUIComposerFragment<
 *     OrderState,
 *     OrderInitModel,
 *     OrderWidgetModel
 * >(R.layout.fragment_order) {
 *
 *     private val viewModel: OrderViewModel by viewModels()
 *     private lateinit var adapter: OrderAdapter
 *
 *     override val container: ListDataComposerHost<OrderState, OrderInitModel, OrderWidgetModel>
 *         get() = viewModel
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         setupRecyclerView()
 *         viewModel.initialize(args.orderData)
 *     }
 *
 *     override fun render(state: List<OrderState>) {
 *         // Called when any widget state changes
 *         adapter.submitList(state)
 *     }
 *
 *     override fun handleUIAction(actionHolder: UIComposerActionHolder) {
 *         // Handle side effects from Stores
 *         when (val action = actionHolder.action) {
 *             is ShowToastAction -> showToast(action.message)
 *             is NavigateAction -> navigate(action.destination)
 *             is ShowBottomSheetAction -> showBottomSheet(action.data)
 *         }
 *     }
 * }
 * ```
 *
 * ## Lifecycle Awareness
 * State and action observation is lifecycle-aware - it starts when the Fragment
 * is at least in STARTED state and automatically stops when destroyed.
 *
 * ## Dispatching Actions
 * Use the extension functions from UIComposerSyntax:
 * ```kotlin
 * // Dispatch to all subscribed stores
 * dispatch(RefreshAction())
 *
 * // Dispatch to specific store
 * dispatch(UpdateAction(data), QuantityStoreId)
 * ```
 *
 * @param UISTATE The base UI state type for widgets in this screen
 * @param INITDATA The initialization data type
 * @param STOREMODEL The widget model type
 * @param layoutId The layout resource ID for this Fragment
 *
 * @see ListUIComposer
 * @see ListUIComposerWithHeaderAndFooterFragment
 */
public abstract class ListUIComposerFragment<UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj>(
    @LayoutRes private val layoutId: Int
): Fragment(layoutId), ListUIComposer<UISTATE, INITDATA, STOREMODEL> {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAsState(lifecycleOwner = viewLifecycleOwner, lifecycleState = Lifecycle.State.STARTED, observer = ::render)
        observeAction(lifecycleState = Lifecycle.State.STARTED, lifecycleOwner = viewLifecycleOwner, observer = ::handleUIAction)
    }

    /**
     * Called when the combined UI state changes.
     *
     * Implement this to update your UI (e.g., submit list to RecyclerView adapter).
     * The list order matches the widget order specified during initialization.
     *
     * @param state List of visible widget states
     */
    public abstract fun render(state: List<UISTATE>)

    /**
     * Handle UI actions dispatched by Stores.
     *
     * Implement this to handle side effects like:
     * - Navigation
     * - Showing dialogs, toasts, bottom sheets
     * - Opening external apps
     * - Triggering animations
     *
     * @param actionHolder The action holder containing the UIComposerAction and source store ID
     */
    public abstract fun handleUIAction(actionHolder: UIComposerActionHolder)
}