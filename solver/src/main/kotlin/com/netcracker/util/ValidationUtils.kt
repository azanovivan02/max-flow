package com.netcracker.util

import com.netcracker.bohong.LockFreeGraph
import com.netcracker.bohong.flow
import java.lang.IllegalStateException

fun LockFreeGraph.assertIsValid() {
    assertVerticesHaveZeroDivergence()
//    assertEdgesMaintainFlowConstraint()
}

private fun LockFreeGraph.assertVerticesHaveZeroDivergence() {
    for (vertex in vertices) {
        if (isSourceOrSink(vertex.id)) {
            continue
        }
        val totalFlow = vertex
                .edges
                .map { it.flow }
                .sum()
        if (totalFlow != 0L) {
            throw IllegalStateException("Non-zero divergence on vertex. Id: ${vertex.id}, div: $totalFlow")
        }
    }
}

private fun LockFreeGraph.assertEdgesMaintainFlowConstraint() {
    for (edge in edges) {
        if (edge.flow > edge.maxCapacity) {
            throw IllegalStateException("Edge has flow larger that capacity. Edge: $edge")
        }
    }
}
