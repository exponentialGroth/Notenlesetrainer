package com.exponential_groth.notenlesetrainer.util

import android.graphics.Rect
import java.time.LocalDate
import kotlin.math.pow

fun Int.toFrequency() = 2f.pow((this-49f)/12) * 440

fun Int.toKeyColor() = if (this % 12 in listOf(0, 2, 5, 7, 10)) KeyColor.BLACK else KeyColor.WHITE

fun Int.toKeyName() = when (this % 12) {
    4 -> if (this >= 28) "c" else "C"
    else -> "͵,"
} + if (this >= 40) ((this - 40) / 12 + 1).toString()
else if (this <= 15)",".repeat((15 - this) / 12 + 1)
else ""

fun getPossibleTonesForDifficulty1(key: Int): List<Int> {
    val possibleTones = mutableListOf(1, 3, 4, 6, 8, 9, 11)
    if (key < 0) {
        val flatPositions = listOf(3, 8, 1, 6, 11, 4, 9)
        repeat(-key) { flatIndex ->
            possibleTones.indexOf(flatPositions[flatIndex]).takeUnless { it == -1 }?.let {
                possibleTones[it] = possibleTones[it] - 1
            }
        }
    } else if (key > 0) {
        val sharpPositions = listOf(9, 4, 11, 6, 1, 8, 3)
        repeat(key) { sharpIndex ->
            possibleTones.indexOf(sharpPositions[sharpIndex]).takeUnless { it == -1 }?.let {
                possibleTones[it] = (possibleTones[it] + 1) % 12
            }
        }
    }
    return possibleTones
}

fun getNumOfWhiteKeys(leftKey: Int, rightKey: Int): Int =
    (leftKey..rightKey).count { it.toKeyColor() == KeyColor.WHITE }


fun Int.plusWhiteKeys(wKeys: Int): Int {
    var keyNum = this
    repeat(wKeys) {
        keyNum++
        if (keyNum.toKeyColor() == KeyColor.BLACK) keyNum++
    }
    return keyNum
}
fun Int.minusWhiteKeys(wKeys: Int): Int {
    var keyNum = this
    repeat(wKeys) {
        keyNum--
        if (keyNum.toKeyColor() == KeyColor.BLACK) keyNum--
    }
    return keyNum
}
fun Int.toPositionInBlackKeyGroup(): Position {
    return when(this % 12) {
        0 -> Position.CENTER
        2, 7 -> Position.RIGHT
        5, 10 -> Position.LEFT
        else -> throw Exception("not a black key")
    }
}

/**
 * returns the key number of the key which is the this-th key of its color
 * */
fun Int.toKeyNum(leftKey: Int, rightKey: Int, keyColor: KeyColor): Int {
    var n = 0
    for (k in leftKey..rightKey) {
        if (k.toKeyColor() == keyColor) n++
        if (n == this) {
            return k
        }
    }
    throw Exception("such key does not exist")
}

operator fun Rect.contains(pos: Pair<Float, Float>): Boolean =
    pos.first.toInt() in left..right &&
            pos.second.toInt() in top..bottom


fun LocalDate.toInt() = year * 10000 + month.value * 100 + dayOfMonth

fun Int.toLocalDate(): LocalDate {
    val dayOfMonth = this % 100
    val monthValue = ((this - dayOfMonth) % 10000) / 100
    return LocalDate.of((this - monthValue * 100 - dayOfMonth) / 10000, monthValue, dayOfMonth)
}
