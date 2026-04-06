package com.debdut.composer.compose

import com.debdut.composer.action.holder.UIComposerActionHolder
import com.debdut.composer.composer.data.DataComposer
import com.debdut.composer.composer.data.host.DataComposerHost
import com.debdut.composer.state.UIState
import com.debdut.composer.state.UIStateDefaultType
import com.debdut.composer.state.UIStateType
import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.store.StoreInitObj
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertSame
import org.junit.Test

class ComposerExtensionsTest {

    private data class TestState(
        override val type: UIStateType = UIStateDefaultType,
        override val visible: Boolean = true,
        override val widgetId: WidgetId = object : WidgetId { override val id = "test" }
    ) : UIState

    private object TestInitObj : StoreInitObj

    @Test
    fun `uiStateFlow returns StateFlow from container`() {
        val stateFlow = MutableStateFlow<List<TestState>>(emptyList())
        val mockComposer = mockk<DataComposer<TestState, TestInitObj, TestInitObj>> {
            every { uiStateFlow } returns stateFlow
            every { uiActionHolder } returns MutableSharedFlow()
        }
        val mockHost = mockk<DataComposerHost<TestState, TestInitObj, TestInitObj>> {
            every { container } returns mockComposer
        }

        assertSame(stateFlow, mockHost.uiStateFlow)
    }

    @Test
    fun `uiActionFlow returns SharedFlow from container`() {
        val actionFlow = MutableSharedFlow<UIComposerActionHolder>()
        val mockComposer = mockk<DataComposer<TestState, TestInitObj, TestInitObj>> {
            every { uiStateFlow } returns MutableStateFlow(emptyList())
            every { uiActionHolder } returns actionFlow
        }
        val mockHost = mockk<DataComposerHost<TestState, TestInitObj, TestInitObj>> {
            every { container } returns mockComposer
        }

        assertSame(actionFlow, mockHost.uiActionFlow)
    }

    @Test
    fun `uiStateFlow reflects state emissions from container`() {
        val stateFlow = MutableStateFlow<List<TestState>>(emptyList())
        val mockComposer = mockk<DataComposer<TestState, TestInitObj, TestInitObj>> {
            every { uiStateFlow } returns stateFlow
            every { uiActionHolder } returns MutableSharedFlow()
        }
        val mockHost = mockk<DataComposerHost<TestState, TestInitObj, TestInitObj>> {
            every { container } returns mockComposer
        }

        val initial = mockHost.uiStateFlow.value
        assert(initial.isEmpty())

        val state = TestState()
        stateFlow.value = listOf(state)

        assertSame(state, mockHost.uiStateFlow.value.first())
    }
}
