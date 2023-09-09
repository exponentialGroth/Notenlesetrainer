package com.exponential_groth.notenlesetrainer.home.objects.circleoffifths

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.exponential_groth.notenlesetrainer.R

class CircleOfFifthsAdapter(val data: MutableList<CircleOfFifthsItem>): RecyclerView.Adapter<CircleOfFifthsAdapter.ViewHolder>() {
    var recyclerView: RecyclerView? = null

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imgView: ImageView
        init {
            imgView = view.findViewById(R.id.circleOfFifthsImgView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.circle_of_fifths_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bitmap = data[position].image
        holder.imgView.setImageBitmap(bitmap)
        holder.imgView.requestLayout()
    }

    override fun getItemCount() = data.size
}