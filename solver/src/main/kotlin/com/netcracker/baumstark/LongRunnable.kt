package com.netcracker.baumstark

import com.netcracker.util.Logger
import com.netcracker.util.customAssert
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

class LongRunnable(
        val runnableIndex: Int,
        val solutionFound: AtomicBoolean,
        val subsetsList: MutableList<Set<Int>>,
        val startBarrier: CyclicBarrier,
        val middleBarrier: CyclicBarrier,
        val vertices: List<BaumVertex>,
        val sinkVertexId: Int,
        var logger: Logger
) : Runnable {

    private val sourceVertexHeight = vertices.size.toLong()
    private val localHeights = MutableList<Long?>(vertices.size) { null }

    override fun run() {
        while (true) {
            startBarrier.await()
            if (solutionFound.get()) {
                break
            }
            val workingSet = subsetsList[runnableIndex]
            updateVerticesInNewWorkingSet(workingSet)
            workingSet.forEach(this::discharge)
            middleBarrier.await()
            workingSet.forEach(this::update)
        }
    }

    fun discharge(currentVertexId: Int) {
        val currentVertex = vertices[currentVertexId]
        currentVertex.discoveredVertices.clear()
        var localHeight = currentVertex.height.get()
        var localExcess = currentVertex.excess.get()
        while (localExcess > 0) {
            var newHeight = sourceVertexHeight
            var skipped = false
            for (edge in currentVertex.residualEdges) {
                customAssert(localExcess >= 0) { "Vertex: $currentVertexId, local excess value is negative: $localExcess" }

                if (localExcess == 0L) {
                    break
                }

                val endVertex = vertices[edge.endVertexId]
                val endVertexHeight = endVertex.height.get()
                val currentVertexHeight = localHeight

                //todo clarify moment with edge admissability
                val edgeIsAdmissable = (currentVertexHeight == endVertexHeight + 1)

                if (endVertex.excess.get() > 0) {
                    val win: Boolean = (currentVertexHeight == endVertexHeight + 1)
                            || (currentVertexHeight < endVertexHeight - 1)
                            || (currentVertexHeight == endVertexHeight && currentVertexId < endVertex.id)
                    if (edgeIsAdmissable && !win) {
                        skipped = true
                        continue
                    }
                }

                if (edgeIsAdmissable && edge.remainingCapacity > 0) {
                    val delta = min(edge.remainingCapacity, localExcess)
                    logger.debug { "- Pushing $delta from ${edge.startVertexId} to ${edge.endVertexId}" }
                    edge.flow.getAndAdd(delta)

                    val reverseEdge = edge.getReverseEdge(vertices)
                    reverseEdge.flow.getAndAdd(-delta)

                    localExcess -= delta

                    endVertex.addedExcess.getAndAdd(delta)

                    if (endVertex.id != sinkVertexId && testAndSet(endVertex.isDiscovered)) {
                        currentVertex.discoveredVertices += endVertex.id
                    }
                }

                if (edge.remainingCapacity > 0 && endVertexHeight >= currentVertexHeight) {
                    newHeight = min(newHeight, endVertexHeight + 1)
                }
            }

            if (localExcess == 0L || skipped) {
                break
            }

            localHeight = newHeight
            if (newHeight == sourceVertexHeight) {
                break
            }
        }

        val addedExcess = localExcess - currentVertex.excess.get()
        currentVertex.addedExcess.addAndGet(addedExcess)

        if (currentVertex.excess.get() > 0L && testAndSet(currentVertex.isDiscovered)) {
            currentVertex.discoveredVertices += currentVertex.id
        }

        localHeights[currentVertexId] = localHeight
    }

    private fun update(currentVertexId: Int) {
        val currentVertex = vertices[currentVertexId]
        val localHeight = localHeights[currentVertexId] ?: throw IllegalStateException()
        currentVertex.updateWithHeight(localHeight)
        logger.debug(" - ${currentVertex.toDetailedString()}")
    }

    private fun updateVerticesInNewWorkingSet(workingSet: Set<Int>) {
        workingSet
                .map(vertices::get)
                .forEach(BaumVertex::update)
    }

    private fun testAndSet(condition: AtomicBoolean) =
            if (condition.get()) {
                false
            } else {
                condition.set(true)
                true
            }
}