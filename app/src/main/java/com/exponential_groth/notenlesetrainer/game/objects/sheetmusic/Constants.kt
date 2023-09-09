package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

const val TOP = 0f
const val BOTTOM = 0.45f
const val MARGIN_LEFT = 0f
const val MARGIN_RIGHT = 0f
const val CLEF_MARGIN_VERTICAL = 0.2f
const val CLEF_VIEWPORT_HEIGHT = 1800f
const val STAFF_STEP = 180f
const val PLAY_ZONE_WIDTH = 0.05f
const val FLAT_WIDTH_WITH_GAP = 189f
const val SHARP_WIDTH_WITH_GAP = 212f
const val FIRST_ACCIDENTAL_X = 2100 - 730
val staffLineTops = List(5) { 531 + STAFF_STEP * it }  // listOf(31.35, 50.49, 70.14, 89.54, 109.44).map { it.toFloat()-1 }
val staffLineBottoms = List(5) { 549 + STAFF_STEP * it }  // listOf(31.35, 50.49, 70.14, 89.54, 109.44).map { it.toFloat()+1 }
const val SHARP_HEIGHT_OVER_CLEF_HEIGHT = (1050f - 570) / CLEF_VIEWPORT_HEIGHT
const val FLAT_HEIGHT_OVER_CLEF_HEIGHT = (1026f - 573) / CLEF_VIEWPORT_HEIGHT
const val FLAT_RELATIVE_POS_OVER_CLEF_HEIGHT = - (788f - 573.5f) / CLEF_VIEWPORT_HEIGHT
const val ACCIDENTAL_MARGIN_RIGHT_OVER_NOTE_WIDTH = 0.4f

const val NOTE_DRAWABLE_VIEWPORT_HEIGHT = 41.92f
const val NOTE_HEAD_HEIGHT = NOTE_DRAWABLE_VIEWPORT_HEIGHT - 30.475f
const val NOTES_PER_LINE = 5
const val NOTES_PER_LEVEL = 10  // > NOTES_PER_LINE
const val GAP_BETWEEN_LEVELS = 1f  // >= 1
const val BAR_LENGTH = 4
const val BAR_LINE_WIDTH = 0.0025f
const val DOUBLE_BAR_LINE_MARGIN = 0.01f
const val OCTAVE_SYMBOL_HEIGHT_OVER_STAFF_STEP = 1.5f
const val OCTAVE_SYMBOL_MARGIN_TOP_OVER_STAFF_STEP = 1f

val whitePaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN) }
val blackPaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN) }
val playZonePaint = Paint().apply { colorFilter =  PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN); alpha = 50 }
val newLevelTextPaint = Paint().apply { style = Paint.Style.FILL; isAntiAlias = true; color = Color.BLUE; textAlign = Paint.Align.CENTER }