package com.netcracker.bohong

import java.util.Collections.unmodifiableMap
import java.util.concurrent.atomic.AtomicLong

const val INITIAL_EXCESS: Long = 0
const val INITIAL_HEIGHT: Long = 0

class LockFreeVertex(val id: Int,
                     val edges: List<LockFreeEdge>) {

    val indices = initIndices(edges)

    val excess = AtomicLong(INITIAL_EXCESS)
    val height = AtomicLong(INITIAL_HEIGHT)

    override fun toString(): String {
        val neighbourIdsString = getNeighbourString()
        val e = excess.get()
        val h = height.get()
        return "(id: $id, e: $e, h: $h): $neighbourIdsString"
    }
}

val LockFreeVertex.residualEdges
    get() = edges.filter { it.remainingCapacity.get() > 0 }

private fun LockFreeVertex.getNeighbourString() = edges
        .map { edge ->
            val endVertexId = edge.endVertexId
            val maxCapacity = edge.maxCapacity
            val flow = edge.flow
            "$endVertexId ($flow/$maxCapacity)"
        }
        .joinToString(", ")

private fun initIndices(edges: List<LockFreeEdge>) : Map<Int, Int> {
    val indices = mutableMapOf<Int, Int>()
    for (index in 0 until edges.size) {
        val edge = edges[index]
        indices[edge.endVertexId] = index
    }
    return unmodifiableMap(indices)
}
