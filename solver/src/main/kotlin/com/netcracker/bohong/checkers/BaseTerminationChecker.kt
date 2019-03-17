package com.netcracker.bohong.checkers

import com.netcracker.bohong.LockFreeGraph
import java.util.concurrent.atomic.AtomicBoolean

class BaseTerminationChecker(
        val graph: LockFreeGraph,
        val solutionComplete: AtomicBoolean
) : Runnable {

    override fun run() {
        if(!graph.middleVerticesHaveExcess()) {
            solutionComplete.set(true)
        } else {
//            println("Condition is not fulfilled")
        }
    }
}