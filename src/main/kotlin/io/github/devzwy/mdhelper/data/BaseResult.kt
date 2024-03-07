package io.github.devzwy.mdhelper.data

data class BaseResult<T>(val success:Boolean, val error_code:Int, var data:T?=null)
