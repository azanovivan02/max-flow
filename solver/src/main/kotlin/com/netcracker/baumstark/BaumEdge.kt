package com.netcracker.baumstark

import java.util.concurrent.atomic.AtomicLong

class BaumEdge(
        val startVertexId: Int,
        val endVertexId: Int,
        val maxCapacity: Long,
        val dummy: Boolean = false
) {
    val flow = AtomicLong(0)

    val remainingCapacity
        get() = maxCapacity - flow.get()

    fun getReverseEdge(vertices: List<BaumVertex>): BaumEdge {
        val endVertex = vertices[endVertexId]
        val startVertexIndex = endVertex.indices[startVertexId]
                ?: throw IllegalStateException("Unable to find index for vertex")
        return endVertex.edges[startVertexIndex]
    }

    override fun toString() = "[$startVertexId, $endVertexId]($flow/$maxCapacity)"
}