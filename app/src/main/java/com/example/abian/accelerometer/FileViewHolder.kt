package com.example.abian.accelerometer

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

interface FileViewHolderListener {
    fun onLongTap(index : Int)
    fun onTap(index : Int)
}

class FileViewHolder(val view: View, val r_tap: FileViewHolderListener): RecyclerView.ViewHolder(view),
    View.OnLongClickListener, View.OnClickListener {

    private val filenameTextView: TextView = view.findViewById(R.id.filename)
    val layout: ConstraintLayout= view.findViewById(R.id.rootLayout)

    init {
        layout.setOnClickListener(this)
        layout.setOnLongClickListener(this)
    }

    override fun onClick(v: View?) {
        r_tap.onTap(adapterPosition)
    }
    override fun onLongClick(v: View?): Boolean {
        r_tap.onLongTap(adapterPosition)
        return true
    }
}