package com.netcracker.singlethreaded

val solver = SingleThreadPushRelabel()

fun main(args: Array<String>) {
    val originalGraph = readGraphFromDimacsFile(path)
    val maxFlowGraph = solver.findMaxFlow(originalGraph)
    print(originalGraph.toDotString())
}