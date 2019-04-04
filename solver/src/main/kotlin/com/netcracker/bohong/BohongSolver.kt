package com.netcracker.bohong

import com.netcracker.MaxFlowSolver
import com.netcracker.OutputMode
import com.netcracker.OutputMode.*
import com.netcracker.addMaxFlowPrefix
import com.netcracker.util.DrawingMode
import com.netcracker.util.MyGraph
import com.netcracker.util.assertIsValid
import com.netcracker.util.toDotString

class BohongSolver : MaxFlowSolver {

    override fun solve(
            originalGraph: MyGraph,
            outputMode: OutputMode,
            drawingMode: DrawingMode
    ): String {
        val lockFreeGraph = createLockFreeGraph(originalGraph)
        val maxFlow = lockFreeGraph.findMaxFlow()
        lockFreeGraph.assertIsValid()
        return when (outputMode) {
            FLOW_GRAPH -> lockFreeGraph
                    .toDotString(drawingMode)
                    .addMaxFlowPrefix(maxFlow)
            MAX_FLOW_VALUE -> maxFlow.toString()
            ELEMENTARY_ACTIONS -> "Not supported"
        }
    }
}