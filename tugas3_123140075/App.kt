package com.example.tugas1_123140075

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val customColors = lightColorScheme(
        primary = Color(0xFF4361EE),
        secondary = Color(0xFF3F37C9),
        primaryContainer = Color(0xFFCAF0F8),
        surface = Color.White
    )

    MaterialTheme(colorScheme = customColors) {
        MainProfileScreen_123140075()  // <- ini fungsi dari tugas3
    }
}