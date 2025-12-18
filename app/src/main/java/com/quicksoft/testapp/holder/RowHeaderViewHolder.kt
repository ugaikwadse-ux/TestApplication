package com.quicksoft.testapp.holder

import android.view.View
import android.widget.TextView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.quicksoft.testapp.R

class RowHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
    val rowHeaderTextView: TextView = itemView.findViewById(R.id.row_header_textview)
}