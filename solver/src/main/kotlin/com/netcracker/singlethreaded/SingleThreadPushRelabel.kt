package com.netcracker.singlethreaded

class SingleThreadPushRelabel : MaxFlowSolver {

    override fun findMaxFlow(graph: MyGraph): MyGraph {
        val sourceVertex = "1"
        val sinkVertex = graph
                .vertexSet()
                .size
                .toString()

        val verticesAmount = graph.getVerticesAmount()
        val solver = PushRelabel(verticesAmount)
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
        println("Max flow: $maxFlow")
        return graph
    }
}