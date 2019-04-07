package com.netcracker.baumstark

import com.netcracker.baumstark.history.actions.DefaultActionRecorderAnalyzer
import com.netcracker.baumstark.history.workingset.DummyWorkingSetRecorder
import com.netcracker.util.readGraphFromDimacsFile

//val path = "/home/ivan/Documents/Takmazian/flow/example.max"
//val path = "C:\\Users\\ivaz0317\\Documents\\Takmazian\\MaxFlow\\BoHongMaxFlow\\src\\main\\resources\\example.max"

val path = "/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"
//val path = "C:\\Users\\ivaz0317\\Documents\\Takmazian\\MaxFlow\\max-flow\\solver\\src\\main\\resources\\generated-tasks\\generated.max"

fun main(args: Array<String>) {
    val originalGraph = readGraphFromDimacsFile(path)
    val baumGraph = createBaumGraph(originalGraph)
    val executor = BaumExecutor(baumGraph, workingSetRecorder = DummyWorkingSetRecorder())
    try {
        val analyzer = DefaultActionRecorderAnalyzer()
        val maxFlowValue = executor.findMaxFlowValue(recorderAnalyzer = analyzer)
        println("Max flow: $maxFlowValue")
        println("Total actions: ${analyzer.totalActionAmount}")
//        println(baumGraph.createRecord().toDotString(DrawingMode.SIMPLE))
    } catch (e: Exception) {
        println("Exception occured")
        e.printStackTrace(System.out)
    }
}
