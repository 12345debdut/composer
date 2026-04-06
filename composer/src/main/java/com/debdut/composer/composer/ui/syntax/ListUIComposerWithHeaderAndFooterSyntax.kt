package com.debdut.composer.composer.ui.syntax

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.debdut.composer.composer.data.syntax.footerState
import com.debdut.composer.composer.data.syntax.headerState
import com.debdut.composer.composer.ui.ListUIComposerWithHeaderAndFooter
import com.debdut.composer.state.UIState
import com.debdut.composer.state.HeaderUIStateType
import com.debdut.composer.state.FooterUIStateType
import com.debdut.composer.store.StoreInitObj
import kotlinx.coroutines.launch

/**
 * Extension functions for [ListUIComposerWithHeaderAndFooter] with lifecycle-aware observation.
 *
 * These extensions enable Fragments to observe header and footer states separately
 * with proper lifecycle management.
 *
 * ## Usage (in Fragment)
 * ```kotlin
 * // Observe header with lifecycle awareness
 * observeHeaderState(viewLifecycleOwner) { headerStates ->
 *     headerStates.filterIsInstance<HeaderWidgetState>().firstOrNull()?.let {
 *         binding.headerTitle.text = it.title
 *     }
 * }
 *
 * // Observe footer with lifecycle awareness
 * observeFooterState(viewLifecycleOwner) { footerStates ->
 *     footerStates.filterIsInstance<ConfirmButtonState>().firstOrNull()?.let {
 *         binding.confirmButton.isEnabled = it.isEnabled
 *     }
 * }
 * ```
 *
 * ## Automatic Setup
 * When using [ListUIComposerWithHeaderAndFooterFragment], these observations are
 * set up automatically and routed to `renderHeader()` and `renderFooter()` methods.
 */

/**
 * Observe header states with lifecycle awareness.
 *
 * Receives states whose type implements [HeaderUIStateType].
 *
 * @param lifecycleOwner The lifecycle owner (typically viewLifecycleOwner)
 * @param lifecycleState Minimum lifecycle state for observation (default: STARTED)
 * @param observer Callback invoked with header states on each update
 */
public fun <UISTATE: UIState, INITDATA: StoreInitObj,STOREMODEL: StoreInitObj> ListUIComposerWithHeaderAndFooter<UISTATE, INITDATA,STOREMODEL>.observeHeaderState(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observer: List<UISTATE>.() -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(lifecycleState) {
            container.headerState.collect(observer)
        }
    }
}

/**
 * Observe footer states with lifecycle awareness.
 *
 * Receives states whose type implements [FooterUIStateType].
 *
 * @param lifecycleOwner The lifecycle owner (typically viewLifecycleOwner)
 * @param lifecycleState Minimum lifecycle state for observation (default: STARTED)
 * @param observer Callback invoked with footer states on each update
 */
public fun <UISTATE: UIState, INITDATA: StoreInitObj, STOREMODEL: StoreInitObj> ListUIComposerWithHeaderAndFooter<UISTATE, INITDATA,STOREMODEL>.observeFooterState(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    observer: List<UISTATE>.() -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(lifecycleState) {
            container.footerState.collect(observer)
        }
    }
}