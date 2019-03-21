package com.netcracker.baumstark

class BaumGraph(
        val vertices: List<BaumVertex>,
        val sourceVertexId: Int = 0,
        val sinkVertexId: Int = vertices.size - 1) {

    val edges = vertices.flatMap { it.edges }
    val sourceVertexHeight = vertices.size.toLong()

    val sourceVertex = vertices[sourceVertexId]
    val sinkVertex = vertices[sinkVertexId]

    fun init() {
        initVertices()
        initEdges()
        saturateEdgesFromSource()
    }

    private fun initVertices() {
        for (vertex in vertices) {
            vertex.init()
        }
        val sourceVertex = vertices[sourceVertexId]
        sourceVertex.height.set(sourceVertexHeight)
    }

    private fun initEdges() {
        for (edge in edges) {
            edge.flow.set(0)
        }
    }

    private fun saturateEdgesFromSource() {
        val sourceVertex = vertices[sourceVertexId]
        for (edge in sourceVertex.edges) {
            val delta = edge.maxCapacity

            edge.flow.set(delta)

            val reverseEdge = edge.getReverseEdge(vertices)
            reverseEdge.flow.getAndAdd(-delta)

            val endVertex = vertices[edge.endVertexId]
            endVertex.excess.set(delta)
        }
    }

    override fun toString() = vertices.joinToString("\n")
}