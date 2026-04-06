package com.debdut.composer

import com.debdut.composer.action.Action
import com.debdut.composer.action.ActionId
import com.debdut.composer.action.StoreAction
import com.debdut.composer.action.holder.DataComposerActionHolder
import com.debdut.composer.composer.data.DataComposerActionHandler
import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.extensions.listWithHeaderAndFooterDataComposer
import com.debdut.composer.state.FooterUIStateType
import com.debdut.composer.state.HeaderUIStateType
import com.debdut.composer.state.UIState
import com.debdut.composer.state.UIStateDefaultType
import com.debdut.composer.state.UIStateType
import com.debdut.composer.store.Store
import com.debdut.composer.store.StoreId
import com.debdut.composer.store.StoreInitObj
import com.debdut.composer.store.factory.StoreFactory
import com.debdut.composer.store.syntax.emitState
import com.debdut.composer.store.syntax.updateState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListWithHeaderFooterDataComposerImplTest {

    // --- Type markers ---

    object TestHeaderType : HeaderUIStateType
    object TestFooterType : FooterUIStateType

    // --- Widget / Store identifiers ---

    object HeaderWidgetId : WidgetId { override val id = "header-widget" }
    object FooterWidgetId : WidgetId { override val id = "footer-widget" }
    object BodyWidgetId : WidgetId { override val id = "body-widget" }

    object HeaderStoreId : StoreId { override val id = "header-store" }
    object FooterStoreId : StoreId { override val id = "footer-store" }
    object BodyStoreId : StoreId { override val id = "body-store" }

    // --- Shared init obj ---

    object TestInitObj : StoreInitObj

    // --- Actions ---

    object UpdateActionId : ActionId { override val id = "update" }
    data class UpdateAction(
        val newValue: String,
        override val actionId: ActionId = UpdateActionId
    ) : StoreAction

    // --- State ---

    data class TestState(
        override val type: UIStateType = UIStateDefaultType,
        override val visible: Boolean = true,
        override val widgetId: WidgetId = BodyWidgetId,
        val value: String = ""
    ) : UIState

    // --- Stores ---

    class HeaderStore : Store<TestState, TestInitObj, TestInitObj>() {
        override val storeId: StoreId = HeaderStoreId
        override val subscribedStoreAction: Set<ActionId> = setOf(UpdateActionId)

        override fun initialise(globalModel: TestInitObj) {
            emitState { TestState(type = TestHeaderType, widgetId = HeaderWidgetId, value = "header-init") }
        }

        override suspend fun receive(action: StoreAction, storeId: StoreId) {
            when (action) {
                is UpdateAction -> updateState { copy(value = action.newValue) }
            }
        }
    }

    class FooterStore : Store<TestState, TestInitObj, TestInitObj>() {
        override val storeId: StoreId = FooterStoreId
        override val subscribedStoreAction: Set<ActionId> = setOf(UpdateActionId)

        override fun initialise(globalModel: TestInitObj) {
            emitState { TestState(type = TestFooterType, widgetId = FooterWidgetId, value = "footer-init") }
        }

        override suspend fun receive(action: StoreAction, storeId: StoreId) {
            when (action) {
                is UpdateAction -> updateState { copy(value = action.newValue) }
            }
        }
    }

    class BodyStore : Store<TestState, TestInitObj, TestInitObj>() {
        override val storeId: StoreId = BodyStoreId
        override val subscribedStoreAction: Set<ActionId> = setOf(UpdateActionId)

        override fun initialise(globalModel: TestInitObj) {
            emitState { TestState(type = UIStateDefaultType, widgetId = BodyWidgetId, value = "body-init") }
        }

        override suspend fun receive(action: StoreAction, storeId: StoreId) {
            when (action) {
                is UpdateAction -> updateState { copy(value = action.newValue) }
            }
        }
    }

    class TestStoreFactory : StoreFactory<TestState, TestInitObj, TestInitObj> {
        override fun get(widgetId: WidgetId): Store<TestState, TestInitObj, TestInitObj> = when (widgetId) {
            HeaderWidgetId -> HeaderStore()
            FooterWidgetId -> FooterStore()
            BodyWidgetId -> BodyStore()
            else -> throw IllegalArgumentException("Unknown widget: $widgetId")
        }
    }

    class NoOpActionHandler : DataComposerActionHandler {
        override suspend fun receiveAction(dataComposerActionHolder: DataComposerActionHolder) = Unit
        override fun receiveAllActions(action: Action) = Unit
    }

    // --- Tests ---

    @Test
    fun `headerState emits only states with HeaderUIStateType`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = listWithHeaderAndFooterDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initialiseWithWidgets(listOf(HeaderWidgetId, FooterWidgetId, BodyWidgetId), TestInitObj)
        advanceUntilIdle()

        val headerStates = composer.headerState.value
        assertEquals(1, headerStates.size)
        assertEquals("header-init", headerStates[0].value)
        assertTrue(headerStates[0].type is HeaderUIStateType)

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `footerState emits only states with FooterUIStateType`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = listWithHeaderAndFooterDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initialiseWithWidgets(listOf(HeaderWidgetId, FooterWidgetId, BodyWidgetId), TestInitObj)
        advanceUntilIdle()

        val footerStates = composer.footerState.value
        assertEquals(1, footerStates.size)
        assertEquals("footer-init", footerStates[0].value)
        assertTrue(footerStates[0].type is FooterUIStateType)

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `uiStateFlow receives only default type states`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = listWithHeaderAndFooterDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initialiseWithWidgets(listOf(HeaderWidgetId, FooterWidgetId, BodyWidgetId), TestInitObj)
        advanceUntilIdle()

        val bodyStates = composer.uiStateFlow.value
        assertEquals(1, bodyStates.size)
        assertEquals("body-init", bodyStates[0].value)
        assertEquals(UIStateDefaultType, bodyStates[0].type)

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `all three flows are correctly populated with mixed-type stores`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = listWithHeaderAndFooterDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initialiseWithWidgets(listOf(HeaderWidgetId, FooterWidgetId, BodyWidgetId), TestInitObj)
        advanceUntilIdle()

        assertEquals(1, composer.headerState.value.size)
        assertEquals(1, composer.footerState.value.size)
        assertEquals(1, composer.uiStateFlow.value.size)

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `dispose clears all three flows`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = listWithHeaderAndFooterDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initialiseWithWidgets(listOf(HeaderWidgetId, FooterWidgetId, BodyWidgetId), TestInitObj)
        advanceUntilIdle()

        composer.dispose()
        scope.cancel()

        assertTrue(composer.currentWidgetIds().isEmpty())
        assertTrue(composer.uiStateFlow.value.isEmpty())
        assertTrue(composer.headerState.value.isEmpty())
        assertTrue(composer.footerState.value.isEmpty())
    }
}
