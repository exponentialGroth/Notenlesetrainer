package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic

import com.exponential_groth.notenlesetrainer.game.FPS

class SheetMusicText(val text: String, var speed: Float = 0f) {
    var x = 1f
    val shouldRemove get() = x < 1f / NOTES_PER_LINE

    fun update() {
        x -= speed / FPS
    }
}