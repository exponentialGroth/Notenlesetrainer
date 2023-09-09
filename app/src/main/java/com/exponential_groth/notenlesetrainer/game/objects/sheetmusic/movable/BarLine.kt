package com.exponential_groth.notenlesetrainer.game.objects.sheetmusic.movable

class BarLine(val cardinality: Int, val FPS: Int): Movable() {

    fun update() {
        x -= speed / FPS
    }
}