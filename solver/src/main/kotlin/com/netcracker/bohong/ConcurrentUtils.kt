package com.netcracker.bohong

import java.util.concurrent.Executors

fun executeRunnables(
        threadAmount: Int,
        runnables: List<Runnable>
) {
    val executorService = Executors.newFixedThreadPool(threadAmount + 2)
    val futures = runnables
            .map(executorService::submit)
    for (future in futures) {
        future.get()
    }
    executorService.shutdown()
}