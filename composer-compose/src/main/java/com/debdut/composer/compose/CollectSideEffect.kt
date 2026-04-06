package com.debdut.composer.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.debdut.composer.action.holder.UIComposerActionHolder
import com.debdut.composer.composer.data.host.DataComposerHost
import com.debdut.composer.state.UIState
import com.debdut.composer.store.StoreInitObj

/**
 * Collect UI side effects from a DataComposerHost in a lifecycle-aware, Compose-safe way.
 *
 * This composable launches a coroutine that collects [UIComposerActionHolder]s
 * from the host's action flow. Collection is lifecycle-aware — it pauses when
 * the lifecycle drops below [Lifecycle.State.STARTED] and resumes automatically.
 *
 * Use this for one-shot UI effects like:
 * - Showing toasts/snackbars
 * - Navigation events
 * - Showing dialogs
 *
 * ## Example
 * ```kotlin
 * @Composable
 * fun CounterScreen(viewModel: CounterViewModel) {
 *     val snackbarHostState = remember { SnackbarHostState() }
 *
 *     CollectSideEffect(viewModel) { holder ->
 *         when (val action = holder.action) {
 *             is ShowToastAction -> {
 *                 snackbarHostState.showSnackbar(action.message)
 *             }
 *             is NavigateAction -> {
 *                 navController.navigate(action.route)
 *             }
 *         }
 *     }
 *
 *     // ... rest of UI
 * }
 * ```
 *
 * @param host The DataComposerHost to collect side effects from
 * @param onAction Callback invoked for each UI action
 */
@Composable
public fun <UISTATE : UIState, INITDATA : StoreInitObj> CollectSideEffect(
    host: DataComposerHost<UISTATE, INITDATA>,
    onAction: suspend (UIComposerActionHolder) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(host, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            host.container.uiActionHolder.collect { holder ->
                onAction(holder)
            }
        }
    }
}
