package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic

import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.movable.MusicNote
import com.exponential_groth.notenlesetrainer.util.KeyColor
import com.exponential_groth.notenlesetrainer.util.getPossibleTonesForDifficulty1
import com.exponential_groth.notenlesetrainer.util.toKeyColor
import kotlin.math.abs
import kotlin.random.Random

class NoteGenerator(difficulty: Int, minTone: Int, maxTone: Int, val key: Int, private val barLength: Int, val FPS: Int, val withRhythm: Boolean = true) {


    private val accidentalPositionsFromKey = when (key) {
        in 1..7 -> listOf(9, 4, 11, 6, 1, 8, 3).subList(0, key)
        in -7..-1 -> listOf(3, 8, 1, 6, 11, 4, 9).subList(0, -key)
        else -> emptyList()
    }
    private val possibleTones = if (difficulty == 1) {
        getPossibleTonesForDifficulty1(key).let { tones -> (minTone..maxTone).filter { it % 12 in tones } }
    } else {
        (minTone..maxTone).toList()
    }

    private val possibleAccidentals = when (difficulty) {
        1 -> emptyList()
        2, 4 -> listOf(Accidental.FLAT, Accidental.SHARP, Accidental.NATURAL)
        else -> listOf(Accidental.FLAT, Accidental.DOUBLE_FLAT, Accidental.SHARP, Accidental.DOUBLE_SHARP, Accidental.NATURAL)
    }

    private val accidentalProbability = when (difficulty) {
        1 -> 0f
        2, 3 -> 0.2f
        4, 5 -> 0.45f
        else -> 0.7f
    }

    private var lastNotesInBar = mutableListOf<MusicNote>()


    fun endBar() {
        lastNotesInBar.clear()
    }

    fun nextNote(): MusicNote {
        val tone = possibleTones[Random.nextInt(possibleTones.size)]
        val note = MusicNote(tone, FPS, withRhythm).apply {
            whiteToneWithoutAccidental = tone - (accidentalOnTone(tone)?.value?:0)
        }
        val accidentalOnTone = accidentalOnTone(tone)
        if (possibleAccidentals.isNotEmpty()) {
            val reallyPossibleAccidentals = possibleAccidentals.toMutableList()
            val octavedTone = tone % 12

            if (tone.toKeyColor() == KeyColor.WHITE) {
                if (octavedTone !in listOf(4, 9)) {
                    reallyPossibleAccidentals.remove(Accidental.SHARP)
                } else {
                    reallyPossibleAccidentals.remove(Accidental.DOUBLE_SHARP)
                }
                if (octavedTone !in listOf(3, 8)) {
                    reallyPossibleAccidentals.remove(Accidental.FLAT)
                } else {
                    reallyPossibleAccidentals.remove(Accidental.DOUBLE_FLAT)
                }
                if (accidentalOnTone == null) reallyPossibleAccidentals.remove(Accidental.NATURAL)
            } else {
                reallyPossibleAccidentals.remove(Accidental.NATURAL)
                if (tone % 12 !in listOf(5, 10)) reallyPossibleAccidentals.remove(Accidental.DOUBLE_SHARP)
                if (tone % 12 !in listOf(2, 7)) reallyPossibleAccidentals.remove(Accidental.DOUBLE_FLAT)

            }

            if (reallyPossibleAccidentals.isNotEmpty()) {
                if (tone.toKeyColor() == KeyColor.WHITE && accidentalOnTone != null || tone.toKeyColor() == KeyColor.BLACK && accidentalOnTone == null) {
                    val randomNum = Random.nextInt(reallyPossibleAccidentals.size)
                    note.accidental = reallyPossibleAccidentals[randomNum]
                    note.whiteToneWithoutAccidental = tone - note.accidental!!.value
                } else {
                    val randomNum = Random.nextFloat()
                    if (randomNum < accidentalProbability) {
                        note.accidental = reallyPossibleAccidentals[(randomNum / accidentalProbability * reallyPossibleAccidentals.size).toInt()]
                        note.whiteToneWithoutAccidental = tone - note.accidental!!.value
                    }
                }
            }
        }


        if (lastNotesInBar.size == barLength - 1) {
            lastNotesInBar.clear()
        } else lastNotesInBar.add(note)

        return note
    }


    private fun accidentalOnTone(keyNum: Int) = if (keyNum.toKeyColor() == KeyColor.WHITE) {
        lastNotesInBar.findLast {
            it.tone - (it.accidental?.value?:0) == keyNum
        }?.accidental?: if (keyNum % 12 in accidentalPositionsFromKey) {
            if (key > 0) Accidental.SHARP else Accidental.FLAT
        } else null
    } else {
        lastNotesInBar.find {
            it.tone == keyNum
        }?.accidental?: if ((keyNum - if (key == 0) 0 else abs(key) / key) % 12 in accidentalPositionsFromKey) {
            if (key > 0) Accidental.SHARP else Accidental.FLAT
        } else null
    }
}