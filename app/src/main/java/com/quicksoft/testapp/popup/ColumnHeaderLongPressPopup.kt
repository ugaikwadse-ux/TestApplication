package com.quicksoft.msbteresultanalysis.activity.popup


import android.content.Context
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import com.evrencoskun.tableview.TableView
import com.evrencoskun.tableview.sort.SortState
import com.quicksoft.testapp.R

import com.quicksoft.testapp.holder.ColumnHeaderViewHolder


class ColumnHeaderLongPressPopup(
    viewHolder: ColumnHeaderViewHolder,
    private val mTableView: TableView
) : PopupMenu(viewHolder.itemView.context, viewHolder.itemView),
    PopupMenu.OnMenuItemClickListener {

    companion object {
        private const val ASCENDING = 1
        private const val DESCENDING = 2
        private const val HIDE_ROW = 3
        private const val SHOW_ROW = 4
        private const val SCROLL_ROW = 5
    }

    private val mXPosition: Int = viewHolder.adapterPosition

    init {
        initialize()
    }

    private fun initialize() {
        createMenuItem()
        changeMenuItemVisibility()
        setOnMenuItemClickListener(this)
    }

    private fun createMenuItem() {
        val context: Context = mTableView.context
        menu.add(Menu.NONE, ASCENDING, 0, context.getString(R.string.sort_ascending))
        menu.add(Menu.NONE, DESCENDING, 1, context.getString(R.string.sort_descending))

        // Uncomment below if you plan to use row actions
        /*
        menu.add(Menu.NONE, HIDE_ROW, 2, context.getString(R.string.hiding_row_sample))
        menu.add(Menu.NONE, SHOW_ROW, 3, context.getString(R.string.showing_row_sample))
        menu.add(Menu.NONE, SCROLL_ROW, 4, context.getString(R.string.scroll_to_row_position))
        menu.add(Menu.NONE, SCROLL_ROW, 5, "Change Width")
        */
    }

    private fun changeMenuItemVisibility() {
        when (mTableView.getSortingStatus(mXPosition)) {
            SortState.UNSORTED -> {
                // Keep all visible
            }
            SortState.DESCENDING -> {
                menu.findItem(DESCENDING)?.isVisible = false
            }
            SortState.ASCENDING -> {
                menu.findItem(ASCENDING)?.isVisible = false
            }
            else -> {}
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            ASCENDING -> mTableView.sortColumn(mXPosition, SortState.ASCENDING)
            DESCENDING -> mTableView.sortColumn(mXPosition, SortState.DESCENDING)
            /*
            HIDE_ROW -> mTableView.hideRow(5)
            SHOW_ROW -> mTableView.showRow(5)
            SCROLL_ROW -> mTableView.scrollToRowPosition(5)
            */
        }
        return true
    }
}
