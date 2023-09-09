package com.exponential_groth.notenlesetrainer.home.objects.keyboardview

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

const val KEY_MARGIN_PERCENTAGE = 0.05f
const val WHITE_BLACK_WIDTH_PROPORTION = 12f / 7
const val WHITE_BLACK_HEIGHT_PROPORTION = 1.5f
const val BLACK_KEY_SHIFT_PROPORTION = 0.75f
const val WHITE_HEIGHT_WIDTH_PROPORTION = 4.75f
const val GREEN_INSET_OVER_WHITE_WIDTH = 0.1f

val whitePaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN) }
val blackPaint = Paint().apply { colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN) }