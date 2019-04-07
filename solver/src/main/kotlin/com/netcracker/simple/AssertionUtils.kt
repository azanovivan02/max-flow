package com.netcracker.simple

fun customAssert(
        condition: Boolean,
        customMessage: String? = null
) {
    if (!condition) {
        val message =
                if (customMessage != null) {
                    "Assertion failed: $customMessage"
                } else {
                    "Assertion failed"
                }
        throw IllegalStateException(message)
    }
}