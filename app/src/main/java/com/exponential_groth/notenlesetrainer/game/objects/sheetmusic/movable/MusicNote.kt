package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.movable

import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.Accidental
import com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.PLAY_ZONE_WIDTH

class MusicNote(val tone: Int, val withRhythm: Boolean = true): Movable() {
    var accidental: Accidental? = null
    var whiteToneWithoutAccidental = 0

    val inPlayingInterval get() = withRhythm && x < PLAY_ZONE_WIDTH + width
    var played = false
    val shouldRemove get() = x < 0 || !withRhythm && x < PLAY_ZONE_WIDTH

    fun update() {
        x -= speed / FPS
    }

    override fun toString() = "$tone:  x = $x;  speed = $speed"

    companion object {
        var FPS = 60
        var width = 0f
    }
}