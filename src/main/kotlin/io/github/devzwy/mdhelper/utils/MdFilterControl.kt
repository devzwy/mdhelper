package io.github.devzwy.mdhelper.utils

import FilterTypeEnum
import SpliceTypeEnum
import io.github.devzwy.mdhelper.data.DataTypeEnum

class MdFilterControl private constructor(val filters: List<HashMap<String, Any?>>) {
    class Builder {

        private val list = arrayListOf<HashMap<String, Any?>>()


        /**
         * 添加过滤字段字段
         * [controlId] 字段
         * [value] 值
         * [filterType] 过滤类型 使用[FilterTypeEnum]构造
         * [dataType] 控件类型 使用[DataTypeEnum]构造，默认[DataTypeEnum.TEXT_SINGLE_LINE]文本类型
         * [spliceType] 拼接方式 使用[SpliceTypeEnum]构造，默认[SpliceTypeEnum.AND]
         */
        fun addFilter(controlId: String, value: String, filterType: FilterTypeEnum, dataType: DataTypeEnum = DataTypeEnum.TEXT_SINGLE_LINE, spliceType: SpliceTypeEnum = SpliceTypeEnum.AND): Builder {
            list.add(hashMapOf("controlId" to controlId, "dataType" to dataType.value, "spliceType" to spliceType.value, "filterType" to filterType.value, "value" to value))
            return this
        }

        fun build(): MdFilterControl {
            return MdFilterControl(list)
        }
    }

}