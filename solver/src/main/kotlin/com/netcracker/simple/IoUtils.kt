package com.netcracker.simple

import com.netcracker.util.MyGraph
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.io.*
import org.jgrapht.util.SupplierUtil
import java.io.File
import java.io.StringWriter

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

fun MyGraph.toDotString(): String {
    val vertexIdProvider = ComponentNameProvider<String> { component: String -> component }
    val vertexLabelProvider = ComponentNameProvider<String> { component: String -> component }
    val edgeLabelProvider = ComponentNameProvider<DefaultWeightedEdge> { edge -> "(${getEdgeWeight(edge)})" }
    val exporter = DOTExporter<String, DefaultWeightedEdge>(vertexIdProvider, vertexLabelProvider, edgeLabelProvider)
    val writer = StringWriter()
    exporter.exportGraph(this, writer)
    return writer.toString()
}