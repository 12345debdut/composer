package com.debdut.composer

import app.cash.turbine.test
import com.debdut.composer.action.Action
import com.debdut.composer.action.ActionId
import com.debdut.composer.action.StoreAction
import com.debdut.composer.action.holder.DataComposerActionHolder
import com.debdut.composer.composer.data.DataComposerActionHandler
import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.extensions.listDataComposer
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
class ListDataComposerImplTest {

    object EmptyWidgetId : WidgetId {
        override val id: String = ""
    }

    data class TestState(
        override val type: UIStateType = UIStateDefaultType,
        override val visible: Boolean = true,
        override val widgetId: WidgetId = EmptyWidgetId,
        val value: String = ""
    ) : UIState

    object TestInitObj : StoreInitObj

    object Widget1Id : WidgetId {
        override val id: String = "widget-1"
    }

    object Widget2Id : WidgetId {
        override val id: String = "widget-2"
    }

    object Store1Id : StoreId {
        override val id: String = "store-1"
    }

    object Store2Id : StoreId {
        override val id: String = "store-2"
    }

    object UpdateActionId : ActionId {
        override val id: String = "update"
    }

    data class UpdateAction(
        val newValue: String,
        override val actionId: ActionId = UpdateActionId
    ) : StoreAction

    class TestStore1 : Store<TestState, TestInitObj, TestInitObj>() {
        override val storeId: StoreId = Store1Id
        override val subscribedStoreAction: Set<ActionId> = setOf(UpdateActionId)

        override fun initialise(globalModel: TestInitObj) {
            emitState { TestState(widgetId = Widget1Id, value = "store1-init") }
        }

        override suspend fun receive(action: StoreAction, storeId: StoreId) {
            when (action) {
                is UpdateAction -> updateState { copy(value = action.newValue) }
            }
        }
    }

    class TestStore2 : Store<TestState, TestInitObj, TestInitObj>() {
        override val storeId: StoreId = Store2Id
        override val subscribedStoreAction: Set<ActionId> = setOf(UpdateActionId)

        override fun initialise(globalModel: TestInitObj) {
            emitState { TestState(widgetId = Widget2Id, value = "store2-init") }
        }

        override suspend fun receive(action: StoreAction, storeId: StoreId) {
            when (action) {
                is UpdateAction -> updateState { copy(value = action.newValue) }
            }
        }
    }

    class TestStoreFactory : StoreFactory<TestState, TestInitObj, TestInitObj> {
        val createdStores = mutableListOf<Store<TestState, TestInitObj, TestInitObj>>()

        override fun get(widgetId: WidgetId): Store<TestState, TestInitObj, TestInitObj> {
            val store = when (widgetId) {
                Widget1Id -> TestStore1()
                Widget2Id -> TestStore2()
                else -> throw IllegalArgumentException("Unknown widget: $widgetId")
            }
            createdStores.add(store)
            return store
        }
    }

    class TestActionHandler : DataComposerActionHandler {
        val receivedActions = mutableListOf<DataComposerActionHolder>()
        val allActions = mutableListOf<Action>()

        override suspend fun receiveAction(dataComposerActionHolder: DataComposerActionHolder) {
            receivedActions.add(dataComposerActionHolder)
        }

        override fun receiveAllActions(action: Action) {
            allActions.add(action)
        }
    }

    @Test
    fun `initialiseWithWidgets creates stores and emits initial state`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val storeFactory = TestStoreFactory()
        val actionHandler = TestActionHandler()
        val composer = listDataComposer(storeFactory, scope, actionHandler)

        composer.initialiseWithWidgets(listOf(Widget1Id, Widget2Id), TestInitObj)
        advanceUntilIdle()

        val states = composer.uiStateFlow.value
        assertEquals(2, states.size)
        assertEquals("store1-init", states[0].value)
        assertEquals("store2-init", states[1].value)

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `dispatch routes StoreAction to all subscribed stores`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val storeFactory = TestStoreFactory()
        val actionHandler = TestActionHandler()
        val composer = listDataComposer(storeFactory, scope, actionHandler)

        composer.initialiseWithWidgets(listOf(Widget1Id, Widget2Id), TestInitObj)
        advanceUntilIdle()

        composer.suspendDispatch(UpdateAction("updated"))
        advanceUntilIdle()

        val states = composer.uiStateFlow.value
        assertEquals("updated", states[0].value)
        assertEquals("updated", states[1].value)

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `dispose clears all stores and state`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val storeFactory = TestStoreFactory()
        val actionHandler = TestActionHandler()
        val composer = listDataComposer(storeFactory, scope, actionHandler)

        composer.initialiseWithWidgets(listOf(Widget1Id), TestInitObj)
        advanceUntilIdle()

        composer.dispose()
        scope.cancel()
        assertTrue(composer.currentWidgetIds().isEmpty())
    }

    @Test
    fun `currentWidgetIds returns initialized widget ids`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val storeFactory = TestStoreFactory()
        val actionHandler = TestActionHandler()
        val composer = listDataComposer(storeFactory, scope, actionHandler)

        composer.initialiseWithWidgets(listOf(Widget1Id, Widget2Id), TestInitObj)
        advanceUntilIdle()

        val ids = composer.currentWidgetIds()
        assertEquals(2, ids.size)
        assertTrue(ids.contains(Widget1Id))
        assertTrue(ids.contains(Widget2Id))

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `receiveAllActions is called for every dispatched action`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val storeFactory = TestStoreFactory()
        val actionHandler = TestActionHandler()
        val composer = listDataComposer(storeFactory, scope, actionHandler)

        composer.initialiseWithWidgets(listOf(Widget1Id), TestInitObj)
        advanceUntilIdle()

        val action = UpdateAction("test")
        composer.suspendDispatch(action)
        advanceUntilIdle()

        assertTrue(actionHandler.allActions.contains(action))

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `hidden widgets are filtered from state`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val storeFactory = TestStoreFactory()
        val actionHandler = TestActionHandler()
        val composer = listDataComposer(storeFactory, scope, actionHandler)

        composer.initialiseWithWidgets(listOf(Widget1Id, Widget2Id), TestInitObj)
        advanceUntilIdle()

        val store1 = storeFactory.createdStores[0]
        store1.mutableUiStateFlow.value = TestState(widgetId = Widget1Id, value = "hidden", visible = false)
        advanceUntilIdle()

        val states = composer.uiStateFlow.value
        assertEquals(1, states.size)
        assertEquals("store2-init", states[0].value)

        composer.dispose()
        scope.cancel()
    }

    @Test
    fun `factory creates stores for each widget id`() = runTest {
        val scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val storeFactory = TestStoreFactory()
        val actionHandler = TestActionHandler()
        val composer = listDataComposer(storeFactory, scope, actionHandler)

        composer.initialiseWithWidgets(listOf(Widget1Id, Widget2Id), TestInitObj)
        advanceUntilIdle()

        assertEquals(2, storeFactory.createdStores.size)

        composer.dispose()
        scope.cancel()
    }
}
