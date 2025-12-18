package com.quicksoft.testapp.model

import com.evrencoskun.tableview.filter.IFilterableModel
import com.evrencoskun.tableview.sort.ISortableModel

open class Cell(
    private val cellId: String,
    val data: Any?
) : ISortableModel, IFilterableModel {

    private val filterKeyword: String = data?.toString() ?: ""

    // Implement interface manually (no auto getter conflict)
    override fun getId(): String = cellId

    override fun getContent(): Any? = data

    override fun getFilterableKeyword(): String = filterKeyword
}