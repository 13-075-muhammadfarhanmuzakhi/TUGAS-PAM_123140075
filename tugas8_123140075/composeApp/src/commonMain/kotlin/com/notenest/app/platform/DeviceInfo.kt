package com.notenest.app.platform

expect class DeviceInfo() {
    fun getDeviceName(): String
    fun getOsName(): String
    fun getOsVersion(): String
    fun getAppVersion(): String
    fun getJvmVersion(): String
}