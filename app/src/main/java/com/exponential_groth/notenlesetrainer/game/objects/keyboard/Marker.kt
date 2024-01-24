package com.exponential_groth.notenlesetrainer.game.objects.keyboard

import android.graphics.Canvas
import android.graphics.Paint
import com.exponential_groth.notenlesetrainer.util.toKeyName

class Marker(val x: Float, val y: Float, size: Float, keyNum: Int) {
    private val name = keyNum.toKeyName()
    private val paint = Paint().apply {
        textSize = size
    }

    fun draw(canvas: Canvas) {
        canvas.drawText(name, x, y, paint)
    }
}