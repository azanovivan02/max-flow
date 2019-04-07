package com.netcracker.simple

val path = "/home/ivan/Documents/Takmazian/max-flow/solver/src/main/resources/generated-tasks/generated.max"

fun main(args: Array<String>) {
    val originalGraph = readGraphFromDimacsFile(path)
    val solver = SimpleSolver()
    val maxFlowValue = solver.solve(originalGraph)
    print(maxFlowValue)
}