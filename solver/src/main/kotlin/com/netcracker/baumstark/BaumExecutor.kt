package com.netcracker.baumstark

import com.netcracker.baumstark.history.DefaultWorkingSetRecorder
import com.netcracker.baumstark.history.WorkingSetRecorder
import com.netcracker.util.LogLevel
import com.netcracker.util.Logger
import com.netcracker.util.StandardOutputLogger
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class BaumExecutor(
        val graph: BaumGraph,
        var logger: Logger = StandardOutputLogger(false, LogLevel.DEBUG),
        var threadAmount: Int = 4,
        var enablePreflowValidation: Boolean = false,
        var workingSetRecorder: WorkingSetRecorder = DefaultWorkingSetRecorder(graph)
) {
    fun findMaxFlowValue(skipInit: Boolean = false): Long {
        if (!skipInit) {
            graph.init()
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

        return graph
                .sinkVertex
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
            graph.vertices,
            graph.sinkVertexId,
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
                graph,
                threadAmount,
                logger,
                workingSetRecorder
        )
        return CyclicBarrier(threadAmount, startBarrierRunnable)
    }

    override fun toString() = graph.toString()
}