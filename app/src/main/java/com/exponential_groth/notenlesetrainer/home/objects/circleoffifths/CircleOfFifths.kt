package com.exponential_groth.notenlesetrainer.home.objects.circleoffifths

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.exponential_groth.notenlesetrainer.R

class CircleOfFifths(context: Context, attrs: AttributeSet?, defStyleAttr: Int): RecyclerView(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context): this(context, null)

    private val fifths = mutableListOf<CircleOfFifthsItem>()
    private val circleOfFifthsAdapter = CircleOfFifthsAdapter(fifths).apply {
        recyclerView = this@CircleOfFifths
    }

    private var drawablesLoaded = false

    init {
        adapter = circleOfFifthsAdapter
        layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
        LinearSnapHelper().attachToRecyclerView(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && !drawablesLoaded) {
            drawablesLoaded = true
            val drawables = listOf(
                R.drawable.key__6,
                R.drawable.key__5,
                R.drawable.key__4,
                R.drawable.key__3,
                R.drawable.key__2,
                R.drawable.key__1,
                R.drawable.key_0,
                R.drawable.key_1,
                R.drawable.key_2,
                R.drawable.key_3,
                R.drawable.key_4,
                R.drawable.key_5,
                R.drawable.key_6,
            ).map {drwbl ->
                AppCompatResources.getDrawable(context, drwbl)!!.let {
                    it.toBitmap(w, (w * it.intrinsicHeight.toFloat() / it.intrinsicWidth).toInt())
                }
            }

            fifths.addAll(drawables.map {
                CircleOfFifthsItem(it)
            })
        }
    }

}