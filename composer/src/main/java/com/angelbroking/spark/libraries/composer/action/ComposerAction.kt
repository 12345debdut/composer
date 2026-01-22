package com.angelbroking.spark.libraries.composer.action

/**
 * Base interface for actions that are processed at the Composer level.
 *
 * ComposerActions are NOT handled by individual Stores. Instead, they flow through
 * the DataComposer and may bubble up to the UI layer or be handled by the
 * [DataComposerActionHandler].
 *
 * ## Subtypes
 * - [DataComposerAction]: Handled by the DataComposer/ViewModel layer
 * - [UIComposerAction]: Bubbles up to the UI layer (Fragment/Activity)
 *
 * ## Flow Diagram
 * ```
 * Store dispatches ComposerAction
 *         │
 *         ▼
 * ┌───────────────────┐
 * │   DataComposer    │
 * └───────────────────┘
 *         │
 *         ├── DataComposerAction → ViewModel.receiveAction()
 *         │
 *         └── UIComposerAction → Fragment.handleUIAction()
 * ```
 *
 * ## Usage Example
 * ```kotlin
 * // In a Store, dispatch a ComposerAction to notify parent layers
 * override suspend fun receive(action: StoreAction, storeId: StoreId) {
 *     when (action) {
 *         is DataLoadedAction -> {
 *             // Notify the DataComposer layer
 *             suspendDispatch(DataLoadedComposerAction(data))
 *         }
 *         is ButtonClickedAction -> {
 *             // Notify the UI layer
 *             suspendDispatch(ShowToastUIAction("Button clicked!"))
 *         }
 *     }
 * }
 * ```
 *
 * @see DataComposerAction
 * @see UIComposerAction
 * @see Action
 */
interface ComposerAction: Action