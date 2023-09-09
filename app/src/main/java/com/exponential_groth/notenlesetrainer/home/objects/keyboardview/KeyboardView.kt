package com.exponential_groth.notenlesetrainer.home.objects.keyboardview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.exponential_groth.notenlesetrainer.R
import com.exponential_groth.notenlesetrainer.util.KeyColor
import com.exponential_groth.notenlesetrainer.util.Position
import com.exponential_groth.notenlesetrainer.util.getNumOfWhiteKeys
import com.exponential_groth.notenlesetrainer.util.getPossibleTonesForDifficulty1
import com.exponential_groth.notenlesetrainer.util.toKeyColor
import com.exponential_groth.notenlesetrainer.util.toPositionInBlackKeyGroup

class KeyboardView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): View(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context): this(context, null)

    private var minKey = 0
        set(value) {
            if (field == value) return
            field = value
            invalidate()
            requestLayout()
        }

    private var maxKey = 88
        set(value) {
            if (field == value) return
            field = value
            invalidate()
            requestLayout()
        }

    var noAdditionalAccidentals = true
        set(value) {
            if (field == value) return
            field = value
            invalidate()
            requestLayout()
        }

    var key = 0
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    private var selectedKeyLeft = 32
    private var selectedKeyRight = 54

    private var c1Marker: Pair<Bitmap, Pair<Float, Float>>? = null
    private val numOfWhiteKeys get() = getNumOfWhiteKeys(minKey, maxKey)
    private val hasAdditionalKeyLeft get() = minKey.toKeyColor() == KeyColor.WHITE && minKey % 12 !in listOf(4, 9)
    private var keys = emptyList<Key>()

    private var insetForHighlighting = 0f


    private val selectedKeyPaint = Paint().apply { colorFilter = PorterDuffColorFilter(context.getColor(R.color.teal_200), PorterDuff.Mode.SRC_IN) }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.KeyboardView, defStyleAttr, defStyleRes).apply {
            try {
                minKey = getInt(R.styleable.KeyboardView_minKey, 0)
                maxKey = getInt(R.styleable.KeyboardView_maxKey, 88)
            } finally {
                recycle()
            }
        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW = paddingLeft + paddingRight + suggestedMinimumWidth
        val availableW = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val associatedH = (MeasureSpec.getSize(availableW) / numOfWhiteKeys * WHITE_HEIGHT_WIDTH_PROPORTION).toInt()
        val h = resolveSizeAndState(associatedH, heightMeasureSpec, 0)
        val w = MeasureSpec.getSize(h).let {
            if (it == h) {
                availableW
            } else {
                (it / WHITE_HEIGHT_WIDTH_PROPORTION * numOfWhiteKeys).toInt()
            }
        }
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createKeys(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), blackPaint)

        val possibleKeys = if (noAdditionalAccidentals) {
            getPossibleTonesForDifficulty1(key).let { tones -> (selectedKeyLeft..selectedKeyRight).filter { it % 12 in tones } }
        } else (selectedKeyLeft..selectedKeyRight)

        for (key in keys) {
            if (key.keyNum.toKeyColor() == KeyColor.BLACK) continue
            val paint = if (key.keyNum in possibleKeys) selectedKeyPaint else whitePaint
            canvas.drawRect(key.rect, paint)
        }
        for (key in keys) {
            if (key.keyNum.toKeyColor() == KeyColor.WHITE) continue
            canvas.drawRect(key.rect, blackPaint)

            if (key.keyNum in possibleKeys) {
                canvas.drawRect(key.rect.left + insetForHighlighting, key.rect.top.toFloat(), key.rect.right - insetForHighlighting, key.rect.bottom - insetForHighlighting, selectedKeyPaint)
            }
        }

        c1Marker?.let {
            canvas.drawBitmap(it.first, it.second.first, it.second.second, null)
        }
    }


    fun updateSelectedKeys(leftKey: Int, rightKey: Int) {
        selectedKeyLeft = leftKey
        selectedKeyRight = rightKey
        invalidate()
    }


    private fun createKeys(canvasW: Int, canvasH: Int) {
        val blackWidth = canvasW / numOfWhiteKeys / WHITE_BLACK_WIDTH_PROPORTION
        val whiteWidth = canvasW / numOfWhiteKeys * (1 - KEY_MARGIN_PERCENTAGE)
        val whiteMargin = (canvasW - whiteWidth * numOfWhiteKeys) / (numOfWhiteKeys - 1)
        val blackBottom = canvasH / WHITE_BLACK_HEIGHT_PROPORTION

        insetForHighlighting = blackWidth * GREEN_INSET_OVER_WHITE_WIDTH

        val getShiftByPosition: (pos: Position) -> Float = { pos ->
            blackWidth * when (pos) {
                Position.LEFT -> BLACK_KEY_SHIFT_PROPORTION
                Position.CENTER -> 0.5f
                Position.RIGHT -> (1 - BLACK_KEY_SHIFT_PROPORTION)
            }
        }

        var previousWhiteRight = - whiteMargin
        val createdKeys = mutableListOf<Key>()

        if (hasAdditionalKeyLeft) {
            createdKeys.add(
                Key(
                    minKey - 1,
                    Rect(
                        0, 0,
                        (blackWidth - getShiftByPosition((minKey - 1).toPositionInBlackKeyGroup())).toInt(),
                        blackBottom.toInt()
                    )
                )
            )
        }

        for (i in minKey..maxKey) {
            if (i.toKeyColor() == KeyColor.WHITE) {
                val newRight = previousWhiteRight + whiteMargin + whiteWidth
                createdKeys.add(
                    Key(
                        i,
                        Rect(
                            (previousWhiteRight + whiteMargin).toInt(),
                            0,
                            newRight.toInt(),
                            canvasH
                        )
                    )
                )
                previousWhiteRight = newRight
            } else {
                val shift = getShiftByPosition(i.toPositionInBlackKeyGroup())
                val newLeft = previousWhiteRight - shift
                createdKeys.add(
                    Key(
                        i,
                        Rect(
                            newLeft.toInt(),
                            0,
                            (newLeft + blackWidth).toInt(),
                            blackBottom.toInt()
                        )
                    )
                )
            }
        }

        if (maxKey.toKeyColor() == KeyColor.WHITE && maxKey % 12 !in listOf(3, 8)) {
            createdKeys.add(
                Key(
                    maxKey + 1,
                    Rect(
                        (previousWhiteRight - getShiftByPosition((maxKey + 1).toPositionInBlackKeyGroup())).toInt(), 0,
                        previousWhiteRight.toInt(), blackBottom.toInt()
                    )
                )
            )
        }

        keys = createdKeys

        val c1Drawable = AppCompatResources.getDrawable(context, R.drawable.c1)!!
        val c1Key = keys.find { it.keyNum == 40 }!!
        val maxW = (c1Key.rect.right - c1Key.rect.left) * .9f
        val maxH = (c1Key.rect.bottom - c1Key.rect.top) * .9f
        if (maxW / c1Drawable.intrinsicWidth * c1Drawable.intrinsicHeight <= maxH) {
            val bm = c1Drawable.toBitmap(maxW.toInt(), (maxW / c1Drawable.intrinsicWidth * c1Drawable.intrinsicHeight).toInt(), null)
            with(c1Key.rect) {
                val pos = Pair(
                    left + .5f * (right - left - maxW),
                    blackBottom + .5f * (bottom - blackBottom - bm.height)
                )
                c1Marker = Pair(bm, pos)
            }
        }
    }

}