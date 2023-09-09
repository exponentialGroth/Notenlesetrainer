package com.exponential_groth.notenlesetrainer.game

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(private val gameView: GameView, private val surfaceHolder: SurfaceHolder, private val FPS: Int): Thread() {

    var running = false

    override fun run() {
        var startTime: Long
        val timePerFrame = (1_000_000_000f / FPS).toLong()

        while (running) {
            startTime = System.nanoTime()
            var canvas: Canvas? = null

            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.update()
                    gameView.drawGameView(canvas)
                }
            } finally {
                canvas?.let {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }

            val timeToSleep = timePerFrame - (System.nanoTime() - startTime)
            if (timeToSleep > 0) sleep(timeToSleep / 1_000_000, (timeToSleep % 1_000_000).toInt())
        }
    }
}