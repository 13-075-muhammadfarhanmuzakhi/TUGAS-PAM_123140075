package com.example.farhan_123140075

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Farhan_123140075",
    ) {
        App()
    }
}