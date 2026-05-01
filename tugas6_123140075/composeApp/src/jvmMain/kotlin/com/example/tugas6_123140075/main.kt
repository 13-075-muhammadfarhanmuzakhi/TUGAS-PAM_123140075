package com.example.tugas6_123140075

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "News Reader - tugas6_123140075"
    ) {
        App()
    }
}