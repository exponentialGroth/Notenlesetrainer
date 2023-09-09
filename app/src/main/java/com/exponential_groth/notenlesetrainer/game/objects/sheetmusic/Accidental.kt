package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic

enum class Accidental {
    FLAT, DOUBLE_FLAT, SHARP, DOUBLE_SHARP, NATURAL;

    val value get() = when(this) {
        FLAT -> -1
        DOUBLE_FLAT -> -2
        SHARP -> 1
        DOUBLE_SHARP -> 2
        else -> 0
    }
}