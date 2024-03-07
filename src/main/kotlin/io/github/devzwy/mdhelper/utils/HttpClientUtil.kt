import io.github.devzwy.mdhelper.MdLog
import com.alibaba.fastjson2.JSON
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Date

class HttpClientUtil {

    companion object {
        fun post(url: String, jsonStr: String): String {
            return httpRequest(url, "POST", jsonStr)
        }

        fun get(url: String, params: Map<String, String>): String {
            return httpRequest(buildUrlWithParams(url, params).toString(), "GET", null)
        }

        private fun httpRequest(url: String, method: String, requestBody: String?): String {

            //开始请求时间
            val startRequestTime = Date().time

            val connection = URL(url).openConnection() as HttpURLConnection

            if (method == "GET") {
                MdLog.debug("GET -> ${url}")
            } else {
                MdLog.debug("POST -> ${url}\n数据:${requestBody}")
            }

            connection.requestMethod = method
            connection.setRequestProperty("Accept-Charset", "UTF-8")
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.connectTimeout = 5 * 1000 // 连接超时时间为5秒
            connection.readTimeout = 60 * 1000// 读取超时时间为10秒
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
            }else{
                MdLog.error(response.toString())
            }

            connection.disconnect()

            return response.toString().also { MdLog.debug("${method}回传 ${url}\n请求耗时:${Date().time - startRequestTime} ms\n回传数据:${it}") }
        }

        private fun buildUrlWithParams(url: String, params: Map<String, String>): URL {
            val urlBuilder = StringBuilder(url)
            if (params.isNotEmpty()) {
                urlBuilder.append('?')
                for ((key, value) in params) {
                    urlBuilder
                        .append(URLEncoder.encode(key, "UTF-8"))
                        .append('=')
                        .append(URLEncoder.encode(value, "UTF-8"))
                        .append('&')
                }
                urlBuilder.deleteCharAt(urlBuilder.length - 1) // Remove the last '&'
            }

            return URL(urlBuilder.toString())
        }
    }
}
