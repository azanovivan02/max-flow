package com.netcracker.baumstark

import java.util.Collections.newSetFromMap
import java.util.Collections.unmodifiableMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

class BaumVertex(
        val id: Int,
        val edges: List<BaumEdge>
) {
    val height = AtomicLong(0)
    val excess = AtomicLong(0)
    val addedExcess = AtomicLong(0)
    val isDiscovered = AtomicBoolean(false)
    //todo decide if needs to be thread-safe
    val discoveredVertices: MutableSet<Int> = newSetFromMap(ConcurrentHashMap())

    val indices = initIndices(edges)

    val residualEdges
        get() = edges.filter { it.remainingCapacity > 0 }

    val discoveredVerticesString
        get() = "[${discoveredVertices.joinToString(", ")}]"

    fun init() {
        height.set(0)
        excess.set(0)
        addedExcess.set(0)
        isDiscovered.set(false)
        discoveredVertices.clear()
    }

    fun update() {
        val addedExcessValue = addedExcess.getAndSet(0L)
        excess.getAndAdd(addedExcessValue)
        isDiscovered.set(false)
    }

    fun updateWithHeight(localHeight: Long) {
        height.set(localHeight)
        update()
    }


    fun toDetailedString() = "(id: $id, h: $height, e: $excess, aE: ${addedExcess.get()}, d: ${isDiscovered.get()}, dV: $discoveredVerticesString)"

    override fun toString() = "(id: $id, h: $height, e: $excess)"
}

private fun initIndices(edges: List<BaumEdge>) : Map<Int, Int> {
    val indices = mutableMapOf<Int, Int>()
    for (index in 0 until edges.size) {
        val edge = edges[index]
        indices[edge.endVertexId] = index
    }
    return unmodifiableMap(indices)
}
