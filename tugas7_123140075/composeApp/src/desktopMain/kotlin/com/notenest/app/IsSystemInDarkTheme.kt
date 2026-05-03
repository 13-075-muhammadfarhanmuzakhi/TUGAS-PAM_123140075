package com.notenest.app

import androidx.compose.runtime.Composable

@Composable
actual fun isSystemInDarkTheme(): Boolean {
    val osName = System.getProperty("os.name", "").lowercase()
    return when {
        osName.contains("mac") -> {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("defaults", "read", "-g", "AppleInterfaceStyle"))
                process.inputStream.bufferedReader().readText().trim().equals("Dark", ignoreCase = true)
            } catch (_: Exception) { false }
        }
        else -> false
    }
}