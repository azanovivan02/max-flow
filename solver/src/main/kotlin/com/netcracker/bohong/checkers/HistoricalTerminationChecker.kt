package com.netcracker.bohong.checkers

import com.netcracker.bohong.LockFreeGraph
import java.util.concurrent.atomic.AtomicBoolean

class HistoricalTerminationChecker(
        val graph: LockFreeGraph,
        val solutionComplete: AtomicBoolean
) : Runnable {

    val activeVerticesAmountHistory = mutableListOf<Int>()

    override fun run() {
        val activeVerticesAmount = countActiveVertices()
        activeVerticesAmountHistory += activeVerticesAmount
        if(activeVerticesAmount == 0) {
            solutionComplete.set(true)
        }
    }

    fun countActiveVertices(): Int {
        var activeVerticesAmount = 0
        for (vertex in graph.vertices) {
            if (graph.isSourceOrSink(vertex.id)) {
                continue
            }
            if (vertex.excess.get() > 0) {
                ++activeVerticesAmount
            }
        }
        return activeVerticesAmount
    }
}