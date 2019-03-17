package com.netcracker.bohong

import com.netcracker.bohong.checkers.HistoricalTerminationChecker
import com.netcracker.bohong.processors.HistoricalVertexProcessor
import java.io.File

data class VertexHistoryRecord(
        val vertexId: Int,
        val iteration: Int,
        val excess: Long,
        val hegiht: Long
)

fun LockFreeVertex.createHistoryRecord(iteration: Int) =
        VertexHistoryRecord(
                vertexId = id,
                iteration = iteration,
                excess = excess.get(),
                hegiht = height.get()
        )

fun processHistoryRecordsFromProcessors(processors: List<HistoricalVertexProcessor>) {
    val records = processors
            .withIndex()
            .map { (thredId, processor) -> thredId to processor.historyRecords }
            .toMap()
    val iterationThreadTable = records
            .map { (thredId, records) -> thredId to records.aggregatedByIteration() }

    val file = File("records_shuffling.csv")
    file.createNewFile()
    for ((threadId, records) in iterationThreadTable) {
        for ((iteration, recordAmount) in records) {
            file.appendText("$threadId,$iteration,$recordAmount\n")
        }
    }
}

fun processHistoryRecordsFromTerminationChecker(
        checker: HistoricalTerminationChecker,
        filePath: String = "active_vertices_by_global_iteration.csv"
) {
    val amountHistory = checker
            .activeVerticesAmountHistory
            .withIndex()
    val file = File(filePath)
    file.delete()
    file.createNewFile()
    file.appendText("iteration,amount\n")
    for ((iteration, amount) in amountHistory) {
        file.appendText("$iteration,$amount\n")
    }
}

fun List<VertexHistoryRecord>.aggregatedByIteration() =
        groupBy { it.iteration }
                .map { (iteration, records) -> iteration to records.size }
                .sortedBy { it.first }