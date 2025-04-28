package io.github.devzwy.mdhelper.v2.utils

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONException
import com.alibaba.fastjson2.TypeReference
import io.github.devzwy.mdhelper.v2.ApiResponse
import java.util.*

object Utils {



    /**
     * 获取签名
     */
    fun getSign(appKey:String,secretKey:String,time: Long = Date().time): String {
        return Signature.getSignature(appKey, secretKey, time)
    }


    /**
     * 统一解析 JSON 字符串为 ApiResponse<T> 对象
     *
     * @param json         需要解析的 JSON 字符串
     * @param typeReference 目标类型的 TypeReference，例：object : TypeReference<ApiResponse<目标类型>>() {}
     * @return             返回解析后的 ApiResponse<T> 对象，解析失败时返回 null
     */
    fun <T> parseApiResponse(json: String, typeReference: TypeReference<ApiResponse<T>>): ApiResponse<T>? {
        return try {
            JSON.parseObject(json, typeReference)
        } catch (e: JSONException) {
            // 解析失败，打印错误日志，可以根据实际需求决定要不要上报错误
            e.printStackTrace()
            null
        }
    }

}