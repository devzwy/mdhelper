package io.github.devzwy.mdhelper.data

data class MDBaseResult<T>(
    val `data`: T, val error_code: Int, val error_msg: String = "", val success: Boolean
)

data class AppInfo(
    val appId: String, val desc: String, val name: String, val projectId: String, val sections: List<Section>
)

data class Section(
    val items: List<Table>, val name: String, val sectionId: String
)

data class Table(
    val alias: String, val iconUrl: String, val id: String, val name: String, val status: Int, val type: Int
)

data class CreateTableData(
    //名称
    val controlName: String,
    //字段别名
    val alias: String? = null,
    /**
     * 字段类型，必须通过[io.github.devzwy.DatType] 进行构造
     */
    val type: Int,
    //是否必须
    val required: Boolean = false,
    //选项 单选或多选类型时填写
    val options: List<MDOption>? = null,
    //单选或多选时默认选中的值
    val enumDefault: Int? = null
)

data class MDTableInfo(
    val controls: List<MDTableControl>, val name: String, val views: List<MDView>, val worksheetId: String
)

data class MDTableControl(
    val alias: String? = null,
    val checked: Boolean? = null,
    val controlId: String,
    val controlName: String,
    val default: String? = null,
    val desc: String? = null,
    val disabled: Boolean? = null,
    val dot: Int? = null,
    val enumDefault: Int? = null,
    val lastEditTime: String? = null,
    val options: List<MDOption>? = null,
    val required: Boolean? = null,
    val type: Int,
    val unique: Boolean
)

data class MDView(
    val name: String, val viewId: String
)

data class MDOption(
    val color: String? = null, val index: Int, val isDeleted: Boolean? = null, val key: String? = null, val score: Double? = null, val value: String
)


class FilterData(
    //排序字段ID
    val sortId: String? = null,
    //是否升序
    val isAsc: Boolean? = null,
    //是否不统计总行数以提高性能
    val notGetTotal: Boolean? = null,
    //筛选条件
    val filters: List<FilterBean>? = null
)

//行数据
data class MdControl(
    //字段ID
    val controlId: String,
    //字段值
    val value: Any?,
    //字段值类型 提交值类型，1=外部文件链接，2=文件流字节编码 base64格式 字符串 (默认1,为1时 外部链接放在value参数中，为2时 文件流base64信息放在controlFiles参数中
    val valueType: Int = 1,
    //valueType 2 时 base64的值放在这里
    val controlFiles: List<ExtraData> = arrayListOf()
)

data class RowData(
    /**
     * 字段值
     * isExtra为true时这里必须为数组
     */
    val value: Any?,
    /**
     * 是否附件，附件时 value的值需要使用[io.github.devzwy.mdhelper.data.ExtraData]进行构建，并且为数组
     */
    val isExtra: Boolean = false
)

data class ExtraData(val baseFile: String, val fileName: String)

class FilterBean private constructor(val controlId: String, val value: Any?, val values: Any?, val dataType: Int, val spliceType: Int, val filterType: Int) {
    class Builder(
        /**
         * 字段名称
         */
        private val controlId: String,
        /**
         * 单个值
         */
        private val value: Any? = null,
        /**
         * 多个值
         */
        val values: Any? = null,
        /**
         * 筛选类型 只能一种
         */
        val spliceType: SpliceType = SpliceType.AND
    ) {

        //字段类型
        private var dataType: Int = 0

        //筛选类型
        private var filterType: Int = 0

        /**
         * 字段的类型 使用[io.github.devzwy.mdhelper.data.DataType]构造
         */
        fun typeOf(dateType: Int) = apply { this.dataType = dateType }

        /**
         * 字段的类型 使用[io.github.devzwy.mdhelper.data.FilterType]构造
         */
        fun filterOf(filterType: Int) = apply { this.filterType = filterType }

        /**
         * 与下一组条件的关系为AND拼接
         */
        fun build() = FilterBean(controlId, value, values, dataType, spliceType.value, filterType)

    }
}

interface ILoggerFactory {
    fun log(any: Any?)
    fun err(any: Any?)
}

open class MDRowData<T>(
    var rows: List<T> = arrayListOf(),
    var total: Int = 0
)


open class MDRow {
    var allowdelete: Boolean = false
    var autoid: Int? = null
    var caid: Caid? = null
    var controlpermissions: String? = null
    var ctime: String = ""
    var ownerid: Ownerid? = null
    var rowid: String = ""
    var utime: String = ""
}

data class Caid(
    val accountId: String,
    val avatar: String,
    val fullname: String,
    val status: Int
)

data class Ownerid(
    val accountId: String,
    val avatar: String,
    val fullname: String,
    val status: Int
)