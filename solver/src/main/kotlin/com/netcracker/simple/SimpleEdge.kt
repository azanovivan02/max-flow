package com.netcracker.simple

data class SimpleEdge(
        val from: Int,
        val to: Int,
        var index: Int,
        val capacity: Int,
        var flow: Int) {

    fun reset() {
        flow = 0
    }
}