package com.netcracker.bohong

import com.netcracker.bohong.checkers.SnapshotTerminationChecker
import com.netcracker.bohong.checkers.processDataFromSnapshotTerminationChecker
import com.netcracker.bohong.processors.VertexProcessor
import com.netcracker.util.min
import com.netcracker.util.splitIntoParts
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicBoolean

private const val iterationsPerCheck = 50

class LockFreeGraph(
        val vertices: List<LockFreeVertex>,
        val sourceVertexId: Int = 0,
        val sinkVertexId: Int = vertices.size - 1
) {

    val edges = vertices.flatMap { it.edges }

    fun findMaxFlow(): Long {
        init(sourceVertexId)
        var iterationNumber = 0
        while (middleVerticesHaveExcess()) {
            for (iteration in 0 until iterationsPerCheck) {
                //println("\n=== Iteration: $iterationNumber =========\n")
                for (currentVertexId in 1 until vertices.size) {
                    processVertex(currentVertexId)
                }
                ++iterationNumber
                //println(this)
            }
        }
        return vertices[sinkVertexId]
                .excess
                .get()
    }

    fun findMaxFlowParallel(
            threadAmount: Int = 4
    ): Long {
        init(sourceVertexId)
        val solutionComplete = AtomicBoolean(false)

//        val terminationChecker = BaseTerminationChecker(this, solutionComplete)
//        val terminationChecker = HistoricalTerminationChecker(this, solutionComplete)
        val terminationChecker = SnapshotTerminationChecker(this, solutionComplete)

        val terminationCheckBarrier = CyclicBarrier(
                threadAmount,
//                BaseTerminationChecker(this, solutionComplete)
                terminationChecker
        )
        val assignedVertexIds = (0 until vertices.size)
                .filter { it != sourceVertexId && it != sinkVertexId }
                .shuffled()
                .splitIntoParts(threadAmount)
        val vertexProcessingRunnables = assignedVertexIds.map { vertexIds ->
            VertexProcessor(
                    this,
                    vertexIds,
                    solutionComplete,
                    terminationCheckBarrier
            )
//            HistoricalVertexProcessor(
//                    this,
//                    vertexIds,
//                    solutionComplete,
//                    terminationCheckBarrier
//            )
        }
        executeRunnables(threadAmount, vertexProcessingRunnables)

//        processHistoryRecordsFromProcessors(vertexProcessingRunnables)
//        processHistoryRecordsFromTerminationChecker(terminationChecker)
        processDataFromSnapshotTerminationChecker(terminationChecker)

        return vertices[sinkVertexId]
                .excess
                .get()
    }

    fun middleVerticesHaveExcess(): Boolean {
        for (vertex in vertices) {
            if (isSourceOrSink(vertex.id)) {
                continue
            }
            if (vertex.excess.get() > 0) {
                return true
            }
        }
        return false
    }

    fun init(sourceVertexId: Int) {
        resetAllVertices()
        resetFlowInAllEdges()

        val sourceVertex = vertices[sourceVertexId]
        setSourceVertexHeight(sourceVertex)
        saturateEdgesFromSource(sourceVertex)
    }

    fun resetAllVertices() {
        for (vertex in vertices) {
            vertex.excess.set(0)
            vertex.height.set(0)
        }
    }

    fun resetFlowInAllEdges() {
        for (edge in edges) {
            edge.remainingCapacity.set(edge.maxCapacity)
        }
    }

    fun setSourceVertexHeight(sourceVertex: LockFreeVertex) {
        val sourceHeight = vertices.size.toLong()
        sourceVertex.height.set(sourceHeight)
    }

    fun saturateEdgesFromSource(sourceVertex: LockFreeVertex) {
        for (edge in sourceVertex.edges) {
            val maxCapacity = edge.maxCapacity
            val reverseEdge = edge.getReverseEdge(vertices)
            edge.remainingCapacity.set(0)
            reverseEdge.remainingCapacity.set(maxCapacity)
            val endVertex = vertices[edge.endVertexId]
            endVertex.excess.addAndGet(maxCapacity)
        }
    }

    fun processVertex(currentVertexId: Int) {
        if (isSourceOrSink(currentVertexId)) {
            return
        }
        val currentVertex = vertices[currentVertexId]
        if (currentVertex.excess.get() > 0) {
            val (lowestNeighbourEdge, lowestNeighbourHeight) = findLowestNeighbourEdge(currentVertexId)
            if (currentVertex.height.get() > lowestNeighbourHeight) {
                push(lowestNeighbourEdge)
            } else {
                relabel(currentVertex, lowestNeighbourHeight)
            }
        }
    }

    fun isSourceOrSink(currentVertexId: Int) = currentVertexId == sourceVertexId || currentVertexId == sinkVertexId

    fun push(edge: LockFreeEdge) {
        val reverseEdge = edge.getReverseEdge(vertices)
        val currentVertex = vertices[edge.startVertexId]
        val neightbourVertex = vertices[edge.endVertexId]

        val flowDiff = min(currentVertex.excess, edge.remainingCapacity)
        edge.remainingCapacity.addAndGet(-flowDiff)
        reverseEdge.remainingCapacity.addAndGet(+flowDiff)
        currentVertex.excess.addAndGet(-flowDiff)
        neightbourVertex.excess.addAndGet(+flowDiff)
    }

    fun relabel(currentVertex: LockFreeVertex,
                lowestNeighbourHeight: Long) {
        currentVertex.height.set(lowestNeighbourHeight + 1)
    }

    fun findLowestNeighbourEdge(currentVertexId: Int): Pair<LockFreeEdge, Long> {
        val currentVertex = vertices[currentVertexId]
        var lowestNeighbourEdge: LockFreeEdge? = null
        var lowestNeighbourHeight = Long.MAX_VALUE
        for (edge in currentVertex.residualEdges) {
            val destinationVertex = vertices[edge.endVertexId]
            val destinationVertexHeight = destinationVertex.height.get()
            if (destinationVertexHeight < lowestNeighbourHeight) {
                lowestNeighbourHeight = destinationVertexHeight
                lowestNeighbourEdge = edge
            }
        }

        if (lowestNeighbourEdge != null) {
            return Pair(lowestNeighbourEdge, lowestNeighbourHeight)
        } else {
            throw IllegalStateException("Unable to find the lowest neighbour")
        }
    }

    override fun toString() = vertices.joinToString("\n")
}

