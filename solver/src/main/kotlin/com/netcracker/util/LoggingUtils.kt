package com.netcracker.util

import com.netcracker.util.LogLevel.*
import com.netcracker.util.Logger.ThreadIdOption.*
import java.lang.UnsupportedOperationException

enum class LogLevel { ERROR, INFO, WARN, DEBUG, TRACE }

interface Logger {

    fun error(message: String)

    fun error(produceMessage: () -> String)

    fun info(message: String)

    fun info(produceMessage: () -> String)

    fun debug(message: String)

    fun debug(produceMessage: () -> String)

    enum class ThreadIdOption {
        NOT_SHOWING,
        ORIGINAL,
        REFINED
    }
}

class StandardOutputLogger(
        var enabled: Boolean = true,
        var loggerLevel: LogLevel = TRACE,
        var threadIdOption: Logger.ThreadIdOption = ORIGINAL
) : Logger {

    override fun error(message: String) = error { message }

    override fun error(produceMessage: () -> String) = log(ERROR, produceMessage)

    override fun info(message: String) = info { message }

    override fun info(produceMessage: () -> String) = log(INFO, produceMessage)

    override fun debug(message: String) = debug { message }

    override fun debug(produceMessage: () -> String) = log(DEBUG, produceMessage)

    private fun log(
            messageLevel: LogLevel,
            produceMessage: () -> String
    ) {
        if(enabled && messageLevel <= loggerLevel) {
            val message = produceMessage()
            val originalThreadId = Thread.currentThread().id
            val output = when(threadIdOption) {
                NOT_SHOWING -> message
                ORIGINAL -> "[$originalThreadId], $message"
                REFINED -> throw UnsupportedOperationException()
            }
            println(output)
        }
    }
}

