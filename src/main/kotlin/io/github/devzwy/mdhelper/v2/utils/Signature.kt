package io.github.devzwy.mdhelper.v2.utils

import java.security.MessageDigest
import java.util.*

object Signature {
    /**
     * 获取签名
     *
     * @param appKey    AppKey
     * @param secretKey SecretKey
     * @param timestamp UTC时间戳（精度为毫秒）
     * @return
     */
    @Throws(Exception::class)
    fun getSignature(appKey: String, secretKey: String, timestamp: Long): String {
        //添加参数

        val paras: MutableMap<String, String> = HashMap()
        paras["AppKey"] = appKey
        paras["SecretKey"] = secretKey
        paras["Timestamp"] = timestamp.toString()

        //按Key排序
        val sortParas = TreeMap<String, String>()
        sortParas.putAll(paras)

        //拼接参数
        val it: Iterator<String> = sortParas.keys.iterator()
        val sortQueryStringTmp = StringBuilder()
        while (it.hasNext()) {
            val key = it.next()
            sortQueryStringTmp.append("&").append(key).append("=").append(paras[key])
        }
        val sortedQueryString = sortQueryStringTmp.substring(1)

        var sign = SHA256(sortedQueryString)
        sign = (Base64.getEncoder().encodeToString(sign!!.toByteArray(charset("UTF-8")))).replace("\n", "").replace("\r", "") //需替换BASE64的换行
        return sign
    }

    /**
     * SHA256加密
     *
     * @param strText
     * @return
     */
    @Throws(Exception::class)
    private fun SHA256(strText: String?): String? {
        var strResult: String? = null

        if (strText != null && strText.length > 0) {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(strText.toByteArray(charset("UTF-8")))

            val byteBuffer = messageDigest.digest()

            val strHexString = StringBuffer()
            for (i in byteBuffer.indices) {
                val hex = Integer.toHexString(0xff and byteBuffer[i].toInt())
                if (hex.length == 1) {
                    strHexString.append('0')
                }
                strHexString.append(hex)
            }
            strResult = strHexString.toString()
        }

        return strResult
    }
}