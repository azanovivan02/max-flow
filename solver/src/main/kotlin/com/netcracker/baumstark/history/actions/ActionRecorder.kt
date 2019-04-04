package com.netcracker.baumstark.history.actions

interface ActionRecorder {

    fun recordAction(runnableIndex: Int, type: ActionType)

    enum class ActionType {
    }
}

class DummyActionRecorder : ActionRecorder {
    override fun recordAction(runnableIndex: Int, type: ActionRecorder.ActionType) {
    }
}