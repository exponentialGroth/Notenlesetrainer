package com.exponential_groth.notenlesetrainer.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.exponential_groth.notenlesetrainer.R
import com.exponential_groth.notenlesetrainer.game.objects.Button
import com.exponential_groth.notenlesetrainer.game.objects.keyboard.Keyboard
import com.exponential_groth.notenlesetrainer.game.objects.scoreboard.AVAILABLE_SPACE
import com.exponential_groth.notenlesetrainer.game.objects.scoreboard.Scoreboard
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.GameSheetMusic
import com.exponential_groth.notenlesetrainer.util.KeyColor
import com.exponential_groth.notenlesetrainer.util.OnFinishedListener
import com.exponential_groth.notenlesetrainer.util.contains
import com.exponential_groth.notenlesetrainer.util.toKeyColor

class RankedGameView(context: Context, val dimensions: Pair<Int, Int>, min: Int, max: Int, key: Int, difficulty: Int, FPS: Int): SurfaceView(context), SurfaceHolder.Callback, GameView {
    constructor(context: Context): this(context, Pair(1920, 1080), 33, 61, 0, 1,30)

    var onFinishedListener: OnFinishedListener? = null

    private val thread: GameThread
    private val keyboard = Keyboard(
        min - if (min.toKeyColor() == KeyColor.BLACK) 1 else 0,
        max + if (min.toKeyColor() == KeyColor.BLACK) 1 else 0,
        dimensions
    )
    private val sheet = GameSheetMusic(context, dimensions, min, max, key, difficulty, FPS)
    val scoreboard = Scoreboard(context, dimensions, sheet.staffLeft)

    private val buttons: List<Button>
    private val clickedKeys = mutableListOf<Int>()
    private val clicks = mutableListOf<Pair<Float, Float>>()

    private var frames = 0

    private val countDownPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = Color.BLUE
        textAlign = Paint.Align.CENTER
        textSize = 250f
    }

    init {
        isFocusable = true
        holder.addCallback(this)
        thread = GameThread(this, holder, FPS)

        val endGameBtnDrawable = AppCompatResources.getDrawable(context, R.drawable.baseline_stop_24)!!
        val endGameBtnWidth = (0.75 * AVAILABLE_SPACE * dimensions.second).toInt()
        val endGameBtnMarginTop = 0.125f * AVAILABLE_SPACE * dimensions.second
        val endGameBtnBitmap = endGameBtnDrawable.toBitmap(endGameBtnWidth, endGameBtnWidth)
        val endGameBtn = Button(endGameBtnBitmap, Pair(dimensions.first - endGameBtnMarginTop - endGameBtnWidth, endGameBtnMarginTop), null) {
            thread.running = false
            onFinishedListener?.finish()
        }
        buttons = listOf(endGameBtn)

        setOnTouchListener { v, event ->
            val pos = Pair(event.x, event.y)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (pos in keyboard.rect) {
                        keyboard.onClick(pos)?.let {
                            clickedKeys.add(it)
                        }
                    } else {
                        clicks.add(pos)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    performClick()
                }
            }

            v?.onTouchEvent(event)?: true
        }
    }


    override fun update() {
        if (frames >= FPS * 3 || frames == 0) {
            sheet.update(clickedKeys)
            keyboard.update(sheet.correctClicks, sheet.wrongClicks)
            scoreboard.update(sheet.correctClicks.size, sheet.wrongClicks.size, sheet.missedClicks.size)
            if (scoreboard.lives == 0) {
                thread.running = false
                onFinishedListener?.finish()
            }
        }

        for (btn in buttons) btn.update(clicks)
        clicks.clear()
        clickedKeys.clear()
        frames++
    }

    override fun drawGameView(canvas: Canvas?) {
        if (canvas == null) return
        super.draw(canvas)

        keyboard.draw(canvas)
        sheet.draw(canvas)
        scoreboard.draw(canvas)
        for (btn in buttons) btn.draw(canvas)

        if (frames < FPS * 3) {
            canvas.drawText("${3 - frames / FPS}", 0.5f * dimensions.first, 0.5f * dimensions.second, countDownPaint)
        }
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        thread.running = true
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        thread.running = false
        thread.join()
    }

}


