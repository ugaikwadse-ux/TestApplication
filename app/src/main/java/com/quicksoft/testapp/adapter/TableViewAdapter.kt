package com.quicksoft.testapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.evrencoskun.tableview.sort.SortState
import com.quicksoft.testapp.R
import com.quicksoft.testapp.holder.CellViewHolder
import com.quicksoft.testapp.holder.ColumnHeaderViewHolder
import com.quicksoft.testapp.holder.RowHeaderViewHolder
import com.quicksoft.testapp.model.Cell
import com.quicksoft.testapp.model.ColumnHeader
import com.quicksoft.testapp.model.RowHeader

class TableViewAdapter(
    private val tableViewModel: TableViewModel
) : AbstractTableAdapter<ColumnHeader, RowHeader, Cell>() {

    companion object {
        private val LOG_TAG = TableViewAdapter::class.java.simpleName
    }

    override fun onCreateCellViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = inflater.inflate(
            R.layout.table_view_cell_layout,
            parent,
            false
        )
        return CellViewHolder(layout)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: Cell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val viewHolder = holder as CellViewHolder
        viewHolder.setCell(cellItemModel)
    }

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_view_column_header_layout, parent, false)
        return ColumnHeaderViewHolder(layout, tableView)
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        val columnHeaderViewHolder = holder as ColumnHeaderViewHolder
        columnHeaderViewHolder.setColumnHeader(columnHeaderItemModel)
    }

    override fun onCreateRowHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_view_row_header_layout, parent, false)
        return RowHeaderViewHolder(layout)
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    ) {
        val rowHeaderViewHolder = holder as RowHeaderViewHolder
        rowHeaderViewHolder.rowHeaderTextView.text =
            rowHeaderItemModel?.data?.toString() ?: ""
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        val corner = LayoutInflater.from(parent.context)
            .inflate(R.layout.table_view_corner_layout, parent, false)

        corner.setOnClickListener {
            val sortState = tableView.rowHeaderSortingStatus
            if (sortState != SortState.ASCENDING) {
                Log.d(LOG_TAG, "Order Ascending")
                tableView.sortRowHeader(SortState.ASCENDING)
            } else {
                Log.d(LOG_TAG, "Order Descending")
                tableView.sortRowHeader(SortState.DESCENDING)
            }
        }
        return corner
    }

    override fun getColumnHeaderItemViewType(position: Int): Int = 0

    override fun getRowHeaderItemViewType(position: Int): Int = 0

    override fun getCellItemViewType(column: Int): Int = 0
}
