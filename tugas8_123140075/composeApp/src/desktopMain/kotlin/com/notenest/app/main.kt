package com.notenest.app

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.notenest.app.di.desktopModule

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "NoteNest",
        state = WindowState(width = 1100.dp, height = 720.dp)
    ) {
        App(extraModules = listOf(desktopModule))
    }
}