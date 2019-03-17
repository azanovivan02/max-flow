package com.netcracker.bohong

import com.netcracker.util.MyGraph
import com.netcracker.util.getVerticesAmount
import org.jgrapht.graph.DefaultWeightedEdge

//todo include case when reverse edge is already provided i.e. then don't create empty twin edge
fun createLockFreeGraph(graph: MyGraph): LockFreeGraph {
    val verticesAmount = graph.getVerticesAmount()
    val edges = createLockFreeEdges(graph)
    val vertices = createLockFreeVertices(verticesAmount, edges)
    return LockFreeGraph(vertices)
}

private fun createLockFreeEdges(graph: MyGraph) = graph
        .edgeSet()
        .flatMap { originalEdge -> createForwardAndReverseLockFreeEdges(graph, originalEdge) }
        .sortedWith(edgeStartEndComparator)

private fun createLockFreeVertices(verticesAmount: Int, edges: List<LockFreeEdge>): MutableList<LockFreeVertex> {
    val adjacencyLists = List<MutableList<LockFreeEdge>>(verticesAmount) { mutableListOf() }
    for (edge in edges) {
        adjacencyLists[edge.startVertexId] += edge
    }
    val vertices = mutableListOf<LockFreeVertex>()
    for (vertexId in 0 until verticesAmount) {
        val list = adjacencyLists[vertexId]
        val vertex = LockFreeVertex(vertexId, list)
        vertices += vertex
    }
    return vertices
}

private fun createForwardAndReverseLockFreeEdges(
        originalGraph: MyGraph,
        originalEdge: DefaultWeightedEdge
): List<LockFreeEdge> {
    val startVertex = originalGraph.getEdgeSource(originalEdge).toInt() - 1
    val endVertex = originalGraph.getEdgeTarget(originalEdge).toInt() - 1
    val capacity = originalGraph.getEdgeWeight(originalEdge).toLong()
    val forwardEdge = LockFreeEdge(startVertex, endVertex, capacity)
    val reverseEdge = LockFreeEdge(endVertex, startVertex, 0, true)
    return listOf(forwardEdge, reverseEdge)
}

private val edgeStartEndComparator = Comparator
        .comparing { edge: LockFreeEdge -> edge.startVertexId }
        .thenComparing { edge -> edge.endVertexId }