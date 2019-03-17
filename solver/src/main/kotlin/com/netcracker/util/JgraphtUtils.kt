package com.netcracker.util

import org.jgrapht.Graph
import org.jgrapht.graph.DefaultWeightedEdge

typealias MyGraph = Graph<String, DefaultWeightedEdge>

fun <V, E> Graph<V, E>.getVerticesAmount() = vertexSet().size