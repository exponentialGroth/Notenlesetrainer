package com.exponential_groth.notenlesetrainer.game

import android.graphics.Canvas

interface GameView {
    fun update()
    fun drawGameView(canvas: Canvas?)
}