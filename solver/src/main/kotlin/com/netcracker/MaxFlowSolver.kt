package com.netcracker

import com.netcracker.util.DrawingMode
import com.netcracker.util.MyGraph

interface MaxFlowSolver {

    fun solve(
            originalGraph: MyGraph,
            outputMode: OutputMode = OutputMode.MAX_FLOW_VALUE,
            drawingMode: DrawingMode = DrawingMode.SIMPLE
    ) : String
}

fun String.addMaxFlowPrefix(maxFlowValue: Long) = "// max_flow:$maxFlowValue\n$this"