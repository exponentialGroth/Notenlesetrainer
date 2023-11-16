package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic

import android.content.Context
import android.graphics.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.exponential_groth.notenlesetrainer.R
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.movable.BarLine
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.movable.MusicNote
import com.exponential_groth.notenlesetrainer.util.*
import kotlin.math.abs

abstract class SheetMusic(val context: Context, val dimensions: Pair<Int, Int>, minNote: Int, maxNote: Int, key: Int, difficulty: Int, val FPS: Int, val withRhythm: Boolean = true) {

    protected val clefTop: Float
    protected val clefHeight: Float
    private val clefViewportScale: Float
    val staffLeft: Float
    val staffWidth: Float
    private val noteHeight: Float
    private val staffStep: Float

    private val backGroundRect: Rect
    private val playZoneRect: Rect
    private val clefBitmap: Bitmap
    private val staff: List<Rect>
    private val noteBitmap: Bitmap
    private val reversedNoteBitmap: Bitmap
    private val accidentalBitmaps: List<Bitmap>

    protected val notes = mutableListOf<MusicNote>()

    private val barLineWidth: Float
    private val barLineTop: Float
    private val barLineBottom: Float
    private val doubleBarLineMargin: Float
    protected val barLines = mutableListOf<BarLine>()

    private val highestSupportedTone: Int
    private val lowestSupportedTone: Int
    private val highestSupportedToneWithOctaveUpSymbol: Int
    private val lowestSupportedToneWithOctaveDownSymbol: Int
    private val octaveSymbols: Map<Int, Bitmap>

    val correctClicks = mutableListOf<Int>()
    val wrongClicks = mutableListOf<Int>()
    val missedClicks = mutableListOf<Int>()
    private var widthAtLastLifeLoss = Float.MIN_VALUE
    private val hasShield get() = widthTravelled - widthAtLastLifeLoss < 0.5 / NOTES_PER_LINE

    protected var widthTravelled = 0f
    var notesPlayed = 0

    protected val noteGenerator = NoteGenerator(difficulty, minNote, maxNote, key, BAR_LENGTH, withRhythm)

    init {
        val clefDrawable = getClefByKey(key)
        clefHeight = (BOTTOM - TOP) * dimensions.second * (1 - 2 * CLEF_MARGIN_VERTICAL)
        val clefScale = clefHeight / clefDrawable.intrinsicHeight
        clefViewportScale = clefHeight / CLEF_VIEWPORT_HEIGHT
        val clefWidth = clefDrawable.intrinsicWidth * clefScale
        clefBitmap = clefDrawable.toBitmap(clefWidth.toInt(), clefHeight.toInt())
        clefTop = TOP * dimensions.second + ((BOTTOM - TOP) * dimensions.second - clefHeight) / 2
        val clefRight = MARGIN_LEFT * dimensions.first + clefWidth
        staffWidth = dimensions.first * (1 - MARGIN_LEFT - MARGIN_RIGHT) - (FIRST_ACCIDENTAL_X + abs(key) * (if (key >= 0) SHARP_WIDTH_WITH_GAP else FLAT_WIDTH_WITH_GAP)) * clefViewportScale
        staffLeft = (1 - MARGIN_RIGHT) * dimensions.first - staffWidth

        backGroundRect = Rect(
            (MARGIN_LEFT * dimensions.first).toInt(),
            (TOP * dimensions.second).toInt(),
            (dimensions.first * (1- MARGIN_RIGHT)).toInt(),
            (BOTTOM * dimensions.second).toInt()
        )
        playZoneRect = Rect(
            staffLeft.toInt(),
            backGroundRect.top,
            (staffLeft + PLAY_ZONE_WIDTH * staffWidth).toInt(),
            backGroundRect.bottom
        )
        staff = (0..4).map {
            Rect(clefRight.toInt() - 1, (clefTop + staffLineTops[it] * clefViewportScale).toInt(), (dimensions.first * (1- MARGIN_RIGHT)).toInt(), (clefTop + staffLineBottoms[it] * clefViewportScale).toInt())
        }
        staffStep = STAFF_STEP * clefViewportScale

        val noteDrawable = AppCompatResources.getDrawable(context, R.drawable.quarterupstem)!!
        val oldNoteHeadHeight = (NOTE_HEAD_HEIGHT / NOTE_DRAWABLE_VIEWPORT_HEIGHT * noteDrawable.intrinsicHeight)
        val noteScale = STAFF_STEP * clefViewportScale / oldNoteHeadHeight
        noteHeight = noteDrawable.intrinsicHeight * noteScale
        noteBitmap = noteDrawable.toBitmap((noteScale * noteDrawable.intrinsicWidth).toInt(), noteHeight.toInt())
        reversedNoteBitmap = Bitmap.createBitmap(noteBitmap, 0, 0, noteBitmap.width, noteBitmap.height, Matrix().apply { postRotate(180f) }, true)
        initNotes()

        val accidentalDrawables = listOf(R.drawable.double_flat, R.drawable.flat, R.drawable.natural, R.drawable.sharp, R.drawable.double_sharp).map { AppCompatResources.getDrawable(context, it)!! }
        accidentalBitmaps = listOf(
            accidentalDrawables[0].toBitmap((FLAT_HEIGHT_OVER_CLEF_HEIGHT * clefHeight / accidentalDrawables[0].intrinsicHeight * accidentalDrawables[0].intrinsicWidth).toInt(), (FLAT_HEIGHT_OVER_CLEF_HEIGHT * clefHeight).toInt()),
            accidentalDrawables[1].toBitmap((FLAT_HEIGHT_OVER_CLEF_HEIGHT * clefHeight / accidentalDrawables[1].intrinsicHeight * accidentalDrawables[1].intrinsicWidth).toInt(), (FLAT_HEIGHT_OVER_CLEF_HEIGHT * clefHeight).toInt()),
            accidentalDrawables[2].toBitmap((SHARP_HEIGHT_OVER_CLEF_HEIGHT * clefHeight / accidentalDrawables[2].intrinsicHeight * accidentalDrawables[2].intrinsicWidth).toInt(), (SHARP_HEIGHT_OVER_CLEF_HEIGHT * clefHeight).toInt()), // natural has about the same height as a sharp
            accidentalDrawables[3].toBitmap((SHARP_HEIGHT_OVER_CLEF_HEIGHT * clefHeight / accidentalDrawables[3].intrinsicHeight * accidentalDrawables[3].intrinsicWidth).toInt(), (SHARP_HEIGHT_OVER_CLEF_HEIGHT * clefHeight).toInt()),
            accidentalDrawables[4].toBitmap((staffStep / accidentalDrawables[4].intrinsicHeight * accidentalDrawables[4].intrinsicWidth).toInt(), staffStep.toInt()),
        )

        val octaveSymbolHeight = staffStep * OCTAVE_SYMBOL_HEIGHT_OVER_STAFF_STEP
        highestSupportedTone = 56.plusWhiteKeys((2 * (staff[0].top - backGroundRect.top) / staffStep).toInt())
        highestSupportedToneWithOctaveUpSymbol = 56.plusWhiteKeys((2 * (staff[0].top - backGroundRect.top - octaveSymbolHeight) / staffStep).toInt())
        lowestSupportedTone = 45.minusWhiteKeys((2 * (backGroundRect.bottom - staff.last().bottom) / staffStep).toInt())
        lowestSupportedToneWithOctaveDownSymbol = 45.minusWhiteKeys((2 * (backGroundRect.bottom - staff.last().bottom - octaveSymbolHeight) / staffStep).toInt())
        val neededOctaveDrawables = mutableListOf<Pair<Int, Int>>()
        val numOfOctavesBelow = (lowestSupportedTone - minNote + 11) / 12
        if (numOfOctavesBelow >= 1) neededOctaveDrawables.add(Pair(-1, R.drawable.octavedown))
        if (numOfOctavesBelow >= 2) neededOctaveDrawables.add(Pair(-2, R.drawable.twooctavesdown))
        if (numOfOctavesBelow >= 3) neededOctaveDrawables.add(Pair(-3, R.drawable.threeoctavesdown))
        val numOfOctavesOnTop = (maxNote - highestSupportedTone + 11) / 12
        if (numOfOctavesOnTop >= 1) neededOctaveDrawables.add(Pair(1, R.drawable.octaveup))
        if (numOfOctavesOnTop >= 2) neededOctaveDrawables.add(Pair(2, R.drawable.twooctavesup))

        octaveSymbols = neededOctaveDrawables.associate {
            Pair(it.first, AppCompatResources.getDrawable(context, it.second)!!.let { drawable ->
                drawable.toBitmap(
                    (octaveSymbolHeight / drawable.intrinsicHeight * drawable.intrinsicWidth).toInt(),
                    octaveSymbolHeight.toInt()
                )
            })
        }


        barLineWidth = BAR_LINE_WIDTH * staffWidth
        barLineTop = staff[0].top.toFloat()
        barLineBottom = staff.last().bottom.toFloat()
        doubleBarLineMargin = DOUBLE_BAR_LINE_MARGIN * staffWidth
    }

    abstract fun updateNotes()

    private fun initNotes() {
        MusicNote.width = noteBitmap.width / staffWidth
        MusicNote.FPS = FPS
    }

    open fun update(clickedKeys: List<Int>) {
        correctClicks.clear()
        wrongClicks.clear()
        missedClicks.clear()
        val remainingClickedKeys = clickedKeys.toMutableList()
        var prevNoteWasPlayed = true
        for (note in notes) {
            note.update()
            if (note.tone in remainingClickedKeys && !note.played) {
                if ((note.inPlayingInterval || !withRhythm && prevNoteWasPlayed)) {
                    note.played = true
                    correctClicks.add(note.tone)
                } else if (!(hasShield && withRhythm)) {
                    wrongClicks.add(note.tone)
                    widthAtLastLifeLoss = widthTravelled
                }
                remainingClickedKeys.remove(note.tone)
            } else if (!note.played && note.shouldRemove && !(hasShield && withRhythm)) {
                missedClicks.add(note.tone)
                widthAtLastLifeLoss = widthTravelled
            }
            prevNoteWasPlayed = note.played
        }
        if (!(hasShield && withRhythm) && wrongClicks.addAll(remainingClickedKeys)) {
            widthAtLastLifeLoss = widthTravelled
        }

        if (notes.firstOrNull()?.shouldRemove == true) {
            notes.removeFirst()
            notesPlayed++
        }

        for (barLine in barLines) {
            barLine.update()
        }

        barLines.firstOrNull()?.let {
            if (it.cardinality == 1 && it.x + BAR_LINE_WIDTH / 2 < 0 || it.x + DOUBLE_BAR_LINE_MARGIN / 2 + BAR_LINE_WIDTH < 0) {
                barLines.removeFirst()
            }
        }

        updateNotes()
    }

    open fun draw(canvas: Canvas) {
        canvas.drawRect(backGroundRect, whitePaint)
        canvas.drawBitmap(clefBitmap, MARGIN_LEFT * dimensions.first, clefTop, null)
        staff.forEach { canvas.drawRect(it, blackPaint) }

        for (note in notes) {
            var octavesUp: Int
            val whiteToneToDraw = note.whiteToneWithoutAccidental.let {
                octavesUp = if (it > highestSupportedTone) {
                    (it - highestSupportedToneWithOctaveUpSymbol + 11) / 12
                } else if (it < lowestSupportedTone) {
                    - (lowestSupportedToneWithOctaveDownSymbol - it + 11) / 12
                } else 0

                it - 12 * octavesUp
            }
            val noteHeadY = getNoteHeadYByKeyNum(whiteToneToDraw)
            val noteY = getNoteY(whiteToneToDraw, noteHeadY)
            note.accidental?.let {accidental ->
                accidentalBitmaps[accidental.value + 2].let {
                    canvas.drawBitmap(it,
                        staffLeft - noteBitmap.width + note.x * staffWidth - ACCIDENTAL_MARGIN_RIGHT_OVER_NOTE_WIDTH * noteBitmap.width - it.width,
                        when (accidental) {
                            Accidental.FLAT, Accidental.DOUBLE_FLAT -> noteHeadY + FLAT_RELATIVE_POS_OVER_CLEF_HEIGHT * clefHeight
                            else -> noteHeadY + noteBitmap.height * NOTE_HEAD_HEIGHT / NOTE_DRAWABLE_VIEWPORT_HEIGHT / 2 - it.height / 2
                        }, null
                    )
                }
            }
            canvas.drawBitmap(if (whiteToneToDraw <= 49) noteBitmap else reversedNoteBitmap, staffLeft + note.x * staffWidth - noteBitmap.width, noteY, null)
            for (y in getLedgerLineYs(whiteToneToDraw)) {
                canvas.drawRect(
                    staffLeft - noteBitmap.width + note.x * staffWidth - 0.1f * noteBitmap.width, y,
                    staffLeft - noteBitmap.width + note.x * staffWidth + 1.1f * noteBitmap.width,
                    y + clefViewportScale * (staffLineBottoms.first() - staffLineTops.first()), blackPaint
                )
            }

            if (octavesUp != 0) {
                octaveSymbols[octavesUp]?.let {
                    canvas.drawBitmap(it,
                        staffLeft + note.x * staffWidth - 0.5f * (it.width + noteBitmap.width),
                        if (octavesUp > 0) backGroundRect.top + OCTAVE_SYMBOL_MARGIN_TOP_OVER_STAFF_STEP * staffStep else backGroundRect.bottom - it.height - OCTAVE_SYMBOL_MARGIN_TOP_OVER_STAFF_STEP * staffStep,
                       null
                    )
                }
            }
        }

        for (barLine in barLines) {
            if (barLine.cardinality == 1) {
                canvas.drawRect(
                    staffLeft + barLine.x * staffWidth - barLineWidth / 2, barLineTop,
                    staffLeft + barLine.x * staffWidth + barLineWidth / 2, barLineBottom, blackPaint
                )
            } else {
                canvas.drawRect(
                    staffLeft + barLine.x * staffWidth - barLineWidth - doubleBarLineMargin / 2, barLineTop,
                    staffLeft + barLine.x * staffWidth - doubleBarLineMargin / 2, barLineBottom, blackPaint
                )
                canvas.drawRect(staffLeft + barLine.x * staffWidth + barLineWidth + doubleBarLineMargin / 2, barLineTop,
                    staffLeft + barLine.x * staffWidth + doubleBarLineMargin / 2, barLineBottom, blackPaint
                )
            }
        }

        if (withRhythm) canvas.drawRect(playZoneRect, playZonePaint)
    }



    private fun getNoteHeadYByKeyNum(key: Int): Float {
        val headHeight49 = clefTop + staffLineBottoms[2] * clefViewportScale
        val keysBetween = if (key <= 48) {
            getNumOfWhiteKeys(key, 49) - 1
        } else {
            -getNumOfWhiteKeys(49, key) + 1
        }
        return headHeight49 + keysBetween * staffStep / 2
    }

    private fun getNoteY(key: Int, headY: Float) = if (key > 49) headY else headY - (1 - NOTE_HEAD_HEIGHT / NOTE_DRAWABLE_VIEWPORT_HEIGHT) * noteHeight

    private fun getLedgerLineYs(key: Int): List<Float> {
        if (key < 41) {
            var ledgerLines = 0
            val distances = listOf(3, 4, 3, 3, 4, 3, 4)
            var i = 40
            while (i >= key) {
                for (d in distances) {
                    ledgerLines++
                    i -= d
                    if (i < key) break
                }
            }
            val lowestStaffLine = staff.last().top
            return (1..ledgerLines).map { (lowestStaffLine + it * staffStep) }

        } else if (key > 60) {
            var ledgerLines = 0
            val distances = listOf(3, 4, 3, 4, 3, 3, 4)
            var i = 61
            while (i <= key) {
                for (d in distances) {
                    ledgerLines++
                    i += d
                    if (i > key) break
                }
            }
            val highestStaffLine = staff.first().top
            return (1..ledgerLines).map { (highestStaffLine - it * staffStep) }
        }
        return emptyList()
    }


    private fun getClefByKey(key: Int) = AppCompatResources.getDrawable(context, when (key) {
        -6 -> R.drawable.key__6
        -5 -> R.drawable.key__5
        -4 -> R.drawable.key__4
        -3 -> R.drawable.key__3
        -2 -> R.drawable.key__2
        -1 -> R.drawable.key__1
        1 -> R.drawable.key_1
        2 -> R.drawable.key_2
        3 -> R.drawable.key_3
        4 -> R.drawable.key_4
        5 -> R.drawable.key_5
        6 -> R.drawable.key_6
        else -> R.drawable.key_0
    })!!
}