package com.netcracker.baumstark.history.workingset

import com.netcracker.baumstark.*
import com.netcracker.util.customAssert
import java.io.File

private const val SIZES_CSV_FILE_PATH = "/home/ivan/Documents/Takmazian/max-flow/metrics/working_set_sizes,csv"
private const val GRAPHS_FILE_PATH_PREFIX = "/home/ivan/Documents/Takmazian/max-flow/metrics/working_set_graph"

class DefaultWorkingSetRecorder(
        val graph: BaumGraph,
        val recordOnlySizes: Boolean = false,
        val idGraphAmount: Int = 4,
        val sizesCsvFilePath: String = SIZES_CSV_FILE_PATH,
        val graphFilePathPrefix: String = GRAPHS_FILE_PATH_PREFIX
) : WorkingSetRecorder {

    private val records = mutableListOf<WorkingSetRecord>()
    private val graphRecords = mutableMapOf<Int, BaumGraphRecord>()

    override fun record(iteration: Int, workingSet: Set<Int>, graph: BaumGraph) {
        val activeSet = workingSet
                .map(graph.vertices::get)
                .filter { it.excess.get() > 0 }
                .map { it.id }
                .toSet()
        val nonActiveSet = workingSet - activeSet
        records += WorkingSetRecord(iteration, activeSet, nonActiveSet)

        graphRecords[iteration] = graph.createRecord()
    }

    override fun save() {
        saveSizesCsv()
        saveAllGraphs()
    }

    private fun saveSizesCsv() {
        val file = File(sizesCsvFilePath)
        file.createNewFile()
        val writer = file.printWriter()
        writer.write("iteration,active_set_size,non_active_set_size")
        for ((iteration, activeSet, nonActiveSet) in records) {
            writer.write("\n$iteration,${activeSet.size},${nonActiveSet.size}")
            writer.flush()
        }
    }

    private fun saveAllGraphs() {
        val iterationAmount = records
                .map { it.iteration }
                .toSet()
                .size
        customAssert(iterationAmount == records.size)

        if (iterationAmount > 100) {
            return
        }
        for (record in records) {
            saveGraph(record)
        }
//        val record = records[iterationAmount / 2]
    }

    private fun saveGraph(record: WorkingSetRecord) {
        val (iteration, activeSet, nonActiveSet) = record

        val vertexLabelSupplier = { vertex: BaumVertexRecord ->
            when (vertex.id) {
                in activeSet -> "style=filled fillcolor=\"green\""
                in nonActiveSet -> "style=filled fillcolor=\"yellow\""
                else -> ""
            }
        }
        val graphRecord = graphRecords[iteration]
                ?: throw IllegalStateException("Graph record not found for iteration: $iteration")
        val dotString = graphRecord.toCustomDotString(vertexLabelSupplier)

        val filePath = "${graphFilePathPrefix}_$iteration.dot"
        val file = File(filePath)
        file.createNewFile()
        file.writeText(dotString)
    }

    private data class WorkingSetRecord(
            val iteration: Int,
            val activeSet: Set<Int>,
            val nonActiveSet: Set<Int>
    )
}

