package com.exponential_groth.notenlesetrainer.util

enum class KeyColor {
    BLACK, WHITE;

    fun toPaint() = if (this == BLACK) KeyPaint.BLACK else KeyPaint.WHITE
}

enum class KeyPaint {
    BLACK, WHITE, GREEN, RED
}

enum class Position {
    LEFT, CENTER, RIGHT
}