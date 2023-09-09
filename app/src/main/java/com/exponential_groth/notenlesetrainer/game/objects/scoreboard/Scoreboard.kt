package com.exponential_groth.notenlesetrainer.game.objects.scoreboard

import android.content.Context
import android.graphics.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.exponential_groth.notenlesetrainer.R
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.BOTTOM
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.TOP

class Scoreboard(context: Context, val dimensions: Pair<Int, Int>, right: Float) {
    var notesPlayed = 0
    var lives = LIVES

    private val notesString = "\uD834\uDD5F = "
    private val notesCoord: Pair<Float, Float>
    private val lifeBitmap: Bitmap
    private val lifeCoordinates: List<Pair<Float, Float>>
    private val positiveLifePaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN) }
    private val negativeLifePaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN); alpha = 100 }
    private val notesPaint: Paint

    init {
        val lifeDrawable = AppCompatResources.getDrawable(context, R.drawable.semibreve)!!
        val scaleLifeDrawable = (right - (dimensions.first * (MARGIN_LEFT + LIFE_MARGIN_HORIZONTAL * (LIVES + 1)))) / LIVES / lifeDrawable.intrinsicWidth
        lifeBitmap = lifeDrawable.toBitmap((scaleLifeDrawable * lifeDrawable.intrinsicWidth).toInt(), (scaleLifeDrawable * lifeDrawable.intrinsicHeight).toInt())
        lifeCoordinates = (1..LIVES).map { Pair(
            MARGIN_LEFT * dimensions.first + LIFE_MARGIN_HORIZONTAL * dimensions.first * (it) + lifeBitmap.width * (it-1),
            TOP * dimensions.second + (AVAILABLE_SPACE * dimensions.second - lifeBitmap.height) / 2
        ) }

        notesCoord = Pair(
            (right + MARGIN_LEFT * dimensions.first) / 2,
            BOTTOM * dimensions.second - 0.2f * AVAILABLE_SPACE * dimensions.second
        )
        notesPaint = Paint().apply { style = Paint.Style.FILL; isAntiAlias = true; color = Color.BLACK; textAlign = Paint.Align.CENTER; textSize = 64f}
    }


    fun update(correctClicks: Int, wrongClicks: Int, missedClicks: Int) {
        notesPlayed += correctClicks
        lives -= wrongClicks + missedClicks
    }

    fun draw(canvas: Canvas) {
        for (live in 1..LIVES) {
            val coord = lifeCoordinates[live-1]
            canvas.drawBitmap(lifeBitmap, coord.first, coord.second, if (live <= lives) positiveLifePaint else negativeLifePaint)
        }
        canvas.drawText(notesString + notesPlayed, notesCoord.first, notesCoord.second, notesPaint)
    }
}