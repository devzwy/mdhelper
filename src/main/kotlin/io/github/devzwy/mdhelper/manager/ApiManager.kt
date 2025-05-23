package io.github.devzwy.mdhelper.manager

import ErrorCodeEnum
import HttpClientUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.TypeReference
import io.github.devzwy.mdhelper.data.BaseResult
import io.github.devzwy.mdhelper.data.MdAppInfo
import io.github.devzwy.mdhelper.data.MdTableInfo
import io.github.devzwy.mdhelper.data.RowBaseResult
import io.github.devzwy.mdhelper.utils.MDUtil.toJson
import io.github.devzwy.mdhelper.utils.MdDataControl
import io.github.devzwy.mdhelper.utils.MdFilterControl
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 数据管理器
 */
internal object ApiManager {

    //共有云的明道域名，apipath不包含/api前缀
    private const val CLOUD_MD_WWW = "mingdao.com"

    //读取应用信息
    private const val URL_APP_INFO = "/api/v1/open/app/get"

    //增加行记录
    private const val URL_ADD_ROW = "/api/v2/open/worksheet/addRow"

    //批量增加行记录
    private const val URL_ADD_ROWS = "/api/v2/open/worksheet/addRows"

    //表结构信息
    private const val URL_TABLE_INFO = "/api/v2/open/worksheet/getWorksheetInfo"

    //编辑行记录
    private const val URL_EDIT_ROW = "/api/v2/open/worksheet/editRow"

    //编辑多行记录
    private const val URL_EDIT_ROWS = "/api/v2/open/worksheet/editRows"

    //获取行记录详情
    private const val URL_GET_ROW = "/api/v2/open/worksheet/getRowByIdPost"

    //删除行
    private const val URL_DEL_ROW = "/api/v2/open/worksheet/deleteRow"

    //过滤行
    private const val URL_FILTER_ROW = "/api/v2/open/worksheet/getFilterRows"

    //查询明道的所有引用列表的时候用
    private const val URL_GET_APP_LIST = "/v1/open/app/getByProject"

    /**
     * 获取列表 传入接收单个row的实体，一些公用的参数(明道基础字段)封装在了[io.github.devzwy.mdhelper.data.Row]类，如有需要可以自行继承
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [filter] 过滤条件，使用[MdFilterControl.Builder]构造多个
     * [pageSize] 行数
     * [pageIndex] 页码
     * [viewId] 视图ID
     * [sortId] 排序字段ID
     * [isAsc] 是否升序
     * [notGetTotal] 是否不统计总行数以提高性能(默认: false)
     * [useControlId] 是否只返回controlId(默认: false)
     * [clazz] 最终列表的每个实体的接收对象
     * @return 过滤后的数据[RowBaseResult]
     */
    fun <T> getRows(
        baseUrlKey: String? = null,
        appConfigKey: String? = null,
        tableId: String,
        filter: MdFilterControl,
        pageSize: Int? = null,
        pageIndex: Int? = null,
        viewId: String? = null,
        sortId: String? = null,
        isAsc: Boolean? = null,
        notGetTotal: Boolean? = null,
        useControlId: Boolean? = null,
        clazz: Class<T>
    ): RowBaseResult<T> {
        val appConfig = getAppConfig(appConfigKey)
        return getRows(baseUrlKey, appConfig.appKey, appConfig.sign, tableId, filter, pageSize, pageIndex, viewId, sortId, isAsc, notGetTotal, useControlId, clazz)
    }

    /**
     * 获取列表 传入接收单个row的实体，一些公用的参数(明道基础字段)封装在了[io.github.devzwy.mdhelper.data.Row]类，如有需要可以自行继承
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [filter] 过滤条件，使用[MdFilterControl.Builder]构造多个
     * [pageSize] 行数
     * [pageIndex] 页码
     * [viewId] 视图ID
     * [sortId] 排序字段ID
     * [isAsc] 是否升序
     * [notGetTotal] 是否不统计总行数以提高性能(默认: false)
     * [useControlId] 是否只返回controlId(默认: false)
     * [clazz] 最终列表的每个实体的接收对象
     * @return 过滤后的数据[RowBaseResult]
     */
    fun <T> getRows(
        baseUrlKey: String? = null,
        appKey: String,
        sign: String,
        tableId: String,
        filter: MdFilterControl,
        pageSize: Int? = null,
        pageIndex: Int? = null,
        viewId: String? = null,
        sortId: String? = null,
        isAsc: Boolean? = null,
        notGetTotal: Boolean? = null,
        useControlId: Boolean? = null,
        clazz: Class<T>
    ): RowBaseResult<T> {

        val url = getUrl(baseUrlKey, URL_FILTER_ROW)

        val requestData = hashMapOf("appKey" to appKey, "sign" to sign, "worksheetId" to tableId, (if (filter.isGroupFilter) "groupFilters" else "filters") to filter.filters)

        viewId?.let { requestData.put("viewId", it) }
        pageSize?.let { requestData.put("pageSize", it) }
        pageIndex?.let { requestData.put("pageIndex", it) }
        sortId?.let { requestData.put("sortId", it) }
        isAsc?.let { requestData.put("isAsc", it) }
        notGetTotal?.let { requestData.put("notGetTotal", it) }
        useControlId?.let { requestData.put("useControlId", it) }


        val resultStr = HttpClientUtil.post(url, requestData.toJson())


        // 构建 Type 对象，用于解析JSON
        val listType: Type = object : ParameterizedType {
            override fun getRawType(): Type = RowBaseResult::class.java
            override fun getOwnerType(): Type? = null
            override fun getActualTypeArguments(): Array<Type> = arrayOf(clazz)
        }

        val type: Type = object : ParameterizedType {
            override fun getRawType(): Type = BaseResult::class.java
            override fun getOwnerType(): Type? = null
            override fun getActualTypeArguments(): Array<Type> = arrayOf(listType)
        }

        // 使用构建好的 Type 对象解析JSON
        val result = JSON.parseObject(resultStr, type) as BaseResult<RowBaseResult<T>>

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取列表请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }


    /**
     * 删除行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 删除行的Id
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 删除成功返回true 否则返回false
     */
    fun deleteRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, triggerWorkflow: Boolean = true): Boolean {
        val appConfig = getAppConfig(appConfigKey)
        return deleteRow(baseUrlKey, appConfig.appKey, appConfig.sign, tableId, rowId, triggerWorkflow)
    }

    /**
     * 删除行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 删除行的Id
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 删除成功返回true 否则返回false
     */
    fun deleteRow(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, rowId: String, triggerWorkflow: Boolean = true): Boolean {
        val url = getUrl(baseUrlKey, URL_DEL_ROW)
        val requestData = hashMapOf(
            "appKey" to appKey, "sign" to sign, "worksheetId" to tableId, "rowId" to rowId, "triggerWorkflow" to triggerWorkflow
        )
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<Boolean>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("删除行记录请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }


    /**
     * 插入多行记录，最大1000行
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [dataList] 写入的数据列，使用[MdDataControl.Builder]构造多个
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入成功的总行数
     */
    fun insertRows(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, dataList: List<MdDataControl>, triggerWorkflow: Boolean = true): Int {

        val appConfig = getAppConfig(appConfigKey)

        return insertRows(baseUrlKey, appConfig.appKey, appConfig.sign, tableId, dataList, triggerWorkflow)
    }


    /**
     * 插入多行记录，最大1000行
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [dataList] 写入的数据列，使用[MdDataControl.Builder]构造多个
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入成功的总行数
     */
    fun insertRows(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, dataList: List<MdDataControl>, triggerWorkflow: Boolean = true): Int {

        val url = getUrl(baseUrlKey, URL_ADD_ROWS)

        val requestData = hashMapOf(
            "appKey" to appKey, "sign" to sign, "worksheetId" to tableId, "rows" to dataList.flatMap { arrayListOf(it.controls) }, "triggerWorkflow" to triggerWorkflow
        )
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<Int>>() {})

        return if (result != null && ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("写入行记录失败，${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }


    /**
     * 插入单行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [data] 写入的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入的行ID
     */
    fun insertRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, data: MdDataControl, triggerWorkflow: Boolean = true): String {
        val appConfig = getAppConfig(appConfigKey)
        return insertRow(baseUrlKey, appConfig.appKey, appConfig.sign, tableId, data, triggerWorkflow)
    }

    /**
     * 插入单行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [data] 写入的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入的行ID
     */
    fun insertRow(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, data: MdDataControl, triggerWorkflow: Boolean = true): String {

        val url = getUrl(baseUrlKey, URL_ADD_ROW)

        val requestData = hashMapOf("appKey" to appKey, "sign" to sign, "worksheetId" to tableId, "controls" to data.controls, "triggerWorkflow" to triggerWorkflow)
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<String>>() {})

        return if (result != null && ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("写入行记录失败，${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }


    /**
     * 编辑行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * [data] 更新的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 编辑成功返回true，否则返回false
     */
    fun updateRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, data: MdDataControl, triggerWorkflow: Boolean = true): Boolean {

        val appConfig = getAppConfig(appConfigKey)

        return updateRow(baseUrlKey, appConfig.appKey, appConfig.sign, tableId, rowId, data,triggerWorkflow)
    }


    /**
     * 编辑行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * [data] 更新的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 编辑成功返回true，否则返回false
     */
    fun updateRow(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, rowId: String, data: MdDataControl, triggerWorkflow: Boolean = true): Boolean {

        val url = getUrl(baseUrlKey, URL_EDIT_ROW)

        val requestData = hashMapOf(
            "appKey" to appKey, "sign" to sign, "worksheetId" to tableId, "rowId" to rowId, "controls" to data.controls, "triggerWorkflow" to triggerWorkflow
        )
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<Boolean>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("编辑行记录请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }

    /**
     * 编辑多行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * [data] 更新的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 编辑成功返回true，否则返回false
     */
    fun updateRows(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowIds: List<String>, data: MdDataControl, triggerWorkflow: Boolean = true): Boolean {

        val appConfig = getAppConfig(appConfigKey)

        return updateRows(baseUrlKey, appConfig.appKey, appConfig.sign, tableId, rowIds, data,triggerWorkflow)
    }


    /**
     * 编辑多行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * [data] 更新的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 编辑成功返回true，否则返回false
     */
    fun updateRows(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, rowIds: List<String>, data: MdDataControl, triggerWorkflow: Boolean = true): Boolean {

        val url = getUrl(baseUrlKey, URL_EDIT_ROWS)

        val requestData = hashMapOf(
            "appKey" to appKey, "sign" to sign, "worksheetId" to tableId, "rowIds" to rowIds, "controls" to data.controls, "triggerWorkflow" to triggerWorkflow
        )
        val resultStr = HttpClientUtil.post(url, requestData.toJson())
        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<Boolean>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("编辑多行记录请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }




    /**
     * 获取行记录详情
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * @return 行记录数据JSON
     */
    fun <T> getRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, clazz: Class<T>): T {
        val appConfig = getAppConfig(appConfigKey)
        return getRow(baseUrlKey, appConfig.appKey, appConfig.sign, tableId, rowId, clazz)
    }

    /**
     * 获取行记录详情
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * @return 行记录数据JSON
     */
    fun <T> getRow(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, rowId: String, clazz: Class<T>): T {

        val url = getUrl(baseUrlKey, URL_GET_ROW)

        val requestData = hashMapOf("appKey" to appKey, "sign" to sign, "worksheetId" to tableId, "rowId" to rowId)
        val resultStr = HttpClientUtil.post(url, requestData.toJson())

        val type: Type = object : ParameterizedType {
            override fun getRawType(): Type = BaseResult::class.java
            override fun getOwnerType(): Type? = null
            override fun getActualTypeArguments(): Array<Type> = arrayOf(clazz)
        }

        val result = JSON.parseObject(resultStr, type) as BaseResult<T>

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取行记录详情请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }

    /**
     * 获取表结构信息
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * @return 表结构信息JSOn
     */
    fun getTableInfo(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String): MdTableInfo {
        val appConfig = getAppConfig(appConfigKey)
        return getTableInfo(baseUrlKey, appConfig.appKey, appConfig.sign, tableId)
    }

    /**
     * 获取表结构信息
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * @return 表结构信息[MdTableInfo]
     */
    fun getTableInfo(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String): MdTableInfo {
        val url = getUrl(baseUrlKey, URL_TABLE_INFO)
        val requestData = hashMapOf("appKey" to appKey, "sign" to sign, "worksheetId" to tableId)
        val resultStr = HttpClientUtil.post(url, requestData.toJson())

        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<MdTableInfo>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取表结构信息请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }

    /**
     * 获取应用数据
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * @return 返回应用信息
     */
    fun getAppInfo(baseUrlKey: String? = null, appConfigKey: String? = null): MdAppInfo {
        val appInfo = ConfigManager.getAppConfig(appConfigKey)
        return getAppInfo(baseUrlKey, appInfo.appKey, appInfo.sign)
    }

    /**
     * 获取应用数据
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * @return 返回应用信息
     */
    fun getAppInfo(baseUrlKey: String? = null, appKey: String, sign: String): MdAppInfo {

        val resultStr = HttpClientUtil.get("${ConfigManager.getBaseUrl(baseUrlKey)}${URL_APP_INFO}", hashMapOf("appKey" to appKey, "sign" to sign))

        //解析数据
        val result = JSON.parseObject(resultStr, object : TypeReference<BaseResult<MdAppInfo>>() {})

        return if (ErrorCodeEnum.fromCode(result.error_code) == ErrorCodeEnum.SUCCESS) {
            result.data!!
        } else {
            throw RuntimeException("获取应用数据请求失败，明道回传了失败的结果：${ErrorCodeEnum.fromCode(result.error_code).description}")
        }
    }

    private fun getUrl(baseUrlKey: String?, path: String?): String {

        val baseUrl = ConfigManager.getBaseUrl(baseUrlKey)
        var reqPath = path ?: ""
        if (baseUrl.contains(CLOUD_MD_WWW) && reqPath.startsWith("/api")) {
            reqPath = reqPath.substring(4)
        }
        return "${baseUrl}${reqPath}"
    }

    private fun getAppConfig(appConfigKey: String?) = ConfigManager.getAppConfig(appConfigKey)


}