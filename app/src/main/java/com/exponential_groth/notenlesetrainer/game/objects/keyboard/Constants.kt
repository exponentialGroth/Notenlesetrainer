package com.exponential_groth.notenlesetrainer.game.objects.keyboard

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

const val KEY_PRESS_PAINT_TIME = 0.25f
const val TOP_MARGIN = 0.5f
const val BOTTOM_MARGIN = 0.05f
const val HORIZONTAL_MARGIN = 0.05f
const val KEY_MARGIN_PERCENTAGE = 0.05f
const val WHITE_BLACK_WIDTH_PROPORTION = 12f / 7
const val WHITE_BLACK_HEIGHT_PROPORTION = 1.5f
const val BLACK_KEY_SHIFT_PROPORTION = 0.75f
const val WHITE_HEIGHT_WIDTH_PROPORTION = 4.75f

val whitePaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN) }
val blackPaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN) }
val greenPaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN) }
val redPaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN) }