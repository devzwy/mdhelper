import cn.uexpo.*
import com.alibaba.fastjson2.annotation.JSONField


fun main() {
    MDHelper.Builder().baseUrl("https://code.umice.cn").httpTimeOut(5 * 1000)
        .addConfig("test1", "", "", "").build(false)
        .apply {
            println("开始测试函数...")
//            println("获取应用信息...")
//            //获取应用详情
            println(getAppInfo(appName = "test1"))

//            println("创建表...")
//            //创建表
//            println(
//                createTable(
//                    tableName = "测试1", tableAlias = "cs1", filed = arrayOf(
//                        CreateTableData(controlName = "测试文本字段", type = DataType.TEXT.value),
//                        CreateTableData(controlName = "测试文本字段带别名", alias = "t2", type = DataType.TEXT.value),
//                    )
//                )
//            )

//            println("获取表结构信息...")
//            //获取表结构信息
//            println(getTableInfo(worksheetId = "64d31d096246a77574313d7b"))
//
//            println("获取表数据")
//            val aa = getData<MDRowData<BBB>>(worksheetId = "64d31d096246a77574313d7b")
//            aa!!.rows.forEach {
//                println(it.name + " " + it.rowid)
//            }
////            println()
//
////            println("写入一条记录...")
////            println(insertRow(worksheetId = "64d31d096246a77574313d7b", data = arrayOf(RowData("64d31df66246a77574313d93", "1111")), triggerWorkflow = false))
//
//            println("获取行记录...")
//
//            val aaa = getRow<BBB>(worksheetId = "64d31d096246a77574313d7b", rowId = "a4ddea0b-5ae9-47fb-942d-44afe7d5046e")
//            println(aaa!!.rowid)

//            println(updateRow(worksheetId = "64d31d096246a77574313d7b", data = arrayOf(RowData("64d31df66246a77574313d93", "测试")), triggerWorkflow = false, rowId = "a4ddea0b-5ae9-47fb-942d-44afe7d5046e"))
//
//            println(delRow(worksheetId = "64d31d096246a77574313d7b", triggerWorkflow = false, rowId = "a4ddea0b-5ae9-47fb-942d-44afe7d5046e"))

            println(getWorksheetCount(worksheetId = "64d31d096246a77574313d7b"))

        }
}

data class BBB(
    @JSONField(name = "64d31df66246a77574313d89")
    val name: String? = null
) : MDRow()