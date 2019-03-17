package com.netcracker.baumstark.history

import java.io.File

class DefaultWorkingSetRecorder(val filePath: String) : WorkingSetRecorder {

    val records = mutableListOf<WorkingSetRecord>()

    override fun record(iteration: Int, workingSet: Collection<Int>) {
        records += WorkingSetRecord(iteration, workingSet.toSet())
    }

    override fun save() {
        val file = File(filePath)
        file.createNewFile()
        val writer = file.printWriter()
        writer.write("iteration,working_set_size")
        for ((iteration, workingSet) in records) {
            writer.write("\n$iteration,${workingSet.size}")
            writer.flush()
        }
    }
}