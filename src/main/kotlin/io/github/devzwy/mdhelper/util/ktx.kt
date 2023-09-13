package io.github.devzwy.mdhelper.util

import io.github.devzwy.mdhelper.data.ExtraData
import io.github.devzwy.mdhelper.data.MDRowData
import io.github.devzwy.mdhelper.data.MdControl
import io.github.devzwy.mdhelper.data.RowData

fun HashMap<String, RowData?>.toRowDataList() = arrayListOf<MdControl>().also {
    forEach { (key, rowData) ->
        if (rowData != null) {
            if (rowData.isExtra) {
                //附件
                it.add(MdControl(controlId = key, value = null, valueType = 2, controlFiles = rowData.value as List<ExtraData>))
            }
        } else {
            it.add(MdControl(controlId = key, value = rowData?.value))
        }
    }
}

