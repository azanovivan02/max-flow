package com.netcracker.bohong.processors

import com.netcracker.bohong.LockFreeGraph
import com.netcracker.bohong.VertexHistoryRecord
import com.netcracker.bohong.createHistoryRecord
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicBoolean

private const val iterationsBetweenChecks = 10

class HistoricalVertexProcessor(
        val graph: LockFreeGraph,
        val assignedVertexIds: Iterable<Int>,
        val solutionComplete: AtomicBoolean,
        val terminationCheckBarrier: CyclicBarrier
) : Runnable {

    val historyRecords: MutableList<VertexHistoryRecord> = mutableListOf()

    override fun run() {
        var globalIteration = 0
        while (true) {
            for (nestedIteration in 0 until iterationsBetweenChecks) {
                for (currentVertexId in assignedVertexIds) {
                    val currentVertex = graph.vertices[currentVertexId]
                    if (currentVertex.excess.get() > 0) {
                        val record = currentVertex.createHistoryRecord(globalIteration)
                        historyRecords += record
                    }
                    graph.processVertex(currentVertexId)
                }
                ++globalIteration
            }
//            println("Iteration complete, awaiting termination check")
            terminationCheckBarrier.await()
            if (solutionComplete.get()) {
                return
            }
        }
    }
}