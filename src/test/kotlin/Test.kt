import io.github.devzwy.mdhelper.MDHelper
import io.github.devzwy.mdhelper.data.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

/**
 * 测试流程：
 * 会在配置的应用中创建一个随机的工作表进行增删改查操作。
 * 中途没有报错就是测试过了
 * 不要挂日志打印器就不会有日志了
 * 测试完成后手动删除测试的工作表
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class Test {


    companion object {

        //TODO 修改这里的参数进行测试
        private val BASE_URL = "https://xx.xx.cn"
        //TODO 写已经存在的应用名称进行测试
        private val APP_NAME = "未命名应用"
        private val APP_KEY = "c9bxxx9exxxxx41ac6"
        private val APP_SIGN = "xxxxxx=="
        //TODO 修改上面的参数进行测试

        private val rInt = Random.nextInt(100, 999)

        private val testTableName = "测试新建工作表-${rInt}"
        private val testTableAlias = "new_test_${rInt}"


        private var tmpTableId = ""
        private var tmpRowId = ""

        private val KEY_ACCOUNT = "account"
        private val KEY_PSW = "password"


        private val testUserAccount = "zhangsan"
        private val testUserPassword = 123456
        private val testUserNewPassword = 666666

        private val log = object : ILoggerFactory {
            override fun log(any: Any?) {
                System.out.println(any)
            }

            override fun err(any: Any?) {
                System.err.println(any)
            }
        }

        /**
         * 实例构造
         */
        private val mdHelper by lazy {
            MDHelper.Builder()
                .baseUrl(BASE_URL)
                .setLogger(log)
                .addConfig(APP_NAME, APP_KEY, APP_SIGN)
                .httpTimeOut(5 * 1000)
                .build(false)
        }

        @JvmStatic
        @AfterAll
        fun end(): Unit {
            println("测试完成，请手动删除：${testTableName}")
        }


    }

    @Test
    @Order(1)
    fun test_appExists() {
        assert(mdHelper.appExists(APP_NAME))
        println("检测应用是否存在 通过")
    }


    @Test
    @Order(2)
    fun test_addConfig() {
        mdHelper.addConfig("1", "2", "3")
        assert(mdHelper.appExists("1"))
        println("临时添加配置 通过")
    }

    @Test
    @Order(3)
    fun test_updateConfig() {
        mdHelper.updateConfig("1", "4", "5")
        assert(mdHelper.appExists("1"))
        println("更新配置 通过")
    }

    @Test
    @Order(4)
    fun test_delConfig() {
        mdHelper.delConfig("1")
        assert(!mdHelper.appExists("1"))
        println("删除配置 通过")
    }


    @Test
    @Order(5)
    fun test_getAppInfo() {
        val appInfo = mdHelper.getAppInfo(APP_NAME)
        assertEquals(appInfo!!.name, APP_NAME)
        println("获取应用详情 通过")
    }


    @Test
    @Order(5)
    fun test_createTable() {
        tmpTableId = mdHelper.createTable(
            appName = APP_NAME,
            tableName = testTableName, tableAlias = testTableAlias, filed = arrayOf(
                CreateTableData(controlName = "账号", alias = KEY_ACCOUNT, type = DataType.TEXT.value, required = true),
                CreateTableData(controlName = "密码", alias = KEY_PSW, type = DataType.NUMBER.value, required = true)
            )
        )!!
        assertNotEquals(tmpTableId, "")
        println("创建工作表 通过")
    }

    @Test
    @Order(6)
    fun test_getTableInfo() {
        val table = mdHelper.getTableInfo(appName = APP_NAME, worksheetId = tmpTableId)!!
        assertEquals(tmpTableId, table.worksheetId)
        println("获取工作表结构 通过")
    }

    @Test
    @Order(7)
    fun test_insertRow() {
        tmpRowId = mdHelper.insertRow(
            appName = APP_NAME, worksheetId = tmpTableId, triggerWorkflow = false, data = hashMapOf(
                KEY_ACCOUNT to testUserAccount,
                KEY_PSW to testUserPassword
            )
        )!!
        assertNotEquals(tmpRowId, "")
        println("插入行记录 通过")
    }

    @Test
    @Order(8)
    fun test_getRow() {
        val user = mdHelper.getRow<User>(appName = APP_NAME, worksheetId = tmpTableId, rowId = tmpRowId)!!
        assertEquals(testUserAccount, user.account)
        println("获取行记录 通过")
    }

    @Test
    @Order(9)
    fun test_updateRow() {
        val isUpdateSucc = mdHelper.updateRow(
            appName = APP_NAME, worksheetId = tmpTableId, rowId = tmpRowId, triggerWorkflow = false, data = hashMapOf(
                KEY_PSW to testUserNewPassword
            )
        )!!
        assert(isUpdateSucc)
        println("更新行记录 通过")
    }


    @Test
    @Order(10)
    fun test_getData() {
        val list = mdHelper.getData<MDRowData<User>>(
            appName = APP_NAME, worksheetId = tmpTableId, pageIndex = 0, pageSize = 10, filter = FilterData(
                sortId = KEY_ACCOUNT,
                isAsc = true,
                notGetTotal = false,
                arrayListOf(
                    FilterBean.Builder(KEY_ACCOUNT, testUserAccount)
                        .typeOf(DataType.TEXT.value)
                        .filterOf(FilterType.Eq.value)
                        .buildAnd(),
                    FilterBean.Builder(KEY_PSW, testUserNewPassword)
                        .typeOf(DataType.NUMBER.value)
                        .filterOf(FilterType.Eq.value)
                        .buildAnd()
                )
            )
        )!!
        assert(list.total > 0 && list.rows.last().account == testUserAccount)
        println("更新列表 通过")
    }

    @Test
    @Order(11)
    fun test_getTableInfoOfTmp() {
        assertNotNull(mdHelper.getTableInfoOfTmp(url = BASE_URL, appKey = APP_KEY, sign = APP_SIGN, worksheetId = tmpTableId))
        println("临时获取表结构 通过")
    }

    @Test
    @Order(12)
    fun test_delRow() {
        val isDelSucc = mdHelper.delRow(appName = APP_NAME, worksheetId = tmpTableId, rowId = tmpRowId)!!
        assert(isDelSucc)
        println("删除行 通过")
    }




    data class User(
        val account: String, val password: Int
    ) : MDRow()


}