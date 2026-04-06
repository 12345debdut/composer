package com.debdut.composer

import app.cash.turbine.test
import com.debdut.composer.action.Action
import com.debdut.composer.action.ActionId
import com.debdut.composer.action.StoreAction
import com.debdut.composer.action.holder.DataComposerActionHolder
import com.debdut.composer.composer.data.DataComposerActionHandler
import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.extensions.singleDataComposer
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
class SingleDataComposerImplTest {

    object EmptyWidgetId : WidgetId {
        override val id: String = ""
    }

    data class TestState(
        override val type: UIStateType = UIStateDefaultType,
        override val visible: Boolean = true,
        override val widgetId: WidgetId = EmptyWidgetId,
        val value: Int = 0
    ) : UIState

    object TestInitObj : StoreInitObj

    object TestWidgetId : WidgetId {
        override val id: String = "single-widget"
    }

    object TestStoreId : StoreId {
        override val id: String = "test-store"
    }

    object IncrementActionId : ActionId {
        override val id: String = "increment"
    }

    data class IncrementAction(
        override val actionId: ActionId = IncrementActionId
    ) : StoreAction

    class SingleTestStore : Store<TestState, TestInitObj>() {
        override val storeId: StoreId = TestStoreId
        override val subscribedStoreAction: Set<ActionId> = setOf(IncrementActionId)

        override fun initialize(globalModel: TestInitObj) {
            emitState { TestState(widgetId = TestWidgetId, value = 0) }
        }

        override suspend fun receive(action: StoreAction, storeId: StoreId) {
            when (action) {
                is IncrementAction -> updateState { copy(value = value + 1) }
            }
        }
    }

    class TestStoreFactory : StoreFactory<TestState, TestInitObj> {
        override fun get(widgetId: WidgetId): Store<TestState, TestInitObj> {
            return SingleTestStore()
        }
    }

    class NoOpActionHandler : DataComposerActionHandler {
        override suspend fun receiveAction(dataComposerActionHolder: DataComposerActionHolder) {}
        override fun receiveAllActions(action: Action) {}
    }

    @Test
    fun `initialize creates single store and emits state`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = singleDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initializeWithWidgets(listOf(TestWidgetId), TestInitObj)
        advanceUntilIdle()

        val states = composer.uiStateFlow.value
        assertEquals(1, states.size)
        assertEquals(0, states[0].value)

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `dispatch updates single store state`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = singleDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initializeWithWidgets(listOf(TestWidgetId), TestInitObj)
        advanceUntilIdle()

        composer.suspendDispatch(IncrementAction())
        advanceUntilIdle()

        val states = composer.uiStateFlow.value
        assertEquals(1, states[0].value)

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `dispose clears widget state`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = singleDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initializeWithWidgets(listOf(TestWidgetId), TestInitObj)
        advanceUntilIdle()

        composer.dispose()
        scope.cancel()
        assertTrue(composer.currentWidgetIds().isEmpty())
    }

    @Test
    fun `currentWidgetIds returns single widget`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = singleDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initializeWithWidgets(listOf(TestWidgetId), TestInitObj)
        advanceUntilIdle()

        val ids = composer.currentWidgetIds()
        assertEquals(1, ids.size)
        assertEquals(TestWidgetId, ids[0])

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `multiple dispatches accumulate state changes`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val composer = singleDataComposer(TestStoreFactory(), scope, NoOpActionHandler())

        composer.initializeWithWidgets(listOf(TestWidgetId), TestInitObj)
        advanceUntilIdle()

        repeat(5) {
            composer.suspendDispatch(IncrementAction())
        }
        advanceUntilIdle()

        val states = composer.uiStateFlow.value
        assertEquals(5, states[0].value)

        composer.dispose()
        scope.cancel()
    }
}
