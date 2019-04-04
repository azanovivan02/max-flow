package com.netcracker.baumstark.history.actions

import java.util.concurrent.atomic.AtomicLong

class DefaultActionRecorder : ActionRecorder {

    val totalActionAmount = AtomicLong(0)

    override fun recordAction(runnableIndex: Int, action: ActionRecorder.ActionType) {
        totalActionAmount.incrementAndGet()
    }

    override fun recordActions(runnableIndex: Int, actions: List<ActionRecorder.ActionType>) {
        totalActionAmount.addAndGet(actions.size.toLong())
    }
}