package com.debdut.composer.action.holder

import com.debdut.composer.action.UIComposerAction
import com.debdut.composer.store.StoreId

/**
 * ActionHolder implementation for [UIComposerAction]s.
 *
 * UIComposerActionHolders wrap actions that should be handled by the UI layer
 * (Fragment/Activity). They are delivered via the `uiActionHolder` SharedFlow
 * and processed in the Fragment's `handleUIAction` method.
 *
 * ## How It's Delivered
 * When a Store dispatches a UIComposerAction via `suspendDispatch()`, it flows:
 * ```
 * Store.suspendDispatch(UIComposerAction)
 *         │
 *         ▼
 * Store.uiSideEffects (SharedFlow)
 *         │
 *         ▼
 * DataComposer combines all Store side effects
 *         │
 *         ▼
 * UIComposer.uiActionHolder (SharedFlow)
 *         │
 *         ▼
 * Fragment.handleUIAction(holder)
 * ```
 *
 * ## Usage Example
 * ```kotlin
 * // In Fragment extending ListUIComposerFragment
 * override fun handleUIAction(holder: UIComposerActionHolder) {
 *     // Access the source store if needed
 *     val sourceStore = holder.storeId
 *
 *     when (val action = holder.action) {
 *         is ShowBottomSheetAction -> {
 *             showBottomSheet(action.sheetData)
 *         }
 *         is NavigateAction -> {
 *             findNavController().navigate(action.destination)
 *         }
 *         is ShowKeyboardAction -> {
 *             showKeyboardForField(action.fieldId)
 *         }
 *         is DismissAction -> {
 *             dismiss()
 *         }
 *     }
 * }
 * ```
 *
 * ## Automatic Observation
 * When using ListUIComposerFragment (from composer-fragment module) or its variants,
 * UI actions are automatically observed and delivered to `handleUIAction()`.
 *
 * @property action The [UIComposerAction] being transported
 * @property storeId The ID of the Store that dispatched this action
 *
 * @see ActionHolder
 * @see UIComposerAction
 */
public data class UIComposerActionHolder(
    override val action: UIComposerAction,
    override val storeId: StoreId
): ActionHolder
