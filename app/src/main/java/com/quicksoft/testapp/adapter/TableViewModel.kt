package com.quicksoft.testapp.adapter

import android.content.Context
import com.quicksoft.testapp.model.ApiResponse
import com.quicksoft.testapp.model.Cell
import com.quicksoft.testapp.model.ColumnHeader
import com.quicksoft.testapp.model.RowHeader

class TableViewModel(
    context: Context,
    private val apiResponse: ApiResponse
) {
    /** Convert API response to Map */
    private fun toFlatMap(): LinkedHashMap<String, String> {
        val map = linkedMapOf<String, String>()

        map["ERROR"] = apiResponse.error.toString()
        map["MESSAGE"] = apiResponse.message as String
        map["IP"] = apiResponse.ip

        // Nested request object
        map["Member ID"] = apiResponse.request.memberId as String
        map["API Password"] = apiResponse.request.apiPassword as String
        map["API Pin"] = apiResponse.request.apiPin as String
        map["Number"] = apiResponse.request.number as String

        return map
    }

    /** Column headers = JSON keys */
    fun getColumnHeaderList(): List<ColumnHeader> =
        toFlatMap().keys.mapIndexed { index, key ->
            ColumnHeader(index.toString(), key)
        }

    /** Single row */
    fun getRowHeaderList(): List<RowHeader> =
        listOf(RowHeader("0", "1"))

    /** Cell values = JSON values */
    fun getCellList(): List<List<Cell>> {
        val cells = toFlatMap().values.mapIndexed { colIndex, value ->
            Cell("0-$colIndex", value)
        }
        return listOf(cells)
    }
}
