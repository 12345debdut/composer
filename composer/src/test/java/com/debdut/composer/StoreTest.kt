package com.debdut.composer

import app.cash.turbine.test
import com.debdut.composer.action.ActionId
import com.debdut.composer.action.StoreAction
import com.debdut.composer.action.UIComposerAction
import com.debdut.composer.state.UIState
import com.debdut.composer.state.UIStateType
import com.debdut.composer.state.UIStateDefaultType
import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.store.Store
import com.debdut.composer.store.StoreId
import com.debdut.composer.store.StoreInitObj
import com.debdut.composer.store.syntax.currentState
import com.debdut.composer.store.syntax.emitState
import com.debdut.composer.store.syntax.updateState
import com.debdut.composer.store.syntax.suspendDispatch
import com.debdut.composer.store.syntax.storeScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StoreTest {

    // Test fixtures
    data class TestState(
        override val type: UIStateType = UIStateDefaultType,
        override val visible: Boolean = true,
        override val widgetId: WidgetId = TestWidgetId,
        val count: Int = 0,
        val label: String = ""
    ) : UIState

    object TestWidgetId : WidgetId {
        override val id: String = "test-widget"
    }

    object TestStoreId : StoreId {
        override val id: String = "test-store"
    }
    object TestInitObj : StoreInitObj

    object IncrementActionId : ActionId {
        override val id: String = "increment"
    }

    object DecrementActionId : ActionId {
        override val id: String = "decrement"
    }

    data class IncrementAction(
        override val actionId: ActionId = IncrementActionId,
        val amount: Int = 1
    ) : StoreAction

    data class DecrementAction(
        override val actionId: ActionId = DecrementActionId
    ) : StoreAction

    data class UnsubscribedAction(
        override val actionId: ActionId = object : ActionId {
            override val id: String = "unsubscribed"
        }
    ) : StoreAction

    data class TestUIAction(val message: String) : UIComposerAction {
        override val actionId: ActionId = object : ActionId {
            override val id: String = "test-ui-action"
        }
    }

    class TestStore : Store<TestState, TestInitObj, TestInitObj>() {
        override val storeId: StoreId = TestStoreId
        override val subscribedStoreAction: Set<ActionId> = setOf(IncrementActionId, DecrementActionId)

        var receiveCallCount = 0
        var lastReceivedAction: StoreAction? = null
        var invokeOnStateUpdateCallCount = 0

        override fun initialise(globalModel: TestInitObj) {
            emitState { TestState(count = 0, label = "initialized") }
        }

        override suspend fun receive(action: StoreAction, storeId: StoreId) {
            receiveCallCount++
            lastReceivedAction = action
            when (action) {
                is IncrementAction -> updateState { copy(count = count + action.amount) }
                is DecrementAction -> updateState { copy(count = count - 1) }
            }
        }

        override fun invokeOnStateUpdate(prevState: TestState?, currentState: TestState?) {
            invokeOnStateUpdateCallCount++
        }
    }

    private lateinit var testScope: TestScope
    private lateinit var store: TestStore

    @Before
    fun setup() {
        testScope = TestScope(StandardTestDispatcher())
        store = TestStore()
        store.updateCoroutineScope(testScope)
    }

    @Test
    fun `initial state is null before initialise`() {
        assertNull(store.currentState)
    }

    @Test
    fun `emitState sets initial state`() {
        store.initialise(TestInitObj)
        val state = store.currentState
        assertNotNull(state)
        assertEquals(0, state!!.count)
        assertEquals("initialized", state.label)
    }

    @Test
    fun `updateState transforms existing state`() {
        store.initialise(TestInitObj)
        store.updateState { copy(count = 42) }
        assertEquals(42, store.currentState!!.count)
    }

    @Test
    fun `updateState returns null when state not initialized`() {
        val result = store.updateState { copy(count = 10) }
        assertNull(result)
    }

    @Test
    fun `updateState preserves unmodified fields`() {
        store.initialise(TestInitObj)
        store.updateState { copy(count = 5) }
        assertEquals("initialized", store.currentState!!.label)
        assertEquals(5, store.currentState!!.count)
    }

    @Test
    fun `invokeOnStateUpdate called after updateState`() {
        store.initialise(TestInitObj)
        assertEquals(0, store.invokeOnStateUpdateCallCount)
        store.updateState { copy(count = 1) }
        assertEquals(1, store.invokeOnStateUpdateCallCount)
    }

    @Test
    fun `receive processes subscribed action`() = testScope.runTest {
        store.initialise(TestInitObj)
        store.receive(IncrementAction(amount = 5), StoreId.Empty)
        assertEquals(5, store.currentState!!.count)
        assertEquals(1, store.receiveCallCount)
    }

    @Test
    fun `receive handles multiple sequential actions`() = testScope.runTest {
        store.initialise(TestInitObj)
        store.receive(IncrementAction(amount = 3), StoreId.Empty)
        store.receive(IncrementAction(amount = 2), StoreId.Empty)
        store.receive(DecrementAction(), StoreId.Empty)
        assertEquals(4, store.currentState!!.count)
        assertEquals(3, store.receiveCallCount)
    }

    @Test
    fun `uiStateFlow emits state changes`() = testScope.runTest {
        store.uiStateFlow.test {
            assertNull(awaitItem()) // initial null

            store.initialise(TestInitObj)
            val initialized = awaitItem()
            assertNotNull(initialized)
            assertEquals(0, initialized!!.count)

            store.updateState { copy(count = 10) }
            assertEquals(10, awaitItem()!!.count)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `storeScope is accessible after coroutineScope is set`() {
        assertNotNull(store.storeScope)
    }

    @Test
    fun `side effects flow emits UIComposerActionHolder`() = testScope.runTest {
        store.initialise(TestInitObj)
        store.uiSideEffects.test {
            store.suspendDispatch(TestUIAction("hello"))
            val holder = awaitItem()
            assertEquals(TestStoreId, holder.storeId)
            assertEquals("hello", (holder.action as TestUIAction).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
