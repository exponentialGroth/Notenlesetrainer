package com.exponential_groth.notenlesetrainer.game.objects.scoreboard

import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.BOTTOM
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.CLEF_MARGIN_VERTICAL
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.TOP

const val AVAILABLE_SPACE = (BOTTOM - TOP) * (CLEF_MARGIN_VERTICAL) + (BOTTOM - TOP) * (1 - 2 * CLEF_MARGIN_VERTICAL) * 0.3f
const val MARGIN_LEFT = 0f
const val LIFE_MARGIN_HORIZONTAL = 0.01f
const val LIVES = 3

