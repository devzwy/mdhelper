import io.github.devzwy.mdhelper.MDHelper
import io.github.devzwy.mdhelper.data.*
import io.github.devzwy.mdhelper.utils.MdDataControl
import io.github.devzwy.mdhelper.utils.MdFilterControl
import org.junit.jupiter.api.Assertions.assertThrows

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 单元测试 针对全部Api测试
 * 全部通过表示正常
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MDHelperTest {

    @Test
    @Order(3)
    fun dataTest() {

        //todo 修改为自己的
        val baseUrl = ""
        val appKey = ""
        val tableId = ""
        val sign = ""


        val instance = MDHelper.getInstance()

//        instance.disableLog()

        instance.addBaseUrl("my", baseUrl)

        instance.addAppConfig("我的应用", appKey, sign)

        assertNotNull(instance.getAppInfo())

        val tableInfo = instance.getTableInfo(tableId = tableId)

        assertNotNull(tableInfo)

        //写入记录
        val rowId = instance.insertRow(
            tableId = tableId,
            data = MdDataControl.Builder()
                .addControl("658e7f60dd2e9988fc03dc26", "1111111")
                .addControl("658e7f75dd2e9988fc03dc31", "你好")
                .addMulti("658e8870dd2e9988fc03dc57", arrayListOf("SGVsbG8sIEJhc2U2NA=="), arrayListOf("111.list"), MuliDataType.BASE64)
                .addOption("65b0b8ef384db183c9a18342", "已打印2", OptionDataType.ADD)
                .build()
        )
        assertNotNull(rowId)

        assertTrue {
            instance.updateRow(
                tableId = tableId, rowId = rowId, data = MdDataControl.Builder()
                    .addControl("658e7f60dd2e9988fc03dc26", "更新后的数据")
                    .addControl("658e7f75dd2e9988fc03dc31", "更新后的数据2")
                    .addMulti("658e8870dd2e9988fc03dc57", arrayListOf("SGVsbG8sIEJhc2U2NA=="), arrayListOf("更新后的数据.list"), MuliDataType.BASE64)
                    .addOption("65b0b8ef384db183c9a18342", "更新后的数据", OptionDataType.ADD)
                    .build()
            )
        }

        val a = instance.getRow(tableId = tableId, rowId = rowId, clazz = AAAA::class.java)
        assertNotNull(a)


        instance.deleteRow(tableId = tableId, rowId = rowId)

        val rowCount = instance.insertRows(
            tableId = tableId,
            dataList = arrayListOf(
                MdDataControl.Builder()
                    .addControl("658e7f60dd2e9988fc03dc26", "1111111")
                    .addControl("658e7f75dd2e9988fc03dc31", "你好")
                    .addMulti("658e8870dd2e9988fc03dc57", arrayListOf("SGVsbG8sIEJhc2U2NA=="), arrayListOf("222.list"), MuliDataType.BASE64)
                    .addOption("65b0b8ef384db183c9a18342", "已打印2", OptionDataType.ADD)
                    .build(),
                MdDataControl.Builder()
                    .addControl("658e7f60dd2e9988fc03dc26", "222")
                    .addControl("658e7f75dd2e9988fc03dc31", "你好啊啊啊啊啊")
                    .addMulti("658e8870dd2e9988fc03dc57", arrayListOf("SGVsbG8sIEJhc2U2NA=="), arrayListOf("333.list"), MuliDataType.BASE64)
                    .addOption("65b0b8ef384db183c9a18342", "已打印")
                    .build()
            )
        )

        assertEquals(rowCount, 2)


        val resultData = instance.getRows(
            tableId = tableId,
            filter = MdFilterControl.Builder()
                .addFilter("658e7f60dd2e9988fc03dc26", "222", FilterTypeEnum.EQ)
                .addFilter("65b0b8ef384db183c9a18342", "更新后的数据", FilterTypeEnum.NE)
                .build(),
            clazz = AAAA::class.java
        )



        println(resultData.rows[0].`658e7f60dd2e9988fc03dc26`)
    }

    @Test
    @Order(2)
    fun addOrRemoveAppConfig() {
        val instance = MDHelper.getInstance()

        assertThrows(Exception::class.java) {
            instance.getAppConfig()
        }

        assertThrows(Exception::class.java) {
            instance.getAppConfig(null)
        }

        assertThrows(Exception::class.java) {
            instance.getAppConfig("")
        }

        assertThrows(Exception::class.java) {
            instance.getAppConfig("not")
        }

        val key1 = "我的应用"
        val appKey1 = "123456"
        val sign1 = "666666"

        val key2 = "我的应用2"
        val appKey2 = "654321"
        val sign2 = "111111"

        instance.addAppConfig(key1, appKey1, sign1)

        assertEquals(instance.getAppConfig().appKey, appKey1)

        instance.addAppConfig(key2, appKey2, sign2)

        assertEquals(instance.getAppConfig().appKey, appKey1)

        assertEquals(instance.getAppConfig(key2).appKey, appKey2)

        assertEquals(instance.getAllAppConfigs().size, 2)

        instance.removeAppByConfigKey(key1)

        assertEquals(instance.getAllAppConfigs().size, 1)

        instance.removeAppByConfigKey(key2)
        instance.removeAppByConfigKey("unkonwKey")

        assertThrows(Exception::class.java) {
            instance.getAppConfig()
        }

        instance.removeAllAppConfigs()

        assertEquals(instance.getAllAppConfigs().size, 0)
    }

    /**
     * baseurl的增删查
     */
    @Test
    @Order(1)
    fun addOrRemoveBaseUrl() {
        val instance = MDHelper.getInstance()

        assertThrows(Exception::class.java) {
            instance.getBaseUrl()
        }

        assertThrows(Exception::class.java) {
            instance.getBaseUrl(null)
        }

        assertThrows(Exception::class.java) {
            instance.getBaseUrl("")
        }

        assertThrows(Exception::class.java) {
            instance.getBaseUrl("not")
        }

        val key1 = "configKey"
        val url1 = "https://www.baidu.com"

        val key2 = "configKey2"
        val url2 = "https://www.baidu.com2"

        instance.addBaseUrl(key1, url1)

        assertEquals(instance.getBaseUrl(), url1)

        instance.addBaseUrl(key2, url2)

        assertEquals(instance.getBaseUrl(), url1)

        assertEquals(instance.getBaseUrl(key2), url2)

        assertEquals(instance.getAllBaseUrls().size, 2)

        instance.removeBaseUrlByKey(key1)

        assertEquals(instance.getAllBaseUrls().size, 1)

        instance.removeBaseUrlByKey(key2)
        instance.removeBaseUrlByKey("unkonwKey")

        assertThrows(Exception::class.java) {
            instance.getBaseUrl()
        }

        instance.removeAllBaseUrls()

        assertEquals(instance.getAllBaseUrls().size, 0)
    }

    /**
     * 单例实例化
     */
    @Test
    @Order(0)
    fun testGetInstance() {
        val instance1 = MDHelper.getInstance()
        val instance2 = MDHelper.getInstance()

        // 检查获取的实例是否非空
        assertNotNull(instance1)
        assertNotNull(instance2)

        // 检查获取的实例是否是同一个实例
        assert(instance1 === instance2)
    }


    class AAAA : Row() {
        var `658e7f60dd2e9988fc03dc26`: String = ""
    }
}
