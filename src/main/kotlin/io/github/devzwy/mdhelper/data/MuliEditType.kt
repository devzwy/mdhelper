package io.github.devzwy.mdhelper.data

/**
 * 附件的操作类型
 */
enum class MuliEditType private constructor(val type:Int) {
    REPLACE(0),
    ADD(1)
}