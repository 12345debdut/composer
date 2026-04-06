package com.debdut.composer.action

/**
 * Interface for actions that bubble up to the UI layer (Fragment/Activity).
 *
 * UIComposerActions are used for side effects that require UI components to handle,
 * such as navigation, showing dialogs, toasts, bottom sheets, or triggering animations.
 * They are wrapped in [UIComposerActionHolder] and delivered via the `uiActionHolder` flow.
 *
 * ## Common Use Cases
 * - Navigation between screens
 * - Showing dialogs, bottom sheets, snackbars
 * - Showing/hiding keyboard
 * - Triggering animations
 * - Requesting permissions
 * - Opening external apps/URLs
 *
 * ## Usage Example
 * ```kotlin
 * // 1. Define the UIComposerAction
 * object ShowToastActionId : ActionId {
 *     override val id: String = "show_toast"
 * }
 *
 * data class ShowToastAction(
 *     val message: String,
 *     val duration: Int = Toast.LENGTH_SHORT,
 *     override val actionId: ActionId = ShowToastActionId
 * ) : UIComposerAction
 *
 * // 2. Dispatch from a Store
 * class MyStore : Store<...>() {
 *     override suspend fun receive(action: StoreAction, storeId: StoreId) {
 *         when (action) {
 *             is SaveSuccessAction -> {
 *                 suspendDispatch(ShowToastAction("Saved successfully!"))
 *             }
 *             is ItemClickedAction -> {
 *                 suspendDispatch(NavigateToDetailsAction(action.itemId))
 *             }
 *         }
 *     }
 * }
 *
 * // 3. Handle in Fragment
 * class MyFragment : ListUIComposerFragment<...>(...) {
 *
 *     override fun handleUIAction(actionHolder: UIComposerActionHolder) {
 *         when (val action = actionHolder.action) {
 *             is ShowToastAction -> {
 *                 Toast.makeText(context, action.message, action.duration).show()
 *             }
 *             is NavigateToDetailsAction -> {
 *                 findNavController().navigate(
 *                     MyFragmentDirections.actionToDetails(action.itemId)
 *                 )
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ## Observation
 * UIComposerActions are automatically observed when using base fragments like
 * [ListUIComposerFragment] or can be manually observed via:
 * ```kotlin
 * observeAction(lifecycleOwner = viewLifecycleOwner) { holder ->
 *     handleUIAction(holder)
 * }
 * ```
 *
 * @see ComposerAction
 * @see UIComposerActionHolder
 * @see ListUIComposerFragment.handleUIAction
 */
public interface UIComposerAction: ComposerAction