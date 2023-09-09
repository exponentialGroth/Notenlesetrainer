package com.exponential_groth.notenlesetrainer.game.objects.scoreboard

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.BOTTOM

class PracticeScoreboard(val dimensions: Pair<Int, Int>, right: Float) {
    var correctNotesPlayed = 0
    var mostPossibleNotesPlayed = 0

    private val notesString = "\uD834\uDD5F = "
    private val notesCoord: Pair<Float, Float>
    private val notesPaint: Paint

    init {
        notesCoord = Pair(
            (right + MARGIN_LEFT * dimensions.first) / 2,
            BOTTOM * dimensions.second - 0.2f * AVAILABLE_SPACE * dimensions.second
        )
        notesPaint = Paint().apply { style = Paint.Style.FILL; isAntiAlias = true; color = Color.BLACK; textAlign = Paint.Align.CENTER; textSize = 64f}
    }

    fun update(correctClicks: Int, wrongClicks: Int, missedClicks: Int) {
        correctNotesPlayed += correctClicks
        mostPossibleNotesPlayed += correctClicks + wrongClicks + missedClicks
    }

    fun draw(canvas: Canvas) {
        canvas.drawText(notesString + (100 * correctNotesPlayed.toFloat() / (if (mostPossibleNotesPlayed == 0) 1 else mostPossibleNotesPlayed) + 0.5).toInt() + "%", notesCoord.first, notesCoord.second, notesPaint)
    }
}