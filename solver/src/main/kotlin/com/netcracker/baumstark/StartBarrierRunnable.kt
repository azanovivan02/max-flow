package com.netcracker.baumstark

import com.netcracker.baumstark.history.WorkingSetRecorder
import com.netcracker.util.Logger
import com.netcracker.util.splitIntoEvenParts
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class StartBarrierRunnable(
        val iterationNumber: AtomicInteger,
        val solutionFound: AtomicBoolean,
        val subsetsList: MutableList<Set<Int>>,
        val graph: BaumGraph,
        val threadAmount: Int,
        val logger: Logger,
        val workingSetRecorder: WorkingSetRecorder
) : Runnable {

    val vertices = graph.vertices
    val edges = vertices.flatMap { it.edges }
    val sourceVertexHeight = vertices.size.toLong()

    val sourceVertex = vertices[graph.sourceVertexId]
    val sinkVertex = vertices[graph.sinkVertexId]

    private var workingSet = createInitialWorkingSet()

    override fun run() {
        logger.info { "Iteration $iterationNumber, working set: [${workingSet.joinToString(", ")}]" }
        if (iterationNumber.get() > 0) {
            workingSet = createNewWorkingSet(workingSet)
            if (workingSet.isEmpty()) {
                solutionFound.set(true)
                return
            }

            updateVerticesInNewWorkingSet(workingSet)
            updateSinkVertex()
        }

        workingSetRecorder.record(iterationNumber.get(), workingSet, graph)

//        validatePreflowIfNeeded()

        for (index in 0 until subsetsList.size) {
            subsetsList[index] = emptySet()
        }
        val subsets = workingSet
                .shuffled()
                .splitIntoEvenParts(threadAmount)
        for ((index, subset) in subsets.withIndex()) {
            subsetsList[index] = subset.toSet()
        }

        iterationNumber.incrementAndGet()
    }

    private fun updateVerticesInNewWorkingSet(workingSet: Set<Int>) {
        workingSet
                .map(vertices::get)
                .forEach(BaumVertex::update)
    }

    private fun updateSinkVertex() {
        val sinkAddedExcess = sinkVertex.addedExcess.get()
        sinkVertex.excess.set(sinkAddedExcess)
    }

    private fun createNewWorkingSet(oldWorkingSet: Set<Int>): Set<Int> {
        val discoveredVertices = oldWorkingSet
                .map(vertices::get)
                .flatMap(BaumVertex::discoveredVertices)
        logger.debug { "   discovered vertices: [${discoveredVertices.joinToString(", ")}]" }

        val newWorkingSet = discoveredVertices
                .map(vertices::get)
                .filter { it.height.get() < sourceVertexHeight }
                .map { it.id }
                .toSet()
        return newWorkingSet
    }

    private fun createInitialWorkingSet() =
            sourceVertex
                    .edges
                    .map { it.endVertexId }
                    .toSet()
}