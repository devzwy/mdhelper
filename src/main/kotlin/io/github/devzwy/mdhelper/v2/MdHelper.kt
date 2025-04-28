package io.github.devzwy.mdhelper.v2

import com.alibaba.fastjson2.TypeReference
import io.github.devzwy.mdhelper.MdLog
import io.github.devzwy.mdhelper.data.MdAppInfo
import io.github.devzwy.mdhelper.data.MdTableInfo
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
    fun getTableInfo(appId: String,tableId:String):MdTableInfo?{
        val appSignCache = appSignCache.get(appId) ?: getAppSignData(appId) ?: return null
        HttpUtils.doPost(
            url = getRequestUrl(this.config.tableInfoUrl),
            requestMap = hashMapOf("sign" to appSignCache.sign, "appKey" to appSignCache.appKey,"worksheetId" to tableId),
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
     * 返回请求的url
     */
    private fun getRequestUrl(path: String): String {
        val reqPath = if (this.config.removePathUrlApiHead && path.startsWith("/api") && path!=this.config.appListUrl && path!=this.config.appAuthUrl) {
            path.substring(4, path.length)
        } else path
        return String.format("%s%s", this.baseUrl, reqPath)
    }


}


fun main() {
    val cloudMDHelper = MdHelper().also {
        it.init("https://api.mingdao.com", "9e079fb4-ff41-4ad1-8ed0-fff44e1a1844", "fd1ee46bea776198", "36a15cf2bb44be96d7fb8785f76984", config = Config().also {
            it.debugEnabled = true
        })
    }



//    val a = cloudMDHelper.getAppList()
//    val b = cloudMDHelper.getAppSignData("5ebe8980-875b-4ebf-ab42-57025c1ca180")
//    val c = cloudMDHelper.getAppList(true)
    val d = cloudMDHelper.getAppInfo("5ebe8980-875b-4ebf-ab42-57025c1ca180")
    val e = cloudMDHelper.getTableInfo("5ebe8980-875b-4ebf-ab42-57025c1ca180","650bed60fb532be3de74886d")
    println()
}