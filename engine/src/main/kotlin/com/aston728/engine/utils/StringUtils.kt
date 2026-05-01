package com.aston728.engine.utils

fun centerString(string: String, fillChar: Char, size: Int): String {
    if (string.length >= size) return string

    val fullPadding: Int = size - string.length
    val leftPadding: Int = fullPadding / 2
    val rightPadding: Int = fullPadding - leftPadding
    return fillChar.toString().repeat(leftPadding) + string + fillChar.toString().repeat(rightPadding)
}
