package com.notenest.app.platform

import java.io.File

actual class BatteryInfo {
    actual fun getBatteryLevel(): Int {
        return try {
            val osName = System.getProperty("os.name", "").lowercase()
            when {
                osName.contains("linux") -> {
                    val capacityFile = File("/sys/class/power_supply/BAT0/capacity")
                    if (capacityFile.exists()) capacityFile.readText().trim().toIntOrNull() ?: -1
                    else -1
                }
                osName.contains("windows") -> {
                    val process = Runtime.getRuntime().exec(
                        arrayOf("WMIC", "Path", "Win32_Battery", "Get", "EstimatedChargeRemaining")
                    )
                    val output = process.inputStream.bufferedReader().readLines()
                    output.getOrNull(1)?.trim()?.toIntOrNull() ?: -1
                }
                else -> -1
            }
        } catch (e: Exception) { -1 }
    }

    actual fun isCharging(): Boolean {
        return try {
            val osName = System.getProperty("os.name", "").lowercase()
            when {
                osName.contains("linux") -> {
                    val statusFile = File("/sys/class/power_supply/BAT0/status")
                    if (statusFile.exists()) statusFile.readText().trim() == "Charging"
                    else false
                }
                osName.contains("windows") -> {
                    val process = Runtime.getRuntime().exec(
                        arrayOf("WMIC", "Path", "Win32_Battery", "Get", "BatteryStatus")
                    )
                    val output = process.inputStream.bufferedReader().readLines()
                    val status = output.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
                    status == 2
                }
                else -> false
            }
        } catch (e: Exception) { false }
    }

    actual fun getBatteryStatus(): String {
        val level = getBatteryLevel()
        val charging = isCharging()
        return when {
            level == -1 -> "Desktop/AC Power"
            charging -> "Charging ($level%)"
            else -> "$level%"
        }
    }
}