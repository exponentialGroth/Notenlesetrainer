package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic

import android.content.Context
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.movable.BarLine

class PracticeSheetMusic(context: Context, dimensions: Pair<Int, Int>, minNote: Int, maxNote: Int,
                     key: Int, difficulty: Int, FPS: Int
) : SheetMusic(context, dimensions,
    minNote,
    maxNote, key, difficulty, FPS
) {

    val gapsTravelled get() = widthTravelled * NOTES_PER_LINE
    var speed = 0.05f

    init {
        repeat(NOTES_PER_LINE) {
            addNote((it+1f) / NOTES_PER_LINE)
            if (it % BAR_LENGTH == 3) barLines.add(BarLine(1, FPS).apply { x = (it+1.5f) / NOTES_PER_LINE; speed = this@PracticeSheetMusic.speed })
        }
    }

    override fun updateNotes() {  // and barLines, text
        val oldGap = gapsTravelled
        widthTravelled += speed / FPS
        val newGap = gapsTravelled
        if (newGap.toInt() != oldGap.toInt()) {
            addNote(notes.last().x + 1f / NOTES_PER_LINE)
        } else if (1 - barLines.last().x <= 4f / NOTES_PER_LINE) {
            barLines.add(BarLine(1, FPS).apply {
                speed = this@PracticeSheetMusic.speed
                x = barLines.last().x + 4f / NOTES_PER_LINE
            })
        }
    }

    fun changeSpeed(dv: Float) {
        if (speed + dv > 0) {
            speed += dv
        }
        for (note in notes) note.speed = speed
        for (bl in barLines) bl.speed = speed
    }


    private fun addNote(pos: Float = 1f) = notes.add(
        noteGenerator.nextNote().apply {
            speed = this@PracticeSheetMusic.speed
            x = pos
        }
    )
}