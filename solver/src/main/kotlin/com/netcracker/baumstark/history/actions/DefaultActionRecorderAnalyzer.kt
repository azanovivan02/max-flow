package com.netcracker.baumstark.history.actions

import java.util.concurrent.atomic.AtomicLong

class DefaultActionRecorderAnalyzer : ActionRecorderAnalyzer {

    var totalActionAmount: Long = -1

    override fun analyze(recorders: List<ActionRecorder>) {
        totalActionAmount = recorders
                .mapNotNull { it as? DefaultActionRecorder }
                .map { it.totalActionAmount }
                .map(AtomicLong::get)
                .sum()
    }
}