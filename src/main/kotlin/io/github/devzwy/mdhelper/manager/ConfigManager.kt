package io.github.devzwy.mdhelper.manager

import io.github.devzwy.mdhelper.MdLog
import io.github.devzwy.mdhelper.data.AppConfig
import java.util.concurrent.ConcurrentSkipListMap

internal object ConfigManager {

    //使用的baseUrl列表
    private val baseUrlHashMap = ConcurrentSkipListMap<String, String>()

    //app配置
    private val appConfigHashMap = ConcurrentSkipListMap<String, AppConfig>()

    /**
     * 添加应用配置
     * [configKey] 配置的key，后续操作改应用时必填,
     * [appKey] 明道云的appKey
     * [sign] 明道云应用的Sign
     */
    fun addAppConfig(configKey: String, appKey: String, sign: String) {

        if (checkAppConfigUnique(configKey)) {
            MdLog.error("添加的应用配置Key已存在,${configKey}")
            return
        }

        appConfigHashMap.put(configKey, AppConfig(appKey, sign))

        MdLog.debug("应用添加成功,${configKey}:${appKey},${sign}")
    }

    /**
     * 根据应用配置的key删除应用
     */
    fun removeAppByConfigKey(configKey: String) {
        if (appConfigHashMap.containsKey(configKey)) {
            appConfigHashMap.remove(configKey)
            MdLog.debug("已移除key=${configKey}的应用")
        }
    }

    /**
     * 获取应用配置,不填写key时会取第一个添加的应用配置,如果没有添加过应用时时可能会抛出异常
     * [appConfigKey] 调用[addApp]时的key
     */
    fun getAppConfig(appConfigKey: String?): AppConfig {
        val key = appConfigKey ?: appConfigHashMap.entries.firstOrNull()!!.key
        return if (appConfigHashMap.containsKey(key)) appConfigHashMap[key]!! else throw Exception("未找到${key}对应的应用配置")
    }


    /**
     * 添加BaseUrl,内部判断所添加的url值，存在时将跳过添加，跳过添加时会有错误日志输出。
     * [key] 后续操作需要携带，用于找到对应的url
     * [url] url值
     */
    fun addBaseUrl(key: String, url: String) {
        synchronized(this) {
            if (this.checkBaseUrlUnique(key, url)) {
                MdLog.error("相同key或url的值已存在！")
                return
            }
            baseUrlHashMap.put(key, url)
            MdLog.debug("baseUrl添加成功:${key}:${url}")
        }
    }


    /**
     * 根据key移除baseurl
     * [key] 调用[addBaseUrl]时的key
     */
    fun removeBaseUrlByKey(key: String) {
        if (baseUrlHashMap.containsKey(key)) {
            baseUrlHashMap.remove(key)
            MdLog.debug("已移除key=${key}的baseUrl")
        }
    }

    /**
     * 移除全部应用配置
     */
    fun removeAllAppConfigs() {
        appConfigHashMap.clear()
        MdLog.debug("全部应用配置已清空，长度:${appConfigHashMap.size}")
    }

    /**
     * 移除全部baseurl配置
     */
    fun removeAllBaseUrls() {
        baseUrlHashMap.clear()
        MdLog.debug("全部BaseUrl配置已清空，长度:${baseUrlHashMap.size}")
    }

    /**
     * 获取baseurl,不填写key时会取第一个添加的baseurl,如果没有添加过baseurl时可能会抛出异常
     * [key] 调用[addBaseUrl]时的key
     */
    fun getBaseUrl(key: String? = baseUrlHashMap.entries.firstOrNull()!!.key): String {
        val ck = key ?: baseUrlHashMap.entries.firstOrNull()!!.key
        return if (baseUrlHashMap.containsKey(ck)) baseUrlHashMap[ck]!! else throw Exception("未找到${ck}对应的baseurl")
    }

    /**
     * 获取全部配置的baseurl
     */
    fun getAllBaseUrls() = baseUrlHashMap

    /**
     * 获取全部应用配置
     */
    fun getAllAppConfigs() = appConfigHashMap

    /**
     * 检查key或value的唯一
     */
    private fun checkBaseUrlUnique(key: String, url: String) =
        baseUrlHashMap.containsKey(key) || baseUrlHashMap.contains(url)


    /**
     * 检查key唯一
     */
    private fun checkAppConfigUnique(key: String) = baseUrlHashMap.containsKey(key)
}