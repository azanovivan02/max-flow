package com.netcracker.baumstark.history

interface WorkingSetRecorder {
    fun record(iteration: Int, workingSet: Collection<Int>)

    fun save()
}

data class WorkingSetRecord(
        val iteration: Int,
        val workingSet: Collection<Int>
)

