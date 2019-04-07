package com.netcracker.singlethreaded

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultWeightedEdge

typealias MyGraph = Graph<String, DefaultWeightedEdge>

interface MaxFlowSolver {

    fun findMaxFlow(graph: MyGraph) : MyGraph
}