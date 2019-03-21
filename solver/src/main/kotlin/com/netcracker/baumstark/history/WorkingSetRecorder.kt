package com.netcracker.baumstark.history

import com.netcracker.baumstark.BaumVertex

interface WorkingSetRecorder {
    fun record(
            iteration: Int,
            workingSet: Set<Int>,
            vertices: List<BaumVertex>
    )

    fun save()
}

