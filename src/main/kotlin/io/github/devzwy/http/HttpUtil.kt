package io.github.devzwy.http

import io.github.devzwy.data.ILoggerFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit


object HttpUtil {

    private var timeout = 10 * 1000L

    private lateinit var client: OkHttpClient

    private var loggerFactory: ILoggerFactory? = null

    /**
     * 配置超时时间
     */
    fun setTimeOut(t: Long) {
        this.timeout = t
    }


    /**
     * 初始化
     */
    fun init(loggerFactory: ILoggerFactory? = null) {
        this.loggerFactory = loggerFactory
        client = OkHttpClient.Builder()
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
            .build()
    }

    private fun hashMapToQueryString(params: HashMap<String, Any>): String {
        val queryString = StringBuilder()

        for ((key, value) in params) {
            if (queryString.isNotEmpty()) {
                queryString.append("&")
            }
            queryString.append("${key}=${value}")
        }

        return queryString.toString()
    }

    /**
     * 发送get请求
     * [url] 不带参数
     * [params] 参数部分，最终会被拼到url后
     */
    fun sendGet(url: String, params: HashMap<String, Any>): String? {
        client.newCall(
            Request.Builder()
                .url("${url}?${hashMapToQueryString(params)}".also {
                    loggerFactory?.log("sendGet:${it}")
                })
                .build()
        ).execute().let { response ->
            loggerFactory?.log("${url} has response:${response}")
            if (response.code != 200) {
                loggerFactory?.err(response)
                return null
            }
            return (response.body?.string())?.also { loggerFactory?.log("${url} has return:${it}") }
        }
    }

    /**
     * 发送Post请求
     */
    fun sendPost(url: String, jsonStr: String): String? {
        client.newCall(
            Request.Builder()
                .url(url)
                .post(jsonStr.also {
                    loggerFactory?.log("sendGet:${it}")
                }.toRequestBody("application/json".toMediaType()))
                .build()
        ).execute().let { response ->
            loggerFactory?.log("${url} has response:${response}")
            if (response.code != 200) {
                loggerFactory?.err(response)
                return null
            }
            return (response.body?.string()).also { loggerFactory?.log("${url} has return:${it}") }
        }
    }
}