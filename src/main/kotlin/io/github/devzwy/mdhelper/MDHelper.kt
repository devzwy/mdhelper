package io.github.devzwy.mdhelper

import io.github.devzwy.mdhelper.http.HttpUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.devzwy.mdhelper.data.*
import io.github.devzwy.mdhelper.util.toRowDataList
import java.util.*

class MDHelper private constructor(private val baseUrl: String, private val configList: ArrayList<MDConfig>, val loggerFactory: ILoggerFactory? = null) {

    private val gson by lazy {
        Gson()
    }

    class Builder {

        private var baseUrl: String = ""

        private val configList = arrayListOf<MDConfig>()

        private var loggerFactory: ILoggerFactory? = null


        /**
         * 添加一个logger拦截器需要继承自[io.github.devzwy.mdhelper.data.ILoggerFactory] 自行实现函数
         */
        fun setLogger(loggerFactory: ILoggerFactory) = apply { this.loggerFactory = loggerFactory }

        /**
         * 明道云地址
         */
        fun baseUrl(url: String) = apply {
            this.baseUrl = if (url.endsWith("/")) url.substring(0, url.length - 1) else url
            loggerFactory?.log("url配置完成：${this.baseUrl}")
        }

        /**
         * 网络请求超时时间
         * [timeout] 超时时间 单位 ms
         */
        fun httpTimeOut(timeout: Long = 10 * 1000L) = apply { HttpUtil.setTimeOut(timeout) }


        /**
         * 添加一个操作应用数据,重复的应用名称会跳过添加
         * [appName] 应用名称
         * [appKey] AppKey
         * [sign] Sign
         */
        fun addConfig(appName: Any, appKey: String, sign: String) = apply {
            if (!appExists(appName)) {
                this.configList.add(MDConfig(appName, appKey, sign))
                loggerFactory?.log("配置添加完成：${this.configList.last()}")
            }
        }

        /**
         * 生成工具类
         * [check] 检查所有配置是否正常,开启时配置错误时内部将抛出异常，默认开启
         */
        fun build(check: Boolean = true): MDHelper {
            if (this.baseUrl.isEmpty() || this.configList.isEmpty()) throw NullPointerException("未配置baseUrl或添加应用，请调用Builder.baseUrl/.addConfig后再试。")
            return MDHelper(this.baseUrl, configList).also {
                HttpUtil.init(loggerFactory)
                if (check) {
                    //对每一个配置获取一下应用信息
                    it.configList.forEach { cnf ->
                        if (it.getAppInfo(cnf.appName) == null) {
                            throw java.lang.RuntimeException("校验不通过，无法获取应用数据，请检查key是否配置正确。${cnf}")
                        }
                    }
                }
                loggerFactory?.log("初始化完成，所有涉及传参为appName时为空将自动使用最后添加的配置->${this.configList.last()}")
            }
        }

        /**
         * 查询同名称的应用是否已配置
         */
        private fun appExists(appName: Any) = this.configList.filter { it.appName == appName }.isNotEmpty()

    }

    /**
     * 查询同名称的应用是否已配置
     * [appName] 应用名称
     */
    fun appExists(appName: Any) = this.configList.filter { it.appName == appName }.isNotEmpty()

    /**
     * 临时添加一个操作应用数据 重复的应用名称不会被添加
     * [appName] 应用名称
     * [appKey] AppKey
     * [sign] Sign
     */
    fun addConfig(appName: Any, appKey: String, sign: String) {
        if (!appExists(appName)) {
            this.configList.add(MDConfig(appName, appKey, sign))
            loggerFactory?.log("配置添加完成：${this.configList.last()}")
        }
    }

    /**
     * 根据应用名称修改原有配置，找不到时不做处理
     * [appName] 应用名称
     * [appKey] 新的AppKey
     * [sign] 新的Sign
     */
    fun updateConfig(appName: Any, appKey: String, sign: String) {
        if (appExists(appName)) {
            this.configList.removeIf { it.appName == appName }
            this.configList.add(MDConfig(appName, appKey, sign))
            loggerFactory?.log("配置更新完成")
        }
    }

    /**
     * 根据应用名称删除配置，找不到时不处理
     * [appName] 应用名称
     */
    fun delConfig(appName: Any) {
        if (appExists(appName)) {
            this.configList.removeIf { it.appName == appName }
            loggerFactory?.log("配置删除完成")
        }
    }

    /**
     * 获取应用信息
     * 查询失败时返回null
     * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
     * @return 成功时返回应用信息，否则返回空。
     */
    fun getAppInfo(appName: Any? = null): AppInfo? {
        appName.getConfig().let { mdConfig ->
            return HttpUtil.sendGet(
                "/api/v1/open/app/get".getRequestUrl(), hashMapOf(
                    "appKey" to mdConfig.appKey, "sign" to mdConfig.sign
                )
            ).parseResp<AppInfo>()
        }
    }


    /**
     * 新建工作表
     * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
     * [tableName] 工作表的名称，例如：用户表
     * [tableAlias] 工作表的别名，默认为空，例如：yhb
     * [filed] 表字段，可传入多个。注意构造时内部的type必须使用[io.github.devzwy.mdhelper.data.DataType]类进行构造，否则会出现找不到类型的错误
     * @return 创建成功时返回工作表的ID，否则返回空
     */
    fun createTable(appName: Any? = null, tableName: String, tableAlias: String? = null, vararg filed: CreateTableData) = HttpUtil.sendPost(
        "/api/v2/open/worksheet/addWorksheet".getRequestUrl(),
        hashMapOf<String, Any?>("name" to tableName, "alias" to tableAlias, "controls" to filed).buildRequestJsonParams(appName)
    ).parseResp<String>()


    /**
     * 获取工作表结构信息
     * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
     * [worksheetId] 获取的工作表的ID
     * @return 表结构数据 [io.github.devzwy.mdhelper.data.MDTableInfo]
     */
    fun getTableInfo(appName: Any? = null, worksheetId: String): MDTableInfo? {
        return HttpUtil.sendPost(
            "/api/v2/open/worksheet/getWorksheetInfo".getRequestUrl(), hashMapOf<String, Any?>(
                "worksheetId" to worksheetId
            ).buildRequestJsonParams(appName)
        ).parseResp<MDTableInfo>()
    }


    /**
     * 临时读取指定url的表结构数据
     * 需要传入全部参数,不会加入配置中
     * [url] baseUrl http://xxx.cn
     * [appKey] 应用密钥
     * [sign] 应用签名
     * [worksheetId] 表名
     * @return 获取成功时返回表结构数据，否则返回null
     */
    fun getTableInfoOfTmp(url: String, appKey: String, sign: String, worksheetId: String): MDTableInfo? {
        return HttpUtil.sendPost(
            "${if (url.endsWith("/")) url.substring(0, url.length - 1) else url}/api/v2/open/worksheet/getWorksheetInfo", gson.toJson(
                hashMapOf<String, Any?>(
                    "worksheetId" to worksheetId,
                    "appKey" to appKey,
                    "sign" to sign
                )
            )
        ).parseResp<MDTableInfo>()
    }


    /**
     * 获取列表
     * [R] 传入MDRowData<最终接收实体(可继承自[io.github.devzwy.mdhelper.data.MDRow]以获取父类字段)>
     * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
     * [worksheetId] 工作表id
     * [pageSize] 分页数据 为空会拉取最大1000数据
     * [pageIndex] 分页数据 为空会拉取最大1000数据
     * [filter] 筛选配置 使用[io.github.devzwy.mdhelper.data.FilterBean.Builder]进行构造 为空时不筛选
     * @return 查询结果列表
     */
    inline fun <reified R> getData(appName: Any? = null, worksheetId: String, pageSize: Int? = null, pageIndex: Int? = null, filter: FilterData? = null): R? {
        return HttpUtil.sendPost(
            "/api/v2/open/worksheet/getFilterRows".getRequestUrl(), hashMapOf<String, Any?>(
                "worksheetId" to worksheetId,
                "pageSize" to pageSize,
                "pageIndex" to pageIndex,
                "sortId" to filter?.sortId,
                "isAsc" to filter?.isAsc,
                "notGetTotal" to filter?.notGetTotal,
                "filters" to filter?.filters
            ).buildRequestJsonParams(appName)
        ).parseResp<R>()
    }


    /**
     * 新建行记录
     * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
     * [worksheetId] 工作表id
     * [triggerWorkflow] 是否触发工作流
     * [data] 写入的列数据 需要上传附件时 这里必须为list 并且 值为 [ExtraData]对象，否则将会当做普通字段上传
     * @return 成功时返回写入记录的行ID 否则为空
     */
    fun insertRow(appName: Any? = null, worksheetId: String, triggerWorkflow: Boolean? = null, data: HashMap<String, Any?>): String? {
        if (data.isEmpty() || worksheetId.isEmpty()) return null
        return HttpUtil.sendPost(
            "/api/v2/open/worksheet/addRow".getRequestUrl(), hashMapOf<String, Any?>(
                "worksheetId" to worksheetId,
                "triggerWorkflow" to triggerWorkflow,
                "controls" to data.toRowDataList()
            ).buildRequestJsonParams(appName)
        ).parseResp<String>()
    }

    /**
     * 获取行记录详情
     * [R] 最终接收实体(可继承自[io.github.devzwy.mdhelper.data.MDRow]以获取父类字段)
     * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
     * [worksheetId] 工作表id
     * [rowId] 行记录ID
     * @return
     */
    inline fun <reified R> getRow(appName: Any? = null, worksheetId: String, rowId: String): R? {
        if (rowId.isEmpty() || worksheetId.isEmpty()) return null
        return HttpUtil.sendPost(
            "/api/v2/open/worksheet/getRowByIdPost".getRequestUrl(), hashMapOf<String, Any?>(
                "worksheetId" to worksheetId,
                "rowId" to rowId
            ).buildRequestJsonParams(appName)
        ).parseResp<R>()
    }

    /**
     * 更新行记录详情
     * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
     * [worksheetId] 工作表id
     * [triggerWorkflow] 是否触发工作流
     * [data] 更新的列数据 需要上传附件时 这里必须为list 并且 值为 [ExtraData]对象，否则将会当做普通字段上传
     * [rowId] 更新的行记录ID
     * @return 成功时返回true，否则返回null
     */
    fun updateRow(appName: Any? = null, worksheetId: String, rowId: String, triggerWorkflow: Boolean? = null, data: HashMap<String, Any?>): Boolean? {
        if (data.isEmpty() || worksheetId.isEmpty() || rowId.isEmpty()) return null

        return HttpUtil.sendPost(
            "/api/v2/open/worksheet/editRow".getRequestUrl(), hashMapOf<String, Any?>(
                "worksheetId" to worksheetId,
                "triggerWorkflow" to triggerWorkflow,
                "rowId" to rowId,
                "controls" to data.toRowDataList()
            ).buildRequestJsonParams(appName)
        ).parseResp<Boolean>()
    }


    /**
     * 删除行记录
     * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
     * [worksheetId] 工作表id
     * [rowId] 行记录ID
     * [triggerWorkflow] 是否触发工作流
     * @return 成功时返回true，否则返回null
     */
    fun delRow(appName: Any? = null, worksheetId: String, rowId: String, triggerWorkflow: Boolean? = null): Boolean? {
        if (worksheetId.isEmpty() || rowId.isEmpty()) return null
        return HttpUtil.sendPost(
            "/api/v2/open/worksheet/deleteRow".getRequestUrl(), hashMapOf<String, Any?>(
                "worksheetId" to worksheetId,
                "triggerWorkflow" to triggerWorkflow,
                "rowId" to rowId
            ).buildRequestJsonParams(appName)
        ).parseResp<Boolean>()
    }


    /**
     * 获取工作表总行数
     * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
     * [worksheetId] 工作表id
     * [filter] 筛选配置 使用[io.github.devzwy.mdhelper.data.FilterBean.Builder]进行构造 为空时不筛选
     * @return 成功时返回行数，否则返回null
     */
    fun getWorksheetCount(appName: Any? = null, worksheetId: String, filter: FilterData? = null): Int? {
        return HttpUtil.sendPost(
            "/api/v2/open/worksheet/getFilterRowsTotalNum".getRequestUrl(), hashMapOf<String, Any?>(
                "worksheetId" to worksheetId,
                "filters" to filter?.filters
            ).buildRequestJsonParams(appName)
        ).parseResp<Int>()
    }


    /**
     * 子路径拼接全路径
     */
    fun String.getRequestUrl() = "${baseUrl}/${this}"

    /**
     * 格式化一下回传的参数
     * 内部自动判断是否成功，失败返回null
     * 成功返回范型对应的数据
     */
    inline fun <reified T> String?.parseResp(): T? {
        Gson().fromJson<MDBaseResult<T>>(this, object : TypeToken<MDBaseResult<T>>() {}.type).apply {
            return if (success) {
                data
            } else {
                loggerFactory?.err("${error_msg}(${error_code})")
                null
            }
        }
    }

    /**
     * 根据AppName在配置中查找出对应的配置实体，传入空时默认返回最后一个配置
     * 传入的appName查找不到时会报错
     */
    private fun Any?.getConfig() = if (this == null) configList.last() else configList.filter { it.appName == this }.last()

    /**
     * 构建请求json参数
     */
    fun HashMap<String, Any?>.buildRequestJsonParams(appName: Any?): String {
        appName.getConfig().let { mdConfig ->
            this["appKey"] = mdConfig.appKey
            this["sign"] = mdConfig.sign
            return gson.toJson(this)
        }
    }

    /**
     * 明道应用配置
     * [appName] 应用的唯一标识，调用Api时需要填写
     * [appKey] 应用的AppKey
     * [secretKey] 应用的SecretKey
     * [sign] 应用的Sign
     */
    private data class MDConfig(val appName: Any, val appKey: String, val sign: String)


}


