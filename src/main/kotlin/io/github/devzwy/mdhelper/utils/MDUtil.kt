package io.github.devzwy.mdhelper.utils

import com.alibaba.fastjson2.JSON


object MDUtil {

    fun Any.toJson() = JSON.toJSONString(this)


}