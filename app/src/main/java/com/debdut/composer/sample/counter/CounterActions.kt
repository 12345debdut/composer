package com.debdut.composer.sample.counter

import com.debdut.composer.action.ActionId
import com.debdut.composer.action.StoreAction
import com.debdut.composer.action.UIComposerAction

// Action IDs
object IncrementActionId : ActionId {
    override val id: String = "increment"
}

object DecrementActionId : ActionId {
    override val id: String = "decrement"
}

object ResetActionId : ActionId {
    override val id: String = "reset"
}

object ShowToastActionId : ActionId {
    override val id: String = "show-toast"
}

// Store Actions (handled by the Store)
data class IncrementAction(
    override val actionId: ActionId = IncrementActionId
) : StoreAction

data class DecrementAction(
    override val actionId: ActionId = DecrementActionId
) : StoreAction

data class ResetAction(
    override val actionId: ActionId = ResetActionId
) : StoreAction

// UI Actions (handled by the Fragment — side effects like toasts, navigation)
data class ShowToastAction(
    val message: String,
    override val actionId: ActionId = ShowToastActionId
) : UIComposerAction
