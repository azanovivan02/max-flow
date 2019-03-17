package com.netcracker.bohong.processors

import com.netcracker.bohong.LockFreeGraph
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicBoolean

class VertexProcessor(
        val graph: LockFreeGraph,
        val assignedVertexIds: Iterable<Int>,
        val solutionComplete: AtomicBoolean,
        val terminationCheckBarrier: CyclicBarrier,
        val iterationsBetweenChecks: Int = 5
) : Runnable {

    override fun run() {
        while (true) {
            for (iteration in 0 until iterationsBetweenChecks) {
                for (currentVertexId in assignedVertexIds) {
                    graph.processVertex(currentVertexId)
                }
            }
//            println("Iteration complete, awaiting termination check")
            terminationCheckBarrier.await()
            if (solutionComplete.get()) {
                return
            }
        }
    }
}