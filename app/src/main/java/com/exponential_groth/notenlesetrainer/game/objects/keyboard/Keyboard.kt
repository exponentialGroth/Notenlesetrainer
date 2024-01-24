package com.exponential_groth.notenlesetrainer.game.objects.keyboard

import android.graphics.*
import com.exponential_groth.notenlesetrainer.util.*
import kotlin.math.min

class Keyboard(val leftKey: Int, val rightKey: Int, val dimensions: Pair<Int, Int>) {

    val keys = getKeys()
    val rect = Rect(
        keys.first().rect.left,
        keys.first().rect.top,
        keys.last().rect.right,
        keys.first { it.color == KeyColor.WHITE }.rect.bottom
    )
    private val marker = getMarker()
    private var hasAdditionalKeyLeft = false


    fun update(correctClicks: List<Int>, wrongClicks: List<Int>) {
        val getIndexFromKeyNum: (Int) -> Int = { it - leftKey + if (hasAdditionalKeyLeft) 1 else 0}
        for (click in correctClicks) {
            keys[getIndexFromKeyNum(click)].apply {
                clickCorrectness = true
                framesSincePaintingStart = 0
            }
        }
        for (click in wrongClicks) {
            keys[getIndexFromKeyNum(click)].apply {
                clickCorrectness = false
                framesSincePaintingStart = 0
            }
        }
        for (key in keys) {
            key.update()
        }
    }

    fun draw(canvas: Canvas) {
        for (key in keys.filter { it.color == KeyColor.WHITE }) canvas.drawRect(
            key.rect,
            getPaint(key.keyPaint)
        )
        for (key in keys.filter { it.color == KeyColor.BLACK }) canvas.drawRect(
            key.rect,
            getPaint(key.keyPaint)
        )
        marker.draw(canvas)
    }

    fun onClick(pos: Pair<Float, Float>): Int? {
        keys.find { pos in it.rect }?.let {key ->
            if (key.color == KeyColor.BLACK) {
                return key.keyNum
            }
            keys.getOrNull(key.keyNum - leftKey + if (hasAdditionalKeyLeft) 2 else 1)?.let {
                if (pos in it.rect) return it.keyNum
            }
            return key.keyNum
        }
        return null
    }


    @JvmName("getKeyList")
    private fun getKeys(): List<Key> {
        val numOfWhiteKeys = getNumOfWhiteKeys(leftKey, rightKey)

        val bestWhiteHeight = (1- TOP_MARGIN - BOTTOM_MARGIN) * dimensions.second
        val associatedWidth = bestWhiteHeight / WHITE_HEIGHT_WIDTH_PROPORTION * numOfWhiteKeys
        val totalWidth = min(associatedWidth, (1- 2 * HORIZONTAL_MARGIN) * dimensions.first)
        val whiteHeight = totalWidth / numOfWhiteKeys * WHITE_HEIGHT_WIDTH_PROPORTION

        val top = TOP_MARGIN * dimensions.second + 0.5 * (bestWhiteHeight - whiteHeight)
        val whiteBottom = top + whiteHeight
        val blackBottom = top + whiteHeight / WHITE_BLACK_HEIGHT_PROPORTION
        val left = (dimensions.first - totalWidth) / 2

        val blackWidth = totalWidth / numOfWhiteKeys / WHITE_BLACK_WIDTH_PROPORTION
        val whiteWidth = totalWidth / numOfWhiteKeys * (1 - KEY_MARGIN_PERCENTAGE)
        val whiteMargin = (totalWidth - whiteWidth * numOfWhiteKeys) / (numOfWhiteKeys - 1)

        val getShiftByPosition: (pos: Position) -> Float = {pos ->
            blackWidth * when (pos) {
                Position.LEFT -> BLACK_KEY_SHIFT_PROPORTION
                Position.CENTER -> 0.5f
                Position.RIGHT -> (1 - BLACK_KEY_SHIFT_PROPORTION)
            }
        }

        var previousWhiteRight = left - whiteMargin
        val keys = mutableListOf<Key>()

        if (leftKey.toKeyColor() == KeyColor.WHITE && leftKey % 12 !in listOf(4, 9)) {
            keys.add(
                Key(leftKey-1,
                    Rect(left.toInt(), top.toInt(),
                        (left - getShiftByPosition((leftKey-1).toPositionInBlackKeyGroup()) + blackWidth).toInt(),
                        blackBottom.toInt()
                    )
                )
            )
            hasAdditionalKeyLeft = true
        }

        for (i in leftKey..rightKey) {
            if (i.toKeyColor() == KeyColor.WHITE) {
                val newRight = previousWhiteRight + whiteMargin + whiteWidth
                keys.add(Key(i, Rect((previousWhiteRight + whiteMargin).toInt(), top.toInt(), newRight.toInt(), whiteBottom.toInt())))
                previousWhiteRight = newRight
            } else {
                val shift = getShiftByPosition(i.toPositionInBlackKeyGroup())
                val newLeft = previousWhiteRight - shift
                keys.add(Key(i, Rect(newLeft.toInt(), top.toInt(), (newLeft + blackWidth).toInt(), blackBottom.toInt())))
            }
        }

        if (rightKey.toKeyColor() == KeyColor.WHITE && rightKey % 12 !in listOf(3, 8)) {
            keys.add(Key(rightKey+1, Rect((previousWhiteRight - getShiftByPosition((rightKey+1).toPositionInBlackKeyGroup())).toInt(), top.toInt(), previousWhiteRight.toInt(), blackBottom.toInt())))
        }

        return keys
    }

    private fun getMarker(): Marker {
        val keyToMark = if (40 in leftKey..rightKey) {
             keys.find { it.keyNum == 40 }!!
        } else if ((leftKey..rightKey).any { it % 12 == 4 }) {
            keys.filter { it.keyNum % 12 == 4 }.let { it[it.size / 2] }
        } else {
            keys[keys.size / 2]
        }

        val keyHeightDifference = (keyToMark.rect.bottom - keyToMark.rect.top) * (1 - 1f / WHITE_BLACK_HEIGHT_PROPORTION)
        val size = keyHeightDifference / 2
        return Marker(
            keyToMark.rect.left + (keyToMark.rect.right - keyToMark.rect.left) * 0.1f,
            keyToMark.rect.bottom - (keyHeightDifference - size) / 2,
            size,
            keyToMark.keyNum
        )
    }

    private fun getPaint(keyPaint: KeyPaint): Paint = when (keyPaint) {
        KeyPaint.BLACK -> blackPaint
        KeyPaint.WHITE -> whitePaint
        KeyPaint.GREEN -> greenPaint
        KeyPaint.RED -> redPaint
    }
}