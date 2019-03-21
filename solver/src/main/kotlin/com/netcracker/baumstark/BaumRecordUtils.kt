package com.netcracker.baumstark

data class BaumGraphRecord(
        val vertices: List<BaumVertexRecord>,
        val sourceVertexId: Int = 0,
        val sinkVertexId: Int = vertices.size - 1
) {
    fun isSourceOrSink(vertexId: Int) = vertexId == sourceVertexId || vertexId == sinkVertexId
}

data class BaumVertexRecord(
        val id: Int,
        val edges: List<BaumEdgeRecord>,
        val height: Long,
        val excess: Long
)

data class BaumEdgeRecord(
        val startVertexId: Int,
        val endVertexId: Int,
        val maxCapacity: Long,
        val flow: Long
) {
    val remainingCapacity = maxCapacity - flow
}

fun BaumGraph.createRecord() =
        BaumGraphRecord(
                vertices = vertices.map(BaumVertex::createRecord),
                sourceVertexId = sourceVertexId,
                sinkVertexId = sinkVertexId
        )

fun BaumVertex.createRecord(): BaumVertexRecord {
    val edgeRecords = edges
            .filter { !it.dummy }
            .map(BaumEdge::createRecord)
    return BaumVertexRecord(
            id = id,
            edges = edgeRecords,
            height = height.get(),
            excess = excess.get()
    )
}

fun BaumEdge.createRecord() =
        BaumEdgeRecord(
                startVertexId,
                endVertexId,
                maxCapacity,
                flow.get()
        )

