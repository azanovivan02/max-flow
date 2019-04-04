package com.netcracker.baumstark

import com.netcracker.MaxFlowSolver
import com.netcracker.OutputMode
import com.netcracker.OutputMode.*
import com.netcracker.addMaxFlowPrefix
import com.netcracker.baumstark.history.actions.DefaultActionRecorderAnalyzer
import com.netcracker.baumstark.history.workingset.DummyWorkingSetRecorder
import com.netcracker.baumstark.history.workingset.WorkingSetRecorder
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
        val workingSetRecorder = DummyWorkingSetRecorder()
        val executor = BaumExecutor(
                graph = baumGraph,
                logger = logger,
                workingSetRecorder = workingSetRecorder,
                threadAmount = threadAmount
        )
        val analyzer = DefaultActionRecorderAnalyzer()
        val maxFlow = executor.findMaxFlowValue(
                skipInit = false,
                recorderAnalyzer = analyzer
        )
        return when (outputMode) {
            FLOW_GRAPH -> baumGraph
                    .createRecord().toDotString(drawingMode)
                    .addMaxFlowPrefix(maxFlow)
            MAX_FLOW_VALUE -> maxFlow.toString()
            ELEMENTARY_ACTIONS -> analyzer.totalActionAmount.toString()
        }
    }
}