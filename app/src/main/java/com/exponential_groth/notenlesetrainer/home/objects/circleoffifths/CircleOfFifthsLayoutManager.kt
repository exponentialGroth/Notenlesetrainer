package com.exponential_groth.notenlesetrainer.home.objects.circleoffifths

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exponential_groth.notenlesetrainer.util.OnItemSelectedListener
import kotlin.math.abs

class CircleOfFifthsLayoutManager(context: Context?): LinearLayoutManager(context) {
    constructor(): this(null)

    private lateinit var recyclerView: RecyclerView
    var onItemSelectedListener: OnItemSelectedListener? = null

    init {
        orientation = HORIZONTAL
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        recyclerView = view!!
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            val recyclerViewCenterX = getRecyclerViewCenterX()
            var minDistance = recyclerView.width
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                val childCenterX = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2
                val d = abs(childCenterX - recyclerViewCenterX)
                if (d < minDistance) {
                    minDistance = d
                } else {
                    val pos = recyclerView.getChildAdapterPosition(child)
                    onItemSelectedListener?.onItemSelected(pos)
                    return
                }
            }

            val pos = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(recyclerView.childCount))
            onItemSelectedListener?.onItemSelected(pos)
        }
    }


    private fun getRecyclerViewCenterX(): Int {
        return (recyclerView.right + recyclerView.left) / 2
    }
}