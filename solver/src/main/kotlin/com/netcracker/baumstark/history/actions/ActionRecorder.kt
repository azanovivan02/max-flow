package com.netcracker.baumstark.history.actions

import com.netcracker.baumstark.history.actions.ActionRecorder.ActionType.UNKNOWN

interface ActionRecorder {

    fun recordAction(runnableIndex: Int, action: ActionType = UNKNOWN)

    fun recordActions(runnableIndex: Int, actions: List<ActionType>)

    enum class ActionType {
        UNKNOWN,
        RELABEL,
        PUSH
    }
}

class DummyActionRecorder : ActionRecorder {
    override fun recordAction(runnableIndex: Int, action: ActionRecorder.ActionType) {
    }

    override fun recordActions(runnableIndex: Int, actions: List<ActionRecorder.ActionType>){
    }
}