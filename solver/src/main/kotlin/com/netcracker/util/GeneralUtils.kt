package com.netcracker.util

import java.util.concurrent.atomic.AtomicLong

fun min(a: AtomicLong, b: AtomicLong): Long = kotlin.math.min(a.get(), b.get())

fun <T> List<T>.splitIntoParts(partsAmount: Int): List<List<T>> {
    val partSize = size / partsAmount + 1
    val parts = chunked(partSize)
    if (parts.size != partsAmount) {
        throw IllegalStateException()
    }
    val partSizesString = parts
            .map { it.size }
            .joinToString(", ")
//    println("Split $size elements into parts: [$partSizesString]")
    return parts
}

fun <T> List<T>.splitIntoEvenParts(maxPartsAmount: Int): List<List<T>> {
    if (size <= maxPartsAmount) {
        return listOf(this)
    }
    val partSize = size / maxPartsAmount + 1
    val parts = chunked(partSize)
//    if (parts.size != maxPartsAmount) {
//        throw IllegalStateException()
//    }
//    val partSizesString = parts
//            .map { it.size }
//            .joinToString(", ")
//    println("Split $size elements into parts: [$partSizesString]")
    return parts
}

fun <E> MutableList<E>.removeLast() {
    removeAt(size - 1)
}