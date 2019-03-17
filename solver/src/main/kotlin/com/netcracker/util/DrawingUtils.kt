package com.netcracker.util

import com.netcracker.bohong.LockFreeEdge
import com.netcracker.bohong.LockFreeGraph
import com.netcracker.bohong.LockFreeVertex
import com.netcracker.bohong.flow
import com.netcracker.util.DrawingMode.EXCESS
import com.netcracker.util.DrawingMode.HEIGHT
import java.lang.IllegalArgumentException

private const val NEW_LINE = "\n"

private val NO_VERTICES_EXCEPTION = IllegalArgumentException("No vertices in graph")

enum class DrawingMode { SIMPLE, HEIGHT, EXCESS }

fun LockFreeGraph.toDotString(mode: DrawingMode = EXCESS) : String {
    val vertexString = when(mode) {
        HEIGHT -> {
            val (minHeight, maxHeight) = vertices.computeMinMaxHeights()
            vertices.joinToString(NEW_LINE) { it.getLineForHeightCase(minHeight, maxHeight) }
        }
        EXCESS -> {
            val (minExcess, maxExcess) = computeMinMaxExcesses()
            vertices.joinToString(NEW_LINE) { it.getLineForExcessCase(minExcess, maxExcess) }
        }
        else -> vertices.joinToString(NEW_LINE) { it.getLineForSimpleCase() }
    }
    val edgeString = vertices
            .flatMap { it.edges }
            .filter { !it.dummy }
            .joinToString(NEW_LINE) { it.getLineForSimpleCase(mode) }
    return "strict digraph G {\n$vertexString\n$edgeString\n}"
}

private fun List<LockFreeVertex>.computeMinMaxHeights(): Pair<Long, Long> {
    val heights = map { it.height.get() }
    val minHeight = heights.min() ?: throw NO_VERTICES_EXCEPTION
    val maxHeight = heights.max() ?: throw NO_VERTICES_EXCEPTION
    return Pair(minHeight, maxHeight)
}

private fun LockFreeGraph.computeMinMaxExcesses(): Pair<Long, Long> {
    val excesses = vertices
            .filter { !isSourceOrSink(it.id) }
            .map { it.excess.get() }
    val minExcess = excesses.min() ?: throw NO_VERTICES_EXCEPTION
    val maxExcess = excesses.max() ?: throw NO_VERTICES_EXCEPTION
    return Pair(minExcess, maxExcess)
}

private fun LockFreeVertex.getLineForSimpleCase() = "  $id [ label=\"${getLabel()}\" ];"

private fun LockFreeVertex.getLineForHeightCase(
        minHeight: Long,
        maxHeight: Long
): String {
    val color = computeColor(
            currentValue = height.get(),
            minValue = minHeight,
            maxValue = maxHeight
    )
    return "  $id [ label=\"${getLabel()}\" style=filled fillcolor=\"#$color\"];"
}

private fun LockFreeVertex.getLineForExcessCase(
        minExcess: Long,
        maxExcess: Long
): String {
    val color = computeColor(
            currentValue = excess.get(),
            minValue = minExcess,
            maxValue = maxExcess
    )
    val label = "$id: ${excess.get()}"
    return "  $id [ label=\"$label\" style=filled fillcolor=\"#$color\"];"
}

private fun LockFreeVertex.getLabel() = "$id: ${height.get()}"

private fun LockFreeEdge.getLineForSimpleCase(mode: DrawingMode) = "  $startVertexId -> $endVertexId [ label=\"${getLabel()}\" ];"

private fun LockFreeEdge.getLabel() = "$flow/$maxCapacity"