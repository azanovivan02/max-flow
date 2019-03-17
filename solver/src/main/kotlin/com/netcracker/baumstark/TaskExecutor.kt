package com.netcracker.baumstark

import java.util.concurrent.Executors

class BasicTaskExecutor(val threadAmount: Int) {

    val executionService = Executors.newFixedThreadPool(threadAmount)

    fun executeRunnables(runnables: Iterable<Runnable>) {
        val futures = runnables.map(executionService::submit)
        for (future in futures) {
            future.get()
        }
    }

    fun shutdown() {
        executionService.shutdown()
    }
}