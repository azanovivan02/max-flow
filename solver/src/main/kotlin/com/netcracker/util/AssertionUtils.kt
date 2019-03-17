package com.netcracker.util

fun customAssert(
        condition: Boolean,
        customMessage: String? = null
) = customAssert(condition) { customMessage }

fun customAssert(
        condition: Boolean,
        produceCustomMessage: () -> String?
) {
    if (!condition) {
        val customMessage = produceCustomMessage()
        val message =
                if (customMessage != null) {
                    "Assertion failed: $customMessage"
                } else {
                    "Assertion failed"
                }
        throw IllegalStateException(message)
    }
}