package io.github.devzwy.mdhelper.util

import io.github.devzwy.mdhelper.data.ExtraData
import io.github.devzwy.mdhelper.data.MdControl

fun HashMap<String, Any?>.toRowDataList() = arrayListOf<MdControl>().also {
    forEach { (key, rowData) ->
        if (rowData != null) {
            if (rowData is List<*> && rowData.isNotEmpty() && rowData[0] is ExtraData) {
                //附件
                val arr = arrayListOf<ExtraData?>()
                rowData.forEach {
                    arr.add(it as? ExtraData)
                }
                it.add(MdControl(controlId = key, value = null, valueType = 2, controlFiles = arr))
            }else{
                it.add(MdControl(controlId = key, value = rowData))
            }
        } else {
            it.add(MdControl(controlId = key, value = null))
        }
    }
}

