package com.exponential_groth.notenlesetrainer.game.objects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.exponential_groth.notenlesetrainer.util.contains

class Button(var bitmap: Bitmap, val pos: Pair<Float, Float>, val paint: Paint?, val onClick: (Button) -> Unit) {

    val rect = Rect(pos.first.toInt(), pos.second.toInt(), pos.first.toInt() + bitmap.width, pos.second.toInt() + bitmap.height)

    fun update(clicks: List<Pair<Float, Float>>) {
        if (clicks.any { it in rect }) {
            onClick(this)
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, pos.first, pos.second, paint)
    }
}