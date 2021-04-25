package com.example.find_your_duo.chat

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.find_your_duo.R

class ChatViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var mMessage: TextView
    var mContainer: LinearLayout
    override fun onClick(view: View) {}

    init {
        itemView.setOnClickListener(this)
        mMessage = itemView.findViewById(R.id.message)
        mContainer = itemView.findViewById(R.id.container)
    }
}