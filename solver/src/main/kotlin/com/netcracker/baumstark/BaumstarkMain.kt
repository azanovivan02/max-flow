package com.netcracker.baumstark

import com.netcracker.util.DrawingMode
import com.netcracker.util.readGraphFromDimacsFile

//val path = "/home/ivan/Documents/Takmazian/flow/example.max"
//val path = "C:\\Users\\ivaz0317\\Documents\\Takmazian\\MaxFlow\\BoHongMaxFlow\\src\\main\\resources\\example.max"

val path = "/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

fun main(args: Array<String>) {
    val originalGraph = readGraphFromDimacsFile(path)
    val baumGraph = createBaumGraph(originalGraph)
    try {
        val maxFlowValue = baumGraph.findMaxFlowValue()
        println(maxFlowValue)
        println(baumGraph.toDotString(DrawingMode.SIMPLE))
    } catch (e: Exception) {
        println("Exception occured")
        e.printStackTrace(System.out)
    }
}
