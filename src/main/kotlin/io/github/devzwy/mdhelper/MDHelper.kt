package io.github.devzwy.mdhelper

import io.github.devzwy.mdhelper.data.MdTableInfo
import io.github.devzwy.mdhelper.data.RowBaseResult
import io.github.devzwy.mdhelper.manager.ApiManager
import io.github.devzwy.mdhelper.manager.ConfigManager
import io.github.devzwy.mdhelper.utils.MdDataControl
import io.github.devzwy.mdhelper.utils.MdFilterControl

/**
 * 明道工具类
 * 支持多个BASEURL
 * 调试日志默认开启，关闭调用[MdLog.disable]，注意：无法关闭错误日志的输出，系统默认打印全部错误日志
 */
class MDHelper private constructor() {


    /**
     * 获取单例操作对象
     */
    companion object {

        @Volatile
        private var instance: MDHelper? = null

        /**
         * 获取单例操作对象
         */
        fun getInstance(): MDHelper {
            if (instance == null) {
                synchronized(MDHelper::class.java) {
                    if (instance == null) {
                        instance = MDHelper()
                    }
                }
            }
            return instance!!
        }
    }


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
        clazz: Class<T>,
    ) = ApiManager.getRows(baseUrlKey, appConfigKey, tableId, filter, pageSize, pageIndex, viewId, sortId, isAsc, notGetTotal, useControlId, clazz)

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
    ) = ApiManager.getRows(baseUrlKey, appKey, sign, tableId, filter, pageSize, pageIndex, viewId, sortId, isAsc, notGetTotal, useControlId, clazz)

    /**
     * 删除行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 删除行的Id
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 删除成功返回true 否则返回false
     */
    fun deleteRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, triggerWorkflow: Boolean = true) =
        ApiManager.deleteRow(baseUrlKey, appConfigKey, tableId, rowId, triggerWorkflow)

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
    fun deleteRow(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, rowId: String, triggerWorkflow: Boolean = true) = ApiManager.deleteRow(baseUrlKey, appKey, sign, tableId, rowId, triggerWorkflow)

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
    fun updateRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, data: MdDataControl, triggerWorkflow: Boolean = true) =
        ApiManager.updateRow(baseUrlKey, appConfigKey, tableId, rowId, data, triggerWorkflow)


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
    fun updateRow(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, rowId: String, data: MdDataControl, triggerWorkflow: Boolean = true) = ApiManager.updateRow(baseUrlKey, appKey, sign, tableId, rowId, data,triggerWorkflow)

        /**
     * 获取行记录详情
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * @return 行记录数据JSON
     */
    fun <T> getRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, clazz: Class<T>) = ApiManager.getRow(baseUrlKey, appConfigKey, tableId, rowId, clazz)

    /**
     * 获取行记录详情
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [rowId] 行记录ID
     * @return 行记录数据JSON
     */
    fun <T> getRow(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, rowId: String, clazz: Class<T>) = ApiManager.getRow(baseUrlKey, appKey, sign, tableId, rowId, clazz)


    /**
     * 插入多行记录，最大1000行
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [dataList] 写入的数据列，使用[MdDataControl.Builder]构造多个
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * * @return 写入成功后回传写入成功的总行数
     */
    fun insertRows(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, dataList: List<MdDataControl>, triggerWorkflow: Boolean = true) =
        ApiManager.insertRows(baseUrlKey, appConfigKey, tableId, dataList, triggerWorkflow)


    /**
     * 插入单行记录
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * [data] 写入的数据列，使用[MdDataControl.Builder]构造
     * [triggerWorkflow] 是否触发工作流(默认: true)
     * @return 写入成功后回传写入的行ID
     */
    fun insertRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, data: MdDataControl, triggerWorkflow: Boolean = true) =
        ApiManager.insertRow(baseUrlKey, appConfigKey, tableId, data, triggerWorkflow)

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
    fun insertRow(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String, data: MdDataControl, triggerWorkflow: Boolean = true) = ApiManager.insertRow(baseUrlKey, appKey, sign, tableId, data,triggerWorkflow)

    /**
     * 获取表结构信息
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * @return 表结构信息JSOn
     */
    fun getTableInfo(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String) = ApiManager.getTableInfo(baseUrlKey, appConfigKey, tableId)

    /**
     * 获取表结构信息
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * [tableId] 操作的表ID，可以为别名或者明道生成的ID
     * @return 表结构信息[MdTableInfo]
     */
    fun getTableInfo(baseUrlKey: String? = null, appKey: String, sign: String, tableId: String) = ApiManager.getTableInfo(baseUrlKey, appKey, sign, tableId)

    /**
     * 获取应用数据
     * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
     * @return 返回应用信息
     */
    fun getAppInfo(baseUrlKey: String? = null, appConfigKey: String? = null) = ApiManager.getAppInfo(baseUrlKey, appConfigKey)

    /**
     * 获取应用数据
     * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
     * [appKey] 应用的appkey
     * [sign] 应用的签名
     * @return 返回应用信息
     */
    fun getAppInfo(baseUrlKey: String? = null, appKey: String, sign: String) = ApiManager.getAppInfo(baseUrlKey, appKey, sign)

    /**
     * 开启日志打印
     */
    fun enableLog() = MdLog.enable()

    /**
     * 关闭日志打印
     */
    fun disableLog() = MdLog.disable()


    /**
     * 添加应用配置
     * [configKey] 配置的key，后续操作改应用时必填,
     * [appKey] 明道云的appKey
     * [sign] 明道云应用的Sign
     */
    fun addAppConfig(configKey: String, appKey: String, sign: String) =
        ConfigManager.addAppConfig(configKey, appKey, sign)

    /**
     * 根据应用配置的key删除应用
     */
    fun removeAppByConfigKey(configKey: String) = ConfigManager.removeAppByConfigKey(configKey)

    /**
     * 获取应用配置,不填写key时会取第一个添加的应用配置,如果没有添加过应用时时可能会抛出异常
     * [appConfigKey] 调用[addApp]时的key
     */
    fun getAppConfig(appConfigKey: String? = null) = ConfigManager.getAppConfig(appConfigKey)

    /**
     * 添加BaseUrl,内部判断所添加的url值，存在时将跳过添加，跳过添加时会有错误日志输出。
     * [key] 后续操作需要携带，用于找到对应的url
     * [url] url值
     */
    fun addBaseUrl(key: String, url: String) = ConfigManager.addBaseUrl(key, url)

    /**
     * 根据key移除baseurl
     * [key] 调用[addBaseUrl]时的key
     */
    fun removeBaseUrlByKey(key: String) = ConfigManager.removeBaseUrlByKey(key)

    /**
     * 移除全部应用配置
     */
    fun removeAllAppConfigs() = ConfigManager.removeAllAppConfigs()

    /**
     * 移除全部baseurl配置
     */
    fun removeAllBaseUrls() = ConfigManager.removeAllBaseUrls()

    /**
     * 获取baseurl,不填写key时会取第一个添加的baseurl,如果没有添加过baseurl时可能会抛出异常
     * [key] 调用[addBaseUrl]时的key
     */
    fun getBaseUrl(key: String? = null) = ConfigManager.getBaseUrl(key)

    /**
     * 获取全部配置的baseurl
     */
    fun getAllBaseUrls() = ConfigManager.getAllBaseUrls()

    /**
     * 获取全部应用配置
     */
    fun getAllAppConfigs() = ConfigManager.getAllAppConfigs()

}