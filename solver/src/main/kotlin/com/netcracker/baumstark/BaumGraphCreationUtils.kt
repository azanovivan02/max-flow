package com.netcracker.baumstark

import com.netcracker.util.MyGraph
import com.netcracker.util.getVerticesAmount
import org.jgrapht.graph.DefaultWeightedEdge

fun createBaumGraph(graph: MyGraph): BaumGraph {
    val verticesAmount = graph.getVerticesAmount()
    val edges = createBaumEdges(graph)
    val vertices = createBaumVertices(verticesAmount, edges)
    return BaumGraph(vertices)
}

private fun createBaumEdges(graph: MyGraph) = graph
        .edgeSet()
        .flatMap { originalEdge -> createForwardAndReverseBaumEdges(graph, originalEdge) }
        .sortedWith(edgeStartEndComparator)

private fun createBaumVertices(verticesAmount: Int, edges: List<BaumEdge>): MutableList<BaumVertex> {
    val adjacencyLists = List<MutableList<BaumEdge>>(verticesAmount) { mutableListOf() }
    for (edge in edges) {
        adjacencyLists[edge.startVertexId] += edge
    }
    val vertices = mutableListOf<BaumVertex>()
    for (vertexId in 0 until verticesAmount) {
        val list = adjacencyLists[vertexId]
        val vertex = BaumVertex(vertexId, list)
        vertices += vertex
    }
    return vertices
}

private fun createForwardAndReverseBaumEdges(
        originalGraph: MyGraph,
        originalEdge: DefaultWeightedEdge
): List<BaumEdge> {
    val startVertex = originalGraph.getEdgeSource(originalEdge).toInt() - 1
    val endVertex = originalGraph.getEdgeTarget(originalEdge).toInt() - 1
    val capacity = originalGraph.getEdgeWeight(originalEdge).toLong()
    val forwardEdge = BaumEdge(startVertex, endVertex, capacity)
    val reverseEdge = BaumEdge(endVertex, startVertex, 0, true)
    return listOf(forwardEdge, reverseEdge)
}

private val edgeStartEndComparator = Comparator
        .comparing { edge: BaumEdge -> edge.startVertexId }
        .thenComparing { edge -> edge.endVertexId }