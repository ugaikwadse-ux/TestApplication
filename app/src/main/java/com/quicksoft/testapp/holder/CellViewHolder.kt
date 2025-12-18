package com.quicksoft.testapp.holder

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.quicksoft.testapp.R
import com.quicksoft.testapp.model.Cell

class CellViewHolder(itemView: View) : AbstractViewHolder(itemView) {

    private val cellTextView: TextView = itemView.findViewById(R.id.cell_data)
    private val cellContainer: LinearLayout = itemView.findViewById(R.id.cell_container)

    private var textColor: Int = Color.BLACK
    private var textStyle: Int = 0
    private var textSize: Int = 15
    private var fontPosition: Int = 0

    fun setCell(cell: Cell?) {
        loadPreferences()

        cellTextView.text = cell?.data?.toString().orEmpty()
        cellTextView.setTextColor(textColor)
        cellTextView.textSize = textSize.toFloat()

        // Apply text style
        val style = when (textStyle) {
            1 -> Typeface.BOLD
            2 -> Typeface.ITALIC
            3 -> Typeface.BOLD_ITALIC
            else -> Typeface.NORMAL
        }
        cellTextView.setTypeface(Typeface.defaultFromStyle(style))

        // Apply font family
        val fontRes = when (fontPosition) {
            else -> R.font.roboto_slab
        }

        ResourcesCompat.getFont(itemView.context, fontRes)?.let {
            cellTextView.typeface = it
        }

        // Auto-size for TableView cell
        cellContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        cellTextView.requestLayout()
    }

    private fun loadPreferences() {
        val context = itemView.context

        textColor = context.pref("savetextcolor").getInt("color", Color.BLACK)
        textStyle = context.pref("textstyle").getInt("style", 0)
        textSize = context.pref("textsize").getInt("size", 15)
        fontPosition = context.pref("fontfamily").getInt("font", 0)
    }

    private fun Context.pref(name: String): SharedPreferences =
        getSharedPreferences(name, Context.MODE_PRIVATE)
}