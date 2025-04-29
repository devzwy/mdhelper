package io.github.devzwy.mdhelper.v2

import com.alibaba.fastjson2.TypeReference
import io.github.devzwy.mdhelper.MdLog
import io.github.devzwy.mdhelper.data.MdAppInfo
import io.github.devzwy.mdhelper.data.MdTableInfo
import io.github.devzwy.mdhelper.utils.MDUtil.toJson
import io.github.devzwy.mdhelper.utils.MdDataControl
import io.github.devzwy.mdhelper.v2.utils.HttpUtils
import io.github.devzwy.mdhelper.v2.utils.Signature
import io.github.devzwy.mdhelper.v2.utils.Utils
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * v2对工具类的升级封装,内部不再维护使用的明道云类型，调用者外部自行控制不同对象初始化到不同的明道云类型
 */
class MdHelper {

    // api请求域名或IP 如:https://api.mingdao.com
    private lateinit var baseUrl: String

    //组织编号(ID) 组织管理-组织-基础信息处获取
    private lateinit var groupNo: String

    //组织密钥-Appkey 组织管理-集成-其他-开放接口处获取
    private lateinit var appKey: String

    //组织密钥-SecretKey 组织管理-集成-其他-开放接口处获取
    private lateinit var secretKey: String

    //配置项
    private lateinit var config: Config

    //存储app签名的临时数据，使用一次后不再通过明道云服务器api获取，直接从这里读取就可以
    private val appSignCache = ConcurrentHashMap<String, AppSignData>()


    // ============================================= 对外开放的函数 =============================================

    /**
     * 初始化 调用时内部会自动拉取用户列表以确认配置的合法性，接口调用通过即为初始化完成
     * @param baseUrl api请求域名或IP 如:https://api.mingdao.com
     * @param groupNo 组织编号(ID) 组织管理-组织-基础信息处获取
     * @param appKey 组织密钥-Appkey 组织管理-集成-其他-开放接口处获取
     * @param secretKey 组织密钥-SecretKey 组织管理-集成-其他-开放接口处获取
     * @param config 配置项，一般情况无需单独配置
     */
    fun init(baseUrl: String, groupNo: String, appKey: String, secretKey: String, config: Config = Config()) {
        this.baseUrl = baseUrl
        this.groupNo = groupNo
        this.appKey = appKey
        this.secretKey = secretKey
        this.config = config
        if (this.config.debugEnabled) {
            MdLog.enable()
        } else {
            MdLog.disable()
        }
        HttpUtils.setTimeout(this.config.httpTimeout)
    }

    /**
     * 读取所有应用
     */
    fun getAppList(): List<MdAppInfo> {
        Date().time.let {
            HttpUtils.doGet(
                url = getRequestUrl(this.config.appListUrl),
                requestMap = hashMapOf("sign" to Utils.getSign(this.appKey, this.secretKey, it), "appKey" to appKey, "timestamp" to "${it}", "projectId" to groupNo),
            ).let { respStr ->
                Utils.parseApiResponse(respStr, object : TypeReference<ApiResponse<List<MdAppInfo>>>() {}).let {
                    if (it != null) {

                        if (it.error_code == 1) {
                            return it.data ?: arrayListOf()
                        } else {
                            MdLog.error("获取应用列表异常,${it.error_msg}")
                        }
                    } else {
                        MdLog.error("数据解析异常,${respStr} 无法解析为 ApiResponse<List<AppInfo>>")
                    }
                }
            }
        }
        return arrayListOf()
    }

    /**
     * 获取指定应用的签名数据，优先缓存读取，只返回一对带有所有权限的密钥
     * @param appId 应用ID，可通过[getAppList]返回或在明道应用页面查看ID
     */
    fun getAppSignData(appId: String): AppSignData? {

        //从缓存中读取
        if (appSignCache.contains(appId)) {
            MdLog.debug("从缓存返回 ${appId} 的签名数据")
            return appSignCache[appId]!!
        }

        Date().time.let { time ->

            HttpUtils.doGet(
                url = getRequestUrl(this.config.appAuthUrl),
                requestMap = hashMapOf("sign" to Signature.getSignature(appKey, secretKey, time), "appKey" to appKey, "timestamp" to "${time}", "projectId" to groupNo, "appId" to appId),
            ).let { respStr ->
                Utils.parseApiResponse(respStr, object : TypeReference<ApiResponse<List<AppSignData>>>() {}).let {
                    if (it != null) {

                        if (it.error_code == 1) {
                            val appSignData = it.data?.find { it.type == 1 }
                            if (appSignData != null) {
                                appSignCache[appId] = appSignData
                                return appSignData
                            } else {
                                MdLog.error("获取应用 ${appId} 签名信息异常，本应用下不存在密钥或密钥权限不足")
                            }
                        } else {
                            MdLog.error("获取应用 ${appId} 签名信息异常,${it.error_msg}")
                        }

                    } else {
                        MdLog.error("数据解析异常,${respStr} 无法解析为 ApiResponse<List<AppSignData>>")
                    }
                }
            }
        }
        return null
    }


    /**
     * 获取应用详细信息，包含内部的表信息
     * @param appId 应用ID，可通过[getAppList]返回或在明道应用页面查看ID
     */
    fun getAppInfo(appId: String): MdAppInfo? {
        val appSignCache = appSignCache.get(appId) ?: getAppSignData(appId) ?: return null
        HttpUtils.doGet(
            url = getRequestUrl(this.config.appInfoUrl),
            requestMap = hashMapOf("sign" to appSignCache.sign, "appKey" to appSignCache.appKey),
        ).let { respStr ->
            Utils.parseApiResponse(respStr, object : TypeReference<ApiResponse<MdAppInfo>>() {}).let {
                if (it != null) {

                    if (it.error_code == 1) {
                        return it.data
                    } else {
                        MdLog.error("获取应用 ${appId} 的详细信息失败,${it.error_msg}")
                    }

                } else {
                    MdLog.error("数据解析异常,${respStr} 无法解析为 ApiResponse<MdAppInfo>")
                }
            }
        }
        return null
    }

    /**
     * 获取表结构
     * @param appId 应用ID，可通过[getAppList]返回或在明道应用页面查看ID
     * @param tableId 表ID
     */
    fun getTableInfo(appId: String, tableId: String): MdTableInfo? {
        val appSignCache = appSignCache.get(appId) ?: getAppSignData(appId) ?: return null
        HttpUtils.doPost(
            url = getRequestUrl(this.config.tableInfoUrl),
            requestMap = hashMapOf("sign" to appSignCache.sign, "appKey" to appSignCache.appKey, "worksheetId" to tableId),
        ).let { respStr ->
            Utils.parseApiResponse(respStr, object : TypeReference<ApiResponse<MdTableInfo>>() {}).let {
                if (it != null) {

                    if (it.error_code == 1) {
                        return it.data
                    } else {
                        MdLog.error("获取 ${appId} 的表结构失败,${it.error_msg}")
                    }

                } else {
                    MdLog.error("数据解析异常,${respStr} 无法解析为 ApiResponse<MdTableInfo>")
                }
            }
        }
        return null
    }

    /**
     * 插入单行记录
     * @param appId 应用ID，可通过[getAppList]返回或在明道应用页面查看ID
     * @param tableId 表ID
     * [data] 写入的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入的行ID
     */
    fun insertRow(appId: String, tableId: String, data: MdDataControl, triggerWorkflow: Boolean = true): String? {

        val appSignCache = appSignCache.get(appId) ?: getAppSignData(appId) ?: return null

        HttpUtils.doPost(
            url = getRequestUrl(this.config.addRowUrl),
            requestMap = hashMapOf("appKey" to appSignCache.appKey, "sign" to appSignCache.sign, "worksheetId" to tableId, "controls" to data.controls, "triggerWorkflow" to triggerWorkflow),
        ).let { respStr ->
            Utils.parseApiResponse(respStr, object : TypeReference<ApiResponse<String>>() {}).let {
                if (it != null) {
                    if (it.error_code == 1) {
                        return it.data
                    } else {
                        MdLog.error("写入 ${appId} 应用下的 ${tableId} 表中记录失败,${it.error_msg}")
                    }
                } else {
                    MdLog.error("数据解析异常,${respStr} 无法解析为 ApiResponse<String>")
                }
            }
        }
        return null
    }

    /**
     * 插入多行记录
     * @param appId 应用ID，可通过[getAppList]返回或在明道应用页面查看ID
     * @param tableId 表ID
     * [dataList] 写入的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入的行ID
     */
    fun insertRows(appId: String, tableId: String, dataList: List<MdDataControl>, triggerWorkflow: Boolean = true): Int {

        val appSignCache = appSignCache.get(appId) ?: getAppSignData(appId) ?: return 0

        HttpUtils.doPost(
            url = getRequestUrl(this.config.addRowsUrl),
            requestMap = hashMapOf("appKey" to appSignCache.appKey, "sign" to appSignCache.sign, "worksheetId" to tableId, "rows" to dataList.flatMap { arrayListOf(it.controls) }, "triggerWorkflow" to triggerWorkflow),
        ).let { respStr ->
            Utils.parseApiResponse(respStr, object : TypeReference<ApiResponse<Int>>() {}).let {
                if (it != null) {
                    if (it.error_code == 1) {
                        return it.data!!
                    } else {
                        MdLog.error("写入 ${appId} 应用下的 ${tableId} 表中多条记录失败,${it.error_msg}")
                    }
                } else {
                    MdLog.error("数据解析异常,${respStr} 无法解析为 ApiResponse<Int>")
                }
            }
        }
        return 0
    }

    /**
     * 更新行记录
     * @param appId 应用ID，可通过[getAppList]返回或在明道应用页面查看ID
     * @param tableId 表ID
     * [data] 写入的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入的行ID
     */
    fun updateRow(appId: String, tableId: String,rowId:String, data: MdDataControl, triggerWorkflow: Boolean = true): Boolean {

        val appSignCache = appSignCache.get(appId) ?: getAppSignData(appId) ?: return false

        HttpUtils.doPost(
            url = getRequestUrl(this.config.editRowUrl),
            requestMap = hashMapOf("appKey" to appSignCache.appKey, "sign" to appSignCache.sign, "rowId" to rowId, "worksheetId" to tableId, "controls" to data.controls, "triggerWorkflow" to triggerWorkflow),
        ).let { respStr ->
            Utils.parseApiResponse(respStr, object : TypeReference<ApiResponse<Boolean>>() {}).let {
                if (it != null) {
                    if (it.error_code == 1) {
                        return it.data!!
                    } else {
                        MdLog.error("写入 ${appId} 应用下的 ${tableId} 表中记录失败,${it.error_msg}")
                    }
                } else {
                    MdLog.error("数据解析异常,${respStr} 无法解析为 ApiResponse<Boolean>")
                }
            }
        }
        return false
    }

    // ============================================= 私有函数 =============================================

    /**
     * 返回请求的url
     */
    private fun getRequestUrl(path: String): String {
        val reqPath = if (this.config.removePathUrlApiHead && path.startsWith("/api") && path != this.config.appListUrl && path != this.config.appAuthUrl) {
            path.substring(4, path.length)
        } else path
        return String.format("%s%s", this.baseUrl, reqPath)
    }


}


fun main() {


    fun log(msg: String) {
        println(msg)
    }

    val testAppId = "dc0e2835-3312-47bd-9a0c-36ad2f1c72c3"

    //共有云配置实体
    val cloudMDHelper = MdHelper().also {
        it.init("https://api.mingdao.com", "9e079fb4-ff41-4ad1-8ed0-fff44e1a1844", "fd1ee46bea776198", "36a15cf2bb44be96d7fb8785f76984", config = Config().also {
            it.debugEnabled = false
        })
    }

    log("示例初始化完成，开始获取应用列表 ...")

    val appList = cloudMDHelper.getAppList()

    val appId = appList.find { it.appId == testAppId }!!.appId

    log("用第一个应用ID ${appId} 取签名信息 ...")

    val appSignData = cloudMDHelper.getAppSignData(appId)

    log("签名获取成功,${appSignData!!.toJson()} ,开始获取应用 ${appId} 详情 ...")

    val appInfo = cloudMDHelper.getAppInfo(appId)

    val tableId = appInfo!!.sections[0].items[0].id

    log("用第一个表ID ${tableId} 取表结构信息 ...")

    val tableInfo = cloudMDHelper.getTableInfo(appId, tableId)

    log("表结构获取成功,${tableInfo!!.toJson()},测试单条测试数据写入 ...")

    val rowId = cloudMDHelper.insertRow(
        appId, tableId, MdDataControl.Builder()
            .addControl("67c175caa47f709d9deb12d2", "测试名称")
            .addControl("67c175caa47f709d9deb12d3", "测试描述")
            .build()
    )!!

    log("写入成功,${rowId},测试多条数据写入 ...")

    val rowIdCount = cloudMDHelper.insertRows(
        appId, tableId, arrayListOf(
            MdDataControl.Builder()
                .addControl("67c175caa47f709d9deb12d2", "测试名称2")
                .addControl("67c175caa47f709d9deb12d3", "测试描述2")
                .build(),
            MdDataControl.Builder()
                .addControl("67c175caa47f709d9deb12d2", "测试名称3")
                .addControl("67c175caa47f709d9deb12d3", "测试描述3")
                .build(),

            )
    )
    log("写入成功,${rowIdCount}数据已写入,测试编辑行记录 ...")


    val isSucc = cloudMDHelper.updateRow(
        appId, tableId,rowId, MdDataControl.Builder()
            .addControl("67c175caa47f709d9deb12d2", "测试名称")
            .addControl("67c175caa47f709d9deb12d3", "测试描述")
            .build()
    )
    log("编辑完成 ${isSucc}")

    println()
}