package com.debdut.composer.uicomponents

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import com.debdut.composer.composer.data.host.ListWithHeaderAndFooterDataComposerHost
import com.debdut.composer.composer.ui.ListUIComposerWithHeaderAndFooter
import com.debdut.composer.composer.ui.syntax.observeFooterState
import com.debdut.composer.composer.ui.syntax.observeHeaderState
import com.debdut.composer.state.UIState
import com.debdut.composer.state.HeaderUIStateType
import com.debdut.composer.state.FooterUIStateType
import com.debdut.composer.store.StoreInitObj

/**
 * Base Fragment for screens with header, footer, and scrollable content.
 *
 * Extends [ListUIComposerFragment] with additional observation for header
 * and footer states. States are automatically routed based on their type:
 * - [HeaderUIStateType] → [renderHeader]
 * - [FooterUIStateType] → [renderFooter]
 * - Other types → [render] (inherited from ListUIComposerFragment)
 *
 * ## Implementation Pattern
 * ```kotlin
 * class OrderPadFragment : ListUIComposerWithHeaderAndFooterFragment<
 *     OrderPadState,
 *     OrderPadInitModel
 * >(R.layout.fragment_order_pad) {
 *
 *     private val viewModel: OrderPadViewModel by viewModels()
 *     private lateinit var binding: FragmentOrderPadBinding
 *
 *     override val container: ListWithHeaderAndFooterDataComposerHost<...>
 *         get() = viewModel
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         binding = FragmentOrderPadBinding.bind(view)
 *         setupRecyclerView()
 *         viewModel.initialize(args.orderData)
 *     }
 *
 *     override fun render(state: List<OrderPadState>) {
 *         // Scrollable list content
 *         adapter.submitList(state)
 *     }
 *
 *     override fun renderHeader(list: List<OrderPadState>) {
 *         // Sticky header
 *         list.filterIsInstance<HeaderWidgetState>().firstOrNull()?.let { state ->
 *             binding.headerTitle.text = state.title
 *             binding.closeButton.isVisible = state.showCloseButton
 *         }
 *     }
 *
 *     override fun renderFooter(list: List<OrderPadState>) {
 *         // Sticky footer (e.g., confirm button)
 *         list.filterIsInstance<ConfirmButtonState>().firstOrNull()?.let { state ->
 *             binding.confirmButton.text = state.buttonText
 *             binding.confirmButton.isEnabled = state.isEnabled
 *             binding.confirmButton.setOnClickListener {
 *                 dispatch(ConfirmButtonClickedAction())
 *             }
 *         }
 *     }
 *
 *     override fun handleUIAction(actionHolder: UIComposerActionHolder) {
 *         when (val action = actionHolder.action) {
 *             is ShowToastAction -> showToast(action.message)
 *             is DismissAction -> dismiss()
 *         }
 *     }
 * }
 * ```
 *
 * ## State Type Markers
 * To route states to header/footer, use the appropriate type marker:
 * ```kotlin
 * data class HeaderWidgetState(
 *     override val type: UIStateType = object : HeaderUIStateType {},
 *     override val visible: Boolean = true,
 *     override val widgetId: WidgetId = HeaderWidgetId,
 *     val title: String = ""
 * ) : OrderPadState
 * ```
 *
 * @param UISTATE The base UI state type for widgets in this screen
 * @param INITDATA The initialization data type
 * @param layoutId The layout resource ID for this Fragment
 *
 * @see ListUIComposerFragment
 * @see ListUIComposerWithHeaderAndFooter
 * @see HeaderUIStateType
 * @see FooterUIStateType
 */
public abstract class ListUIComposerWithHeaderAndFooterFragment<UISTATE: UIState, INITDATA: StoreInitObj>(
    @LayoutRes private val layoutId: Int
): ListUIComposerFragment<UISTATE, INITDATA>(layoutId = layoutId), ListUIComposerWithHeaderAndFooter<UISTATE, INITDATA> {

    public abstract override val container: ListWithHeaderAndFooterDataComposerHost<UISTATE, INITDATA>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeHeaderState(lifecycleOwner = viewLifecycleOwner, lifecycleState = Lifecycle.State.STARTED, observer = ::renderHeader)
        observeFooterState(lifecycleOwner = viewLifecycleOwner, lifecycleState = Lifecycle.State.STARTED, observer = ::renderFooter)
    }

    /**
     * Called when header states change.
     *
     * Implement to update your fixed header section. Receives states
     * whose type implements [HeaderUIStateType].
     *
     * @param list List of header widget states
     */
    public abstract fun renderHeader(list: List<UISTATE>)

    /**
     * Called when footer states change.
     *
     * Implement to update your fixed footer section. Receives states
     * whose type implements [FooterUIStateType].
     *
     * @param list List of footer widget states
     */
    public abstract fun renderFooter(list: List<UISTATE>)
}