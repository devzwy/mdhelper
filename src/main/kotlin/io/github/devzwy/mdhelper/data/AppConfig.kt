package io.github.devzwy.mdhelper.data

import io.github.devzwy.mdhelper.manager.ConfigManager

data class AppConfig(val appKey:String,val sign:String)

fun main() {
    ConfigManager.toString()
}
