package com.netcracker.baumstark

import com.netcracker.util.DrawingMode
import com.netcracker.util.DrawingMode.EXCESS
import com.netcracker.util.DrawingMode.HEIGHT
import com.netcracker.util.computeColor

private const val NEW_LINE = "\n"

private val NO_VERTICES_EXCEPTION = IllegalArgumentException("No vertices in graph")

fun BaumGraph.toDotString(mode: DrawingMode = DrawingMode.SIMPLE) : String {
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
    val edgeString = createEdgeString()
    return "strict digraph G {\n$vertexString\n$edgeString\n}"
}


fun BaumGraph.toCustomDotString(vertexLabelSupplier: (BaumVertex) -> String) : String {
    val vertexString = vertices.joinToString(NEW_LINE) { it.getLineForCustomCase(vertexLabelSupplier) }
    val edgeString = createEdgeString()
    return "strict digraph G {\n$vertexString\n$edgeString\n}"
}

private fun List<BaumVertex>.computeMinMaxHeights(): Pair<Long, Long> {
    val heights = map { it.height.get() }
    val minHeight = heights.min() ?: throw NO_VERTICES_EXCEPTION
    val maxHeight = heights.max() ?: throw NO_VERTICES_EXCEPTION
    return Pair(minHeight, maxHeight)
}

private fun BaumGraph.computeMinMaxExcesses(): Pair<Long, Long> {
    val excesses = vertices
            .filter { !isSourceOrSink(it.id) }
            .map { it.excess.get() }
    val minExcess = excesses.min() ?: throw NO_VERTICES_EXCEPTION
    val maxExcess = excesses.max() ?: throw NO_VERTICES_EXCEPTION
    return Pair(minExcess, maxExcess)
}

private fun BaumVertex.getLineForSimpleCase() = "  $id [ label=\"${getLabel()}\" ];"

private fun BaumVertex.getLineForCustomCase(vertexLabelSupplier: (BaumVertex) -> String): String {
    val customLabel = vertexLabelSupplier(this)
    return "  $id [ label=\"${getLabel()}\" $customLabel ];"
}

private fun BaumVertex.getLineForHeightCase(
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

private fun BaumVertex.getLineForExcessCase(
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

private fun BaumVertex.getLabel() = "$id: ${height.get()}"

private fun BaumEdge.getLabel() = "$flow/$maxCapacity"

private fun BaumGraph.createEdgeString(): String {
    val edgeString = vertices
            .flatMap { it.edges }
            .filter { !it.dummy }
            .joinToString(NEW_LINE) { it.getLineForSimpleCase() }
    return edgeString
}

private fun BaumEdge.getLineForSimpleCase() = "  $startVertexId -> $endVertexId [ label=\"${getLabel()}\" ];"
