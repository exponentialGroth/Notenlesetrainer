package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic

import android.content.Context
import android.graphics.Canvas
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.movable.BarLine

class GameSheetMusic(context: Context, dimensions: Pair<Int, Int>, minNote: Int, maxNote: Int,
                     key: Int, difficulty: Int, FPS: Int
) : SheetMusic(context, dimensions,
    minNote,
    maxNote, key, difficulty, FPS
) {
    private var text: SheetMusicText? = null
    private val textPaint = newLevelTextPaint.apply { textSize = clefHeight / 2 }

    val gapsTravelled get() = widthTravelled * NOTES_PER_LINE
    val levelAtTheRight get() = 1 + (gapsTravelled.toInt() + NOTES_PER_LINE) / (NOTES_PER_LEVEL + (GAP_BETWEEN_LEVELS * NOTES_PER_LINE).toInt())
    val speedAtTheRight get() = 0.075f + (levelAtTheRight - 1) * 0.025f

    init {
        repeat(NOTES_PER_LINE) {
            addNote()
            notes.last().x = (it+1f) / NOTES_PER_LINE
            if (it % BAR_LENGTH == 3) barLines.add(BarLine(1, FPS).apply { x = (it+1.5f) / NOTES_PER_LINE; speed = speedAtTheRight })
        }
    }

    override fun updateNotes() {  // and barLines, text
            val oldGap = (gapsTravelled.toInt() + NOTES_PER_LINE - 1) % (NOTES_PER_LEVEL + (GAP_BETWEEN_LEVELS * NOTES_PER_LINE).toInt()) + 1
            widthTravelled += speedAtTheRight / FPS
            val newGap = (gapsTravelled.toInt() + NOTES_PER_LINE - 1) % (NOTES_PER_LEVEL + (GAP_BETWEEN_LEVELS * NOTES_PER_LINE).toInt()) + 1
            if (newGap != oldGap) {
                if (newGap == NOTES_PER_LEVEL + (GAP_BETWEEN_LEVELS * NOTES_PER_LINE + 1).toInt() / 2) {
                    text = SheetMusicText("Level ${levelAtTheRight+1}", speedAtTheRight)
                } else if (newGap <= NOTES_PER_LEVEL) {
                    addNote()
                    if (newGap == 1) {
                        text?.speed = speedAtTheRight
                        for (barLine in barLines) barLine.speed = speedAtTheRight
                    }
                }
            } else if ((barLines.lastOrNull()?.x?:1f) <= 1 - BAR_LENGTH.toFloat() / NOTES_PER_LINE && newGap <= NOTES_PER_LEVEL || newGap == BAR_LENGTH && notes.last().x <= 1 - 0.5f / NOTES_PER_LINE && (barLines.lastOrNull()?.x?:0f) <= 1 - BAR_LENGTH.toFloat() / NOTES_PER_LINE) {
                barLines.add(BarLine(1, FPS).apply { speed = speedAtTheRight })
            } else if (newGap == 10 && notes.last().x <= 1 - 0.5f / NOTES_PER_LINE && !barLines.any { it.cardinality == 2 }) {
                barLines.add(BarLine(2, FPS).apply { speed = speedAtTheRight })
                noteGenerator.endBar()
            }
    }


    override fun update(clickedKeys: List<Int>) {
        super.update(clickedKeys)

        text?.let {
            it.update()
            if (it.shouldRemove) text = null
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        text?.let {
            canvas.drawText(it.text, staffLeft + it.x * staffWidth, clefTop + 0.5f*clefHeight, textPaint)
        }
    }


    private fun addNote() = notes.add(
        noteGenerator.nextNote().apply {
            speed = speedAtTheRight
        }
    )
}