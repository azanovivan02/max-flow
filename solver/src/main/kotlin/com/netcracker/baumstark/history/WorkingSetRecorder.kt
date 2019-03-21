package com.netcracker.baumstark.history

import com.netcracker.baumstark.BaumGraph

interface WorkingSetRecorder {
    fun record(
            iteration: Int,
            workingSet: Set<Int>,
            graph: BaumGraph
    )

    fun save()
}

