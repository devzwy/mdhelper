package io.github.devzwy.mdhelper.util

import io.github.devzwy.mdhelper.data.RowData

fun HashMap<String, Any?>.toRowDataList() = arrayListOf<RowData>().also {
    forEach { (t, u) ->
        it.add(RowData(t, u))
    }
}

