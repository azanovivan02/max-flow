package com.netcracker.simple

import org.jgrapht.Graph

fun <V, E> Graph<V, E>.getVerticesAmount() = vertexSet().size