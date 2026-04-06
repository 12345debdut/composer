package com.debdut.composer.sample.counter

import com.debdut.composer.composer.ui.WidgetId
import com.debdut.composer.state.UIState
import com.debdut.composer.state.UIStateDefaultType
import com.debdut.composer.state.UIStateType

data class CounterState(
    override val type: UIStateType = UIStateDefaultType,
    override val visible: Boolean = true,
    override val widgetId: WidgetId = CounterWidgetId,
    val count: Int = 0,
    val label: String = "Counter"
) : UIState
