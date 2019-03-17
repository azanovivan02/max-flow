package com.netcracker.bohong

import java.lang.IllegalStateException
import java.util.concurrent.atomic.AtomicLong

class LockFreeEdge(
        val startVertexId: Int,
        val endVertexId: Int,
        val maxCapacity: Long,
        val dummy: Boolean = false
) {

    val remainingCapacity = AtomicLong(maxCapacity)

    override fun toString() = "[$startVertexId, $endVertexId]($flow/$maxCapacity)"
}

val LockFreeEdge.flow
    get() = maxCapacity - remainingCapacity.get()

fun LockFreeEdge.getReverseEdge(vertices: List<LockFreeVertex>): LockFreeEdge {
    val endVertex = vertices[endVertexId]
    val startVertexIndex = endVertex.indices[startVertexId]
            ?: throw IllegalStateException("Unable to find index for vertex")
    return endVertex.edges[startVertexIndex]
}