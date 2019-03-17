package com.netcracker.baumstark.history

class DummyWorkingSetRecorder : WorkingSetRecorder {
    override fun record(iteration: Int, workingSet: Collection<Int>) {
    }

    override fun save() {
    }
}