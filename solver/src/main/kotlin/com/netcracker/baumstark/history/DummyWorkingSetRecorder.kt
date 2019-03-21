package com.netcracker.baumstark.history

import com.netcracker.baumstark.BaumGraph

class DummyWorkingSetRecorder : WorkingSetRecorder {
    override fun record(iteration: Int, workingSet: Set<Int>, graph: BaumGraph) {
    }

    override fun save() {
    }
}