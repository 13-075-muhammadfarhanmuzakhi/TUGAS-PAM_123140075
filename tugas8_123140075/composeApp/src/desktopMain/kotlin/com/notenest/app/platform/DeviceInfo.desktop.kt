package com.notenest.app.platform

import java.net.InetAddress

actual class DeviceInfo {
    actual fun getDeviceName(): String {
        return try {
            InetAddress.getLocalHost().hostName
        } catch (e: Exception) {
            System.getProperty("os.name", "Desktop") + " PC"
        }
    }

    actual fun getOsName(): String {
        return System.getProperty("os.name", "Unknown OS")
    }

    actual fun getOsVersion(): String {
        return System.getProperty("os.version", "Unknown")
    }

    actual fun getAppVersion(): String {
        return "1.0.0"
    }

    actual fun getJvmVersion(): String {
        return System.getProperty("java.version", "Unknown")
    }
}