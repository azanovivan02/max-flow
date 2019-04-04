package com.netcracker.bohong

import com.netcracker.util.assertIsValid
import com.netcracker.util.readGraphFromDimacsFile

//val path = "/home/ivan/Documents/Takmazian/flow/example.max"
//val path = "C:\\Users\\ivaz0317\\Documents\\Takmazian\\MaxFlow\\BoHongMaxFlow\\src\\main\\resources\\example.max"

val path = "/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

fun main(args: Array<String>) {
    val originalGraph = readGraphFromDimacsFile(path)
    val lockFreeGraph = createLockFreeGraph(originalGraph)
    val maxFlow = lockFreeGraph.findMaxFlowParallel()
    lockFreeGraph.assertIsValid()
    println(maxFlow)
//    println(lockFreeGraph.toDotString())
}
