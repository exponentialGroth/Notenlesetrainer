package com.exponential_groth.notenlesetrainer.game.objects.keyboard

import android.graphics.Rect
import com.exponential_groth.notenlesetrainer.game.FPS
import com.exponential_groth.notenlesetrainer.util.KeyPaint
import com.exponential_groth.notenlesetrainer.util.toKeyColor

class Key(val keyNum: Int, val rect: Rect) {
    val color = keyNum.toKeyColor()

    var clickCorrectness: Boolean? = null
    var framesSincePaintingStart = 0

    val keyPaint get() = when (clickCorrectness) {
        true -> KeyPaint.GREEN
        false -> KeyPaint.RED
        null -> color.toPaint()
    }

    fun update() {
        if (clickCorrectness != null) framesSincePaintingStart++
        if (framesSincePaintingStart > KEY_PRESS_PAINT_TIME * FPS) {
            framesSincePaintingStart = 0
            clickCorrectness = null
        }
    }
}