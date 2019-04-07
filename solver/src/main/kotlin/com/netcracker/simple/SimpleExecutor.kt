package com.netcracker.simple

import com.netcracker.util.removeLast
import java.util.*
import kotlin.math.max
import kotlin.math.min

class SimpleExecutor(val verticesAmount: Int) {

    val adjacencyLists: List<MutableList<SimpleEdge>> = List(verticesAmount) { mutableListOf<SimpleEdge>() }
    var B: List<MutableList<Int>> = List(verticesAmount) { mutableListOf<Int>() }

    var excesses: MutableList<Int> = MutableList(verticesAmount) { 0 }
    var distanceLabels: MutableList<Int> = MutableList(verticesAmount) { 0 }
    var count: MutableList<Int> = MutableList(2 * verticesAmount) { 0 }
    var active: MutableList<Boolean> = MutableList(verticesAmount) { false }
    var queue: Queue<Int> = LinkedList<Int>()

    var b: Int = 0

    fun reset() {
        for(list in adjacencyLists) {
            for (edge in list) {
                edge.reset()
            }
        }
        B = List(verticesAmount) { mutableListOf<Int>() }
        excesses = MutableList(verticesAmount) { 0 }
        distanceLabels = MutableList(verticesAmount) { 0 }
        count = MutableList(2 * verticesAmount) { 0 }
        active = MutableList(verticesAmount) { false }
        queue = LinkedList<Int>()
        b = 0
    }

    fun addEdge(
            rawFrom: Int,
            rawTo: Int,
            cap: Int
    ) {
        val from = rawFrom - 1
        val to = rawTo - 1
        adjacencyLists[from] += SimpleEdge(
                from = from,
                to = to,
                index = adjacencyLists[to].size,
                capacity = cap,
                flow = 0
        )
        if (from == to) {
            adjacencyLists[from].last().index++
        }
        adjacencyLists[to] += SimpleEdge(
                from = to,
                to = from,
                index = adjacencyLists[from].size - 1,
                capacity = 0,
                flow = 0
        )
    }

    fun getMaxFlow(
            rawSource: Int,
            rawSink: Int
    ): Int {
        val source = rawSource - 1
        val sink = rawSink - 1

        for (edge in adjacencyLists[source]) {
            excesses[source] += edge.capacity
        }

        count[0] = verticesAmount
        enqueue(source)
        active[sink] = true

        while (b >= 0) {
            if (!B[b].isEmpty()) {
                val v = B[b].last()
                B[b].removeLast()
                active[v] = false
                discharge(v)
            } else {
                b--
            }
        }

        return excesses[sink]
    }

    private fun enqueue(vertex: Int) {
        if (!active[vertex] && excesses[vertex] > 0 && distanceLabels[vertex] < verticesAmount) {
            active[vertex] = true
            B[distanceLabels[vertex]] += vertex
            b = max(b, distanceLabels[vertex])
        }
    }

    private fun push(edge: SimpleEdge) {
        val amount = min(excesses[edge.from], edge.capacity - edge.flow)
        if (distanceLabels[edge.from] == distanceLabels[edge.to] + 1 && amount > 0) {
            edge.flow += amount
            adjacencyLists[edge.to][edge.index].flow -= amount
            excesses[edge.to] += amount
            excesses[edge.from] -= amount
            enqueue(edge.to)
        }
    }

    private fun gap(k: Int) {
        for (vertex in 0 until verticesAmount) {
            if (distanceLabels[vertex] >= k) {
                count[distanceLabels[vertex]]--
                distanceLabels[vertex] = max(distanceLabels[vertex], verticesAmount)
                count[distanceLabels[vertex]]++
                enqueue(vertex)
            }
        }
    }

    private fun relabel(vertex: Int) {
        count[distanceLabels[vertex]]--
        distanceLabels[vertex] = verticesAmount
        for (edge in adjacencyLists[vertex]) {
            if (edge.capacity - edge.flow > 0) {
                distanceLabels[vertex] = min(distanceLabels[vertex], distanceLabels[edge.to] + 1)
            }
        }
        count[distanceLabels[vertex]]++
        enqueue(vertex)
    }

    private fun discharge(vertex: Int) {
        for (edge in adjacencyLists[vertex]) {
            if (excesses[vertex] > 0) {
                push(edge)
            } else {
                break
            }
        }

        if (excesses[vertex] > 0) {
            if (count[distanceLabels    [vertex]] == 1) {
                gap(distanceLabels[vertex])
            } else {
                relabel(vertex)
            }
        }
    }

//    T GetMinCut (int s, int t, vector <int> &cut)
}
