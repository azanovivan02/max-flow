package com.netcracker.baumstark

import com.netcracker.MaxFlowSolver
import com.netcracker.OutputMode
import com.netcracker.OutputMode.FLOW_GRAPH
import com.netcracker.OutputMode.MAX_FLOW_VALUE
import com.netcracker.addMaxFlowPrefix
import com.netcracker.util.*

class BaumstarkSolver(
        val logger: Logger = StandardOutputLogger(true, LogLevel.DEBUG),
        val threadAmount: Int
) : MaxFlowSolver {

    override fun solve(
            originalGraph: MyGraph,
            outputMode: OutputMode,
            drawingMode: DrawingMode
    ): String {
        val baumGraph = createBaumGraph(originalGraph)
        val executor = BaumExecutor(
                graph = baumGraph,
                logger = logger,
                threadAmount = threadAmount
        )
        val maxFlow = executor.findMaxFlowValue()
        return when (outputMode) {
            FLOW_GRAPH -> baumGraph
                    .toDotString(drawingMode)
                    .addMaxFlowPrefix(maxFlow)
            MAX_FLOW_VALUE -> maxFlow.toString()
        }
    }
}