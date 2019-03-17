package com.netcracker.util

import com.netcracker.bohong.LockFreeGraph
import com.netcracker.bohong.flow
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.io.*
import org.jgrapht.util.SupplierUtil
import java.io.File
import java.io.StringReader
import java.io.StringWriter

private const val NEW_LINE = "\n"

fun readWholeFileFromStandardInput() : String {
    val lines = mutableListOf<String>()
    var currentLine = readLine()
    while (currentLine != null) {
        lines += currentLine
        currentLine = readLine()
    }
    if(lines.isEmpty()) {
        throw IllegalArgumentException("Input file is empty")
    }
    return lines.joinToString("\n")
}

fun readGraphFromDimacsFile(filePath: String): MyGraph {
    val graph = DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    val vertexProvider = VertexProvider<String> { label, attributes -> label ?: throw IllegalStateException() }
    val defaultWeightedEdgeSupplier = SupplierUtil.createDefaultWeightedEdgeSupplier()
    val edgeProvider = EdgeProvider<String, DefaultWeightedEdge> { from, to, label, attributes -> defaultWeightedEdgeSupplier.get() }
    val importer = DIMACSImporter<String, DefaultWeightedEdge>(vertexProvider, edgeProvider)
    val file = File(filePath)
    importer.importGraph(graph, file)
    return graph
}

fun readGraphFromDimacsString(dimacsGraphString: String): MyGraph {
    val graph = DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    val vertexProvider = VertexProvider<String> { label, attributes -> label ?: throw IllegalStateException() }
    val defaultWeightedEdgeSupplier = SupplierUtil.createDefaultWeightedEdgeSupplier()
    val edgeProvider = EdgeProvider<String, DefaultWeightedEdge> { from, to, label, attributes -> defaultWeightedEdgeSupplier.get() }
    val importer = DIMACSImporter<String, DefaultWeightedEdge>(vertexProvider, edgeProvider)
    val stringReader = StringReader(dimacsGraphString)
    importer.importGraph(graph, stringReader)
    return graph
}

fun LockFreeGraph.toMyGraph() : MyGraph {
    val vertexLabels = vertices
            .map { it.id to it.id.toString() }
            .toMap()
    val graph = DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    for (currentVertex in vertices) {
        val currentVertexLabel = vertexLabels[currentVertex.id]
        graph.addVertex(currentVertexLabel)
    }

    val edgeSupplier = SupplierUtil.createDefaultWeightedEdgeSupplier()
    val nonDummyEdges = vertices
            .flatMap { it.edges }
            .filter { !it.dummy }
    for (edge in nonDummyEdges) {
        val startVertexLabel = vertexLabels[edge.startVertexId]
        val endVertexLabel = vertexLabels[edge.endVertexId]
        val weightedEdge = edgeSupplier.get()
        graph.addEdge(startVertexLabel, endVertexLabel, weightedEdge)
        graph.setEdgeWeight(weightedEdge, edge.flow.toDouble())
    }

    return graph
}

fun MyGraph.toDotString(): String {
    val vertexIdProvider = ComponentNameProvider<String> { component -> component }
    val vertexLabelProvider = ComponentNameProvider<String> { component -> component }
    val edgeLabelProvider = ComponentNameProvider<DefaultWeightedEdge> { edge -> "(${getEdgeWeight(edge)})" }
    val exporter = DOTExporter<String, DefaultWeightedEdge>(vertexIdProvider, vertexLabelProvider, edgeLabelProvider)
    val writer = StringWriter()
    exporter.exportGraph(this, writer)
    return writer.toString()
}