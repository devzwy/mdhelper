package io.github.devzwy.mdhelper

import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * SDK内部日志打印
 */
internal object MdLog {

    // 是否打印日志
    private var isLogEnabled: Boolean = false

    /**
     * 开启日志打印
     */
    fun enable() {
        this.isLogEnabled = true
    }


    /**
     * 关闭日志打印
     */
    fun disable() {
        this.isLogEnabled = false
    }

    /**
     * 打印调试日志
     */
    fun debug(msg: String?) {
        if (isLogEnabled) {
            println("[DEBUG][${time()}] $msg")
        }
    }

    /**
     * 打印错误日志
     */
    fun error(msg: String?) {
        System.err.println("[ERROR][${time()}] ${msg}")
    }

    /**
     * 打印错误日志
     */
    fun error(ex: Exception) {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        ex.printStackTrace(pw)
        val stackTrace = sw.toString()
        System.err.println("[ERROR][${time()}] ${stackTrace}")
    }

    /**
     * 格式化日志时间
     */
    private fun time() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
}