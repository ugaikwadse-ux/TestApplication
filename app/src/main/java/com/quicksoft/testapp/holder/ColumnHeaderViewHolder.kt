package com.quicksoft.testapp.holder

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.evrencoskun.tableview.ITableView
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder
import com.evrencoskun.tableview.sort.SortState
import com.quicksoft.testapp.R
import com.quicksoft.testapp.model.ColumnHeader

class ColumnHeaderViewHolder(
    itemView: View,
    private val tableView: ITableView
) : AbstractSorterViewHolder(itemView) {

    private val columnHeaderContainer: LinearLayout = itemView.findViewById(R.id.column_header_container)
    private val columnHeaderTextView: TextView = itemView.findViewById(R.id.column_header_textView)
    private val columnHeaderSortButton: ImageButton = itemView.findViewById(R.id.column_header_sortButton)
    private val sortButtonClickListener = View.OnClickListener {
        tableView.let {
            when (sortState) {
                SortState.ASCENDING -> it.sortColumn(adapterPosition, SortState.DESCENDING)
                SortState.DESCENDING -> it.sortColumn(adapterPosition, SortState.ASCENDING)
                else -> it.sortColumn(adapterPosition, SortState.DESCENDING)
            }
        }
    }

    init {
        // Set click listener to the sort button
        columnHeaderSortButton.setOnClickListener(sortButtonClickListener)
    }

    fun setColumnHeader(columnHeader: ColumnHeader?) {
        columnHeaderTextView.text = columnHeader?.data?.toString()

        // Auto resize for TableView columns
        columnHeaderContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        columnHeaderTextView.requestLayout()
    }


    override fun onSortingStatusChanged(sortState: SortState) {
        Log.e(
            LOG_TAG, " + onSortingStatusChanged: x: $adapterPosition, " +
                    "old state: $sortState, visibility: ${columnHeaderSortButton.visibility}"
        )

        super.onSortingStatusChanged(sortState)

        // Auto resize
        columnHeaderContainer.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT

        controlSortState(sortState)

        Log.e(
            LOG_TAG, " - onSortingStatusChanged: x: $adapterPosition, " +
                    "state: $sortState, visibility: ${columnHeaderSortButton.visibility}"
        )

        columnHeaderTextView.requestLayout()
        columnHeaderSortButton.requestLayout()
        columnHeaderContainer.requestLayout()
        itemView.requestLayout()
    }

    private fun controlSortState(sortState: SortState) {
        when (sortState) {
            SortState.ASCENDING -> {
                columnHeaderSortButton.visibility = View.VISIBLE
                columnHeaderSortButton.setImageResource(R.drawable.ic_down)
            }
            SortState.DESCENDING -> {
                columnHeaderSortButton.visibility = View.VISIBLE
                columnHeaderSortButton.setImageResource(R.drawable.ic_up)
            }
            else -> {
                columnHeaderSortButton.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        private val LOG_TAG = ColumnHeaderViewHolder::class.java.simpleName
    }
}
