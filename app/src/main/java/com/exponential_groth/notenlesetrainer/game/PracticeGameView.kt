package com.exponential_groth.notenlesetrainer.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.exponential_groth.notenlesetrainer.R
import com.exponential_groth.notenlesetrainer.game.objects.Button
import com.exponential_groth.notenlesetrainer.game.objects.keyboard.Keyboard
import com.exponential_groth.notenlesetrainer.game.objects.keyboard.whitePaint
import com.exponential_groth.notenlesetrainer.game.objects.scoreboard.AVAILABLE_SPACE
import com.exponential_groth.notenlesetrainer.game.objects.scoreboard.PracticeScoreboard
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.PracticeSheetMusic
import com.exponential_groth.notenlesetrainer.util.KeyColor
import com.exponential_groth.notenlesetrainer.util.contains
import com.exponential_groth.notenlesetrainer.util.toKeyColor

class PracticeGameView(context: Context, val dimensions: Pair<Int, Int>, min: Int, max: Int, key: Int, difficulty: Int, FPS: Int): SurfaceHolder.Callback, GameView(context) {
    constructor(context: Context): this(context, Pair(1920, 1080), 33, 61, 0, 1,30)


    private val thread: GameThread
    private val keyboard = Keyboard(
        min - if (min.toKeyColor() == KeyColor.BLACK) 1 else 0,
        max + if (min.toKeyColor() == KeyColor.BLACK) 1 else 0,
        dimensions
    )
    private val sheet = PracticeSheetMusic(context, dimensions, min, max, key, difficulty, FPS)
    val scoreboard = PracticeScoreboard(dimensions, sheet.staffLeft)

    private val buttons: List<Button>
    private val clickedKeys = mutableListOf<Int>()
    private val clicks = mutableListOf<Pair<Float, Float>>()
    private var frames = 0
    private var paused = false

    private var countDownPaint = Paint().apply {
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

        val decreaseSpeedBtnDrawable = AppCompatResources.getDrawable(context, R.drawable.baseline_remove_circle_24)!!
        val increaseSpeedBtnDrawable = AppCompatResources.getDrawable(context, R.drawable.baseline_add_circle_24)!!
        val pauseGameDrawable = AppCompatResources.getDrawable(context, R.drawable.baseline_pause_24)!!
        val resumeGameDrawable = AppCompatResources.getDrawable(context, R.drawable.baseline_play_arrow_24)!!
        val endGameBtnDrawable = AppCompatResources.getDrawable(context, R.drawable.baseline_stop_24)!!


        val speedBtnWidth = (0.6 * keyboard.rect.left).toInt()
        val speedBtnMarginHorizontal = 0.5f * (keyboard.rect.left - speedBtnWidth)
        val speedBtnY = 0.5f * (keyboard.rect.top + keyboard.rect.bottom) - 0.5f * speedBtnWidth
        val endGameBtnWidth = (0.75 * AVAILABLE_SPACE * dimensions.second).toInt()
        val endGameBtnMarginTop = 0.125f * AVAILABLE_SPACE * dimensions.second

        val decreaseSpeedBtnBitmap = decreaseSpeedBtnDrawable.toBitmap(speedBtnWidth, speedBtnWidth)
        val increaseSpeedBtnBitmap = increaseSpeedBtnDrawable.toBitmap(speedBtnWidth, speedBtnWidth)
        val pauseGameBtnBitmap = pauseGameDrawable.toBitmap(endGameBtnWidth, endGameBtnWidth)
        val resumeGameBtnBitmap = resumeGameDrawable.toBitmap(endGameBtnWidth, endGameBtnWidth)
        val endGameBtnBitmap = endGameBtnDrawable.toBitmap(endGameBtnWidth, endGameBtnWidth)

        val decreaseSpeedBtn = Button(decreaseSpeedBtnBitmap, Pair(speedBtnMarginHorizontal, speedBtnY), whitePaint) {
            sheet.changeSpeed(-0.02f)
        }
        val increaseSpeedBtn = Button(increaseSpeedBtnBitmap, Pair(keyboard.rect.right + speedBtnMarginHorizontal, speedBtnY), whitePaint) {
            sheet.changeSpeed(0.02f)
        }
        val pauseGameBtn = Button(pauseGameBtnBitmap, Pair(dimensions.first - 2 * endGameBtnMarginTop - 2 * endGameBtnWidth, endGameBtnMarginTop), null) {
            paused = !paused
            it.bitmap = if (paused) resumeGameBtnBitmap else pauseGameBtnBitmap
        }
        val endGameBtn = Button(endGameBtnBitmap, Pair(dimensions.first - endGameBtnMarginTop - endGameBtnWidth, endGameBtnMarginTop), null) {
            thread.running = false
            onFinishedListener?.finish()
        }

        buttons = listOf(decreaseSpeedBtn, increaseSpeedBtn, pauseGameBtn, endGameBtn)

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

    override fun getPoints(): Int {
        return scoreboard.correctNotesPlayed
    }


    override fun update() {
        if ((frames >= FPS * 3 || frames == 0) && !paused) {
            sheet.update(clickedKeys)
            keyboard.update(sheet.correctClicks, sheet.wrongClicks)
            scoreboard.update(sheet.correctClicks.size, sheet.wrongClicks.size, sheet.missedClicks.size)
        }
        for (btn in buttons) {
            btn.update(clicks)
        }
        clicks.clear()
        clickedKeys.clear()
        if (!paused) frames++
    }

    override fun drawGameView(canvas: Canvas?) {
        if (canvas == null) return
        super.draw(canvas)
        keyboard.draw(canvas)
        sheet.draw(canvas)
        scoreboard.draw(canvas)

        for (btn in buttons) {
            btn.draw(canvas)
        }

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


