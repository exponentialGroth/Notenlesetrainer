package com.exponential_groth.notenlesetrainer.game

import android.content.Context
import android.graphics.Canvas
import android.view.SurfaceView
import com.exponential_groth.notenlesetrainer.util.OnFinishedListener

abstract class GameView(context: Context): SurfaceView(context) {
    var onFinishedListener: OnFinishedListener? = null
    var playNote: ((Int) -> Unit)? = null

    abstract fun getPoints(): Int
    abstract fun update()
    abstract fun drawGameView(canvas: Canvas?)
}