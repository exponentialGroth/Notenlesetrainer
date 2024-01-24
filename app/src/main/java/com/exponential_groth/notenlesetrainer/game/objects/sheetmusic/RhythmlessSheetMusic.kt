package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic

import android.content.Context
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.movable.BarLine

class RhythmlessSheetMusic(context: Context, dimensions: Pair<Int, Int>, minNote: Int, maxNote: Int,
                           key: Int, difficulty: Int, FPS: Int, val animSpeed: Float
) : SheetMusic(context, dimensions,
    minNote,
    maxNote, key, difficulty, FPS, false
) {
    private val gapsTravelled get() = widthTravelled * NOTES_PER_LINE
    private var speed = 0f
    private var gapsToStopMovingAt = Float.MAX_VALUE

    init {
        repeat(NOTES_PER_LINE) {
            addNote()
            notes.last().x = (it+1f) / NOTES_PER_LINE
            if (it % BAR_LENGTH == 3) barLines.add(BarLine(1, FPS).apply { x = (it+1.5f) / NOTES_PER_LINE; speed = this@RhythmlessSheetMusic.speed })
        }
    }

    override fun updateNotes() {  // and barLines
        if (correctClicks.isNotEmpty()) {
            addNote()
            if (gapsToStopMovingAt == Float.MAX_VALUE) {
                gapsToStopMovingAt = gapsTravelled + 1
                speed = animSpeed
                for (n in notes) n.speed = speed
                for (b in barLines) b.speed = speed
            } else {
                gapsToStopMovingAt++
            }
        } else if (gapsTravelled + speed / FPS * NOTES_PER_LINE >= gapsToStopMovingAt) {
            speed = 0f
            val remainingDistance = (gapsToStopMovingAt - gapsTravelled) / NOTES_PER_LINE
            for (m in notes.plus(barLines)) {
                m.speed = 0f
                m.x -= remainingDistance
            }
            widthTravelled += remainingDistance
            gapsToStopMovingAt = Float.MAX_VALUE
        }

        widthTravelled += speed / FPS
        if (gapsTravelled.toInt() % 4 == 3 && (gapsTravelled - speed / FPS * NOTES_PER_LINE - 0.5).toInt() != (gapsTravelled - 0.5).toInt()) {
            barLines.add(BarLine(1, FPS).apply { speed = this@RhythmlessSheetMusic.speed })
        }
    }


    private fun addNote() = notes.add(
        noteGenerator.nextNote().apply {
            speed = this@RhythmlessSheetMusic.speed
            if (notes.size >= NOTES_PER_LINE - 1) {
                x = notes.last().x + 1f / NOTES_PER_LINE
            }
        }
    )
}