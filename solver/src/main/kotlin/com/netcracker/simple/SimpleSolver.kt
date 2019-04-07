package com.netcracker.simple

import com.netcracker.MaxFlowSolver
import com.netcracker.OutputMode
import com.netcracker.util.DrawingMode
import com.netcracker.util.MyGraph

class SimpleSolver : MaxFlowSolver {

    override fun solve(
            originalGraph: MyGraph,
            outputMode: OutputMode,
            drawingMode: DrawingMode): String {
        val (maxFlow, graph) = findMaxFlow(originalGraph)
        return maxFlow.toString()
    }

    private fun findMaxFlow(graph: MyGraph): Pair<Int, MyGraph> {
        val sourceVertex = "1"
        val sinkVertex = graph
                .vertexSet()
                .size
                .toString()

        val verticesAmount = graph.getVerticesAmount()
        val solver = SimpleExecutor(verticesAmount)
        for (edge in graph.edgeSet()) {
            val from = graph.getEdgeSource(edge).toInt()
            val to = graph.getEdgeTarget(edge).toInt()
            val cap = graph.getEdgeWeight(edge).toInt()
            solver.addEdge(
                    rawFrom = from,
                    rawTo = to,
                    cap = cap
            )
        }
        val maxFlow = solver.getMaxFlow(1, verticesAmount)
        return Pair(maxFlow, graph)
    }
}