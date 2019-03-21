package com.netcracker.baumstark.history

import com.netcracker.baumstark.BaumVertex

class DummyWorkingSetRecorder : WorkingSetRecorder {
    override fun record(iteration: Int, workingSet: Set<Int>, vertices: List<BaumVertex>) {
    }

    override fun save() {
    }
}