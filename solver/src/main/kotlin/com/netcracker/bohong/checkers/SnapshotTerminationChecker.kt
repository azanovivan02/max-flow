package com.netcracker.bohong.checkers

import com.netcracker.bohong.LockFreeGraph
import com.netcracker.util.DrawingMode.EXCESS
import com.netcracker.util.toDotString
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class SnapshotTerminationChecker(
        val graph: LockFreeGraph,
        val solutionComplete: AtomicBoolean,
//        val requiredIterations: Set<Int> = setOf(0, 1, 2)
        val requiredIterations: Set<Int> = setOf()
) : Runnable {

    val iterationCounter = AtomicInteger(0)
    val snapshots : MutableMap<Int, String> = mutableMapOf()

    override fun run() {
        val currentIteration = iterationCounter.get()
        if (mustSaveSnapshot(currentIteration)) {
            snapshots[currentIteration] = graph.toDotString(mode = EXCESS)
        }

        if(!graph.middleVerticesHaveExcess()) {
            solutionComplete.set(true)
        } else {
            iterationCounter.incrementAndGet()
        }
    }

    private fun mustSaveSnapshot(currentIteration: Int) = requiredIterations.isEmpty() || currentIteration in requiredIterations
}

fun processDataFromSnapshotTerminationChecker(
        checker: SnapshotTerminationChecker,
        filePathPrefix: String = "graphSnapshot",
        filePathSuffix: String = ".dot"
) {
    System.err.println("Total iterations: ${checker.iterationCounter.get()}")
    for ((iteration, snapshotString) in checker.snapshots) {
        val filePath = "$filePathPrefix-$iteration$filePathSuffix"
        val file = File(filePath)
        file.delete()
        file.createNewFile()
        file.appendText(snapshotString)
    }
}