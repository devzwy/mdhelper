package io.github.devzwy.mdhelper.v2.utils

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONWriter
import io.github.devzwy.mdhelper.MdLog
import io.github.devzwy.mdhelper.utils.MDUtil.toJson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

object HttpUtils {

    private var readTimeout:Int = 100000

    /**
     * 设置超时时间
     */
    fun setTimeout(readTimeout:Int){
        this.readTimeout = readTimeout
    }

    fun doGet(url: String, requestMap: Map<String, String?> = hashMapOf()): String {
        return httpRequest(buildUrlWithParams(url, requestMap).toString(), "GET", null)
    }

    fun doPost(url: String, requestMap: Map<String, Any?> = hashMapOf()): String {
        return httpRequest(url, "POST", requestMap.toJson())
    }


    private fun httpRequest(url: String, method: String, requestBody: String?): String {

        //开始请求时间
        val startRequestTime = Date().time

        val connection = URL(url).openConnection() as HttpURLConnection

        if (method == "GET") {
            MdLog.debug("GET -> ${url}")
        } else {
            MdLog.debug("POST -> ${url} : 数据:${requestBody}")
        }

        connection.requestMethod = method
        connection.setRequestProperty("Accept-Charset", "UTF-8")
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        connection.setRequestProperty("Accept", "application/json")
        connection.connectTimeout = 3000 // 连接超时时间为3秒
        connection.readTimeout = readTimeout// 读取超时时间为10秒
        connection.doOutput = true

        if (requestBody != null) {
            val os: OutputStream = connection.outputStream
            val input: ByteArray = requestBody.toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }

        val responseCode = connection.responseCode
        val response: StringBuilder = StringBuilder()

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()
        } else {
            MdLog.error(response.toString())
        }

        connection.disconnect()

        return response.toString().also { MdLog.debug("${method} <- [${Date().time - startRequestTime} ms] ${url}\n${formatJsonString(it)}") }
    }

    /**
     * 将 JSON 字符串格式化成漂亮的缩进样式
     *
     * @param jsonString 原始的JSON字符串
     * @return 格式化后的字符串，如果格式错误则返回原字符串
     */
    private fun formatJsonString(jsonString: String): String {
        return try {
            // 先把字符串解析成对象
            val jsonObject = JSON.parse(jsonString)
            // 再把对象以格式化方式输出
            JSON.toJSONString(jsonObject, JSONWriter.Feature.PrettyFormat)
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果解析失败，返回原始字符串
            jsonString
        }
    }

    private fun buildUrlWithParams(url: String, params: Map<String, String?>): URL {
        val urlBuilder = StringBuilder(url)
        if (params.isNotEmpty()) {
            urlBuilder.append('?')
            for ((key, value) in params) {
                value?.let {
                    urlBuilder
                        .append(URLEncoder.encode(key, "UTF-8"))
                        .append('=')
                        .append(URLEncoder.encode(value, "UTF-8"))
                        .append('&')
                }
            }
            urlBuilder.deleteCharAt(urlBuilder.length - 1) // Remove the last '&'
        }

        return URL(urlBuilder.toString())
    }
}