package com.quicksoft.msbteresultanalysis.activity.popup

import android.content.Context
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.evrencoskun.tableview.TableView

class RowHeaderLongPressPopup(
    viewHolder: RecyclerView.ViewHolder,
    private val mTableView: TableView
) : PopupMenu(viewHolder.itemView.context, viewHolder.itemView),
    PopupMenu.OnMenuItemClickListener {

    companion object {
        private const val SCROLL_COLUMN = 1
        private const val SHOWHIDE_COLUMN = 2
        private const val REMOVE_ROW = 3
    }

    private val mRowPosition: Int = viewHolder.adapterPosition

    init {
        initialize()
    }

    private fun initialize() {
        createMenuItem()
        setOnMenuItemClickListener(this)
    }

    private fun createMenuItem() {
        val context: Context = mTableView.context
      //  menu.add(Menu.NONE, SCROLL_COLUMN, 0, context.getString(R.string.scroll_to_column_position))
        // menu.add(Menu.NONE, SHOWHIDE_COLUMN, 1, context.getString(R.string.show_hide_the_column))
      //  menu.add(Menu.NONE, REMOVE_ROW, 2, "Remove row $mRowPosition")
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            SCROLL_COLUMN -> {
                mTableView.scrollToColumnPosition(2)
            }

            /*
            SHOWHIDE_COLUMN -> {
                val column = 1
                if (mTableView.isColumnVisible(column)) {
                    mTableView.hideColumn(column)
                } else {
                    mTableView.showColumn(column)
                }
            }
            */

            REMOVE_ROW -> {
                mTableView.adapter?.removeRow(mRowPosition)
            }
        }
        return true
    }
}
