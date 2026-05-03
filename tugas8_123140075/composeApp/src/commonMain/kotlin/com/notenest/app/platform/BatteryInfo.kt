package com.notenest.app.platform

expect class BatteryInfo() {
    fun getBatteryLevel(): Int
    fun isCharging(): Boolean
    fun getBatteryStatus(): String
}