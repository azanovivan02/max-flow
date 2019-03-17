package com.netcracker.util

import java.lang.IllegalStateException

data class Color(
        val red: Int,
        val green: Int,
        val blue: Int
) {
    fun multipliedBy(multiplyer: Double) = Color(
            red = (red * multiplyer).toInt(),
            green = (green * multiplyer).toInt(),
            blue = (blue * multiplyer).toInt()
    )

    override fun toString(): String {
        val colors = listOf(red, green, blue)
        return colors
                .map { it.toString(16) }
                .map { it.addZeroPaddingIfNeeded() }
                .joinToString("")
    }
}

fun String.addZeroPaddingIfNeeded() = when(length) {
    1 -> "0$this"
    2 -> this
    else -> throw IllegalStateException()
}

val MAX_COLOR = Color(
        red = 236,
        green = 208,
        blue = 120
)

fun computeColor(
        currentValue: Long,
        minValue: Long,
        maxValue: Long
): String {
    val diff = maxValue - minValue

    val normalizedCurrentValue = when {
        currentValue < minValue -> minValue
        currentValue > maxValue -> maxValue
        else -> currentValue
    }

    val currentPart = normalizedCurrentValue - minValue
    val percentage = currentPart.toDouble() / diff
    return MAX_COLOR
            .multipliedBy(percentage)
            .toString()
}