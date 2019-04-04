package com.netcracker.baumstark

import com.netcracker.baumstark.history.actions.ActionRecorderAnalyzer
import com.netcracker.baumstark.history.actions.DefaultActionRecorder
import com.netcracker.baumstark.history.actions.DefaultActionRecorderAnalyzer
import com.netcracker.baumstark.history.workingset.DefaultWorkingSetRecorder
import com.netcracker.baumstark.history.workingset.WorkingSetRecorder
import com.netcracker.util.LogLevel
import com.netcracker.util.Logger
import com.netcracker.util.StandardOutputLogger
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class BaumExecutor(
        val graph: BaumGraph,
        var logger: Logger = StandardOutputLogger(false, LogLevel.DEBUG),
        var threadAmount: Int = 4,
        var enablePreflowValidation: Boolean = false,
        var workingSetRecorder: WorkingSetRecorder = DefaultWorkingSetRecorder(graph)
) {
    fun findMaxFlowValue(
            skipInit: Boolean = false,
            recorderAnalyzer: ActionRecorderAnalyzer = DefaultActionRecorderAnalyzer()
    ): Long {
        if (!skipInit) {
            graph.init()
        }

        val executor = BasicTaskExecutor(threadAmount)
        try {
            val iterationNumber = AtomicInteger(0)
            val solutionFound = AtomicBoolean(false)
            val subsetsList = MutableList<Set<Int>>(threadAmount) { setOf() }

            val startBarrierRunnable = createStartBarrierRunnable(
                    iterationNumber,
                    solutionFound,
                    subsetsList
            )
            val startBarrier = CyclicBarrier(threadAmount, startBarrierRunnable)
            val middleBarrier = CyclicBarrier(threadAmount)

            val longRunnables = createLongRunnables(solutionFound, subsetsList, startBarrier, middleBarrier)

            executor.executeRunnables(longRunnables)

            val actionRecorders = longRunnables.map { it.actionRecorder } + listOf(startBarrierRunnable.actionRecorder)
            recorderAnalyzer.analyze(actionRecorders)
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
    ) = BaumstarkBaseRunnable(runnableIndex,
            solutionFound,
            subsetsList,
            startBarrier,
            middleBarrier,
            graph.vertices,
            graph.sinkVertexId,
            logger,
            actionRecorder = DefaultActionRecorder()
    )

    private fun createStartBarrierRunnable(iterationNumber: AtomicInteger, solutionFound: AtomicBoolean, subsetsList: MutableList<Set<Int>>): StartBarrierRunnable {
        val startBarrierRunnable = StartBarrierRunnable(
                iterationNumber,
                solutionFound,
                subsetsList,
                graph,
                threadAmount,
                logger,
                workingSetRecorder,
                actionRecorder = DefaultActionRecorder()
        )
        return startBarrierRunnable
    }

    override fun toString() = graph.toString()
}