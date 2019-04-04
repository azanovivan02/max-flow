package com.netcracker

import com.netcracker.OutputMode.*
import com.netcracker.baumstark.BaumstarkSolver
import com.netcracker.util.*
import com.netcracker.util.DrawingMode.*
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    val dimacsGraphString = readWholeFileFromStandardInput()
    val originalGraph = readGraphFromDimacsString(dimacsGraphString)
    val (outputMode, drawingMode, threadAmount) = parseCommandLineArguments(args)

    val solver = BaumstarkSolver(
            logger = StandardOutputLogger(false, LogLevel.DEBUG),
            threadAmount = threadAmount
    )

    val output = solver.solve(originalGraph, outputMode, drawingMode)

    println(output)
}

private fun parseCommandLineArguments(args: Array<String>): Arguments {
    if (args.isEmpty()) {
        return Arguments(MAX_FLOW_VALUE, SIMPLE, 4)
    }
    val threadAmount = args.first().toInt()
    return when (args.size) {
        1 -> Arguments(MAX_FLOW_VALUE, SIMPLE, threadAmount)
        2 -> when (args.last()) {
            "value" -> Arguments(MAX_FLOW_VALUE, SIMPLE, threadAmount)
            "simple" -> Arguments(FLOW_GRAPH, SIMPLE, threadAmount)
            "excess" -> Arguments(FLOW_GRAPH, EXCESS, threadAmount)
            "height" -> Arguments(FLOW_GRAPH, HEIGHT, threadAmount)
            else -> throw IllegalArgumentException("Illegal argument")
        }
        else -> throw IllegalArgumentException("Too many arguments")
    }
}

data class Arguments(
        val outputMode: OutputMode,
        val drawingMode: DrawingMode,
        val threadAmount: Int
)

enum class OutputMode {
    FLOW_GRAPH,
    MAX_FLOW_VALUE,
    ELEMENTARY_ACTIONS
}



