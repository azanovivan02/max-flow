package com.netcracker.singlethreaded

import org.jgrapht.Graph

fun <V, E> Graph<V, E>.getVerticesAmount() = vertexSet().size