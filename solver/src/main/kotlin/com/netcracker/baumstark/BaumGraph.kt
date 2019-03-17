package com.netcracker.baumstark

import com.netcracker.baumstark.history.DefaultWorkingSetRecorder
import com.netcracker.baumstark.history.WorkingSetRecorder
import com.netcracker.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


const val WORKING_SET_FILE_PATH = "/home/ivan/Documents/Takmazian/max-flow/solver/working_sets.csv"

class BaumGraph(
        val vertices: List<BaumVertex>,
        val sourceVertexId: Int = 0,
        val sinkVertexId: Int = vertices.size - 1,
        var logger: Logger = StandardOutputLogger(false, LogLevel.DEBUG),
        var threadAmount: Int = 4,
        var enablePreflowValidation: Boolean = false,
        var workingSetRecorder: WorkingSetRecorder = DefaultWorkingSetRecorder(WORKING_SET_FILE_PATH)
) {
    val edges = vertices.flatMap { it.edges }
    val sourceVertexHeight = vertices.size.toLong()

    val sourceVertex = vertices[sourceVertexId]
    val sinkVertex = vertices[sinkVertexId]

    fun isSourceOrSink(currentVertexId: Int) = currentVertexId == sourceVertexId || currentVertexId == sinkVertexId

    fun init() {
        initVertices()
        initEdges()
        saturateEdgesFromSource()
    }

    fun findMaxFlowValue(skipInit: Boolean = false): Long {
        if (!skipInit) {
            init()
        }

        val executor = BasicTaskExecutor(threadAmount)
        try {
            val iterationNumber = AtomicInteger(0)
            val solutionFound = AtomicBoolean(false)
            val subsetsList = MutableList<Set<Int>>(threadAmount) { setOf() }

            val startBarrier = createStartBarrier(iterationNumber, solutionFound, subsetsList)
            val middleBarrier = CyclicBarrier(threadAmount)

            val longRunnables = createLongRunnables(solutionFound, subsetsList, startBarrier, middleBarrier)

            executor.executeRunnables(longRunnables)
        } finally {
            executor.shutdown()
            workingSetRecorder.save()
        }

        return sinkVertex
                .excess
                .get()
    }

    private fun createLongRunnables(
            solutionFound: AtomicBoolean,
            subsetsList: MutableList<Set<Int>>,
            startBarrier: CyclicBarrier,
            middleBarrier: CyclicBarrier
    ) =
            (0 until threadAmount).map { runnableIndex ->
                createLongRunnable(
                        runnableIndex,
                        solutionFound,
                        subsetsList,
                        startBarrier,
                        middleBarrier
                )
            }

    private fun createLongRunnable(
            runnableIndex: Int,
            solutionFound: AtomicBoolean,
            subsetsList: MutableList<Set<Int>>,
            startBarrier: CyclicBarrier,
            middleBarrier: CyclicBarrier
    ) = LongRunnable(runnableIndex,
            solutionFound,
            subsetsList,
            startBarrier,
            middleBarrier,
            vertices,
            sinkVertexId,
            logger
    )

    private fun createStartBarrier(
            iterationNumber: AtomicInteger,
            solutionFound: AtomicBoolean,
            subsetsList: MutableList<Set<Int>>
    ): CyclicBarrier {
        val startBarrierRunnable = StartBarrierRunnable(
                iterationNumber,
                solutionFound,
                subsetsList,
                threadAmount,
                vertices,
                sourceVertexId,
                sinkVertexId,
                logger,
                workingSetRecorder
        )
        return CyclicBarrier(threadAmount, startBarrierRunnable)
    }

    private fun createInitialWorkingSet() =
            sourceVertex
                    .edges
                    .map { it.endVertexId }
                    .toSet()

    private fun updateSinkVertex() {
        val sinkAddedExcess = sinkVertex.addedExcess.get()
        sinkVertex.excess.set(sinkAddedExcess)
    }

    private fun updateVerticesInNewWorkingSet(workingSet: Set<Int>) {
        workingSet
                .map(vertices::get)
                .forEach(BaumVertex::update)
    }

    private fun createNewWorkingSet(oldWorkingSet: Set<Int>): Set<Int> {
        val discoveredVertices = oldWorkingSet
                .map(vertices::get)
                .flatMap(BaumVertex::discoveredVertices)
        logger.debug { "   discovered vertices: [${discoveredVertices.joinToString(", ")}]" }

        val newWorkingSet = discoveredVertices
                .map(vertices::get)
                .filter { it.height.get() < sourceVertexHeight }
                .map { it.id }
                .toSet()
        return newWorkingSet
    }

    private fun createDischargeUpdateRunnables(subsets: List<List<Int>>): List<DischargeUpdateRunnable> {
        val runnables = subsets
                .map(this::createDischargeUpdateRunnable)
                .toList()
        val latch = CountDownLatch(runnables.size)
        runnables.forEach { it.latch = latch }
        return runnables
    }

    private fun createDischargeUpdateRunnable(workingSet: Iterable<Int>) =
            DischargeUpdateRunnable(
                    workingSet,
                    vertices,
                    sinkVertexId,
                    latch = null,
                    logger = logger
            )

    private fun initVertices() {
        for (vertex in vertices) {
            vertex.init()
        }
        val sourceVertex = vertices[sourceVertexId]
        sourceVertex.height.set(sourceVertexHeight)
    }

    private fun initEdges() {
        for (edge in edges) {
            edge.flow.set(0)
        }
    }

    private fun saturateEdgesFromSource() {
        val sourceVertex = vertices[sourceVertexId]
        for (edge in sourceVertex.edges) {
            val delta = edge.maxCapacity

            edge.flow.set(delta)

            val reverseEdge = edge.getReverseEdge(vertices)
            reverseEdge.flow.getAndAdd(-delta)

            val endVertex = vertices[edge.endVertexId]
            endVertex.excess.set(delta)
        }
    }

    private fun validatePreflowIfNeeded() {
        if (!enablePreflowValidation) {
            return
        }
        val violationMessages = findPreflowViolations()
        if (!violationMessages.isEmpty()) {
            logger.error { violationMessages.joinToString("\n") }
            throw IllegalStateException("Incorrect preflow")
        }
    }

    private fun findPreflowViolations(): List<String> {
        val violationMessages = mutableListOf<String>()
        for (vertex in vertices) {
            val totalFlow = vertex
                    .edges
                    .map { it.flow.get() }
                    .sum()
            val excess = vertex.excess.get()
            if (!isSourceOrSink(vertex.id) && (-totalFlow) != excess) {
                violationMessages += "Vertex ${vertex.id} has preflow mismatch. Total flow: $totalFlow, excess: $excess"
            }
        }
        return violationMessages
    }

    override fun toString() = vertices.joinToString("\n")
}