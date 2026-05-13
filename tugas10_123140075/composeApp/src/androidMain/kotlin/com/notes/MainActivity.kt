package com.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.notes.presentation.screen.NotesScreen

// Skema warna: Indigo / Deep Blue
private val AppColors = lightColorScheme(
    primary         = Color(0xFF3F51B5),   // Indigo
    onPrimary       = Color.White,
    primaryContainer= Color(0xFFE8EAF6),
    secondary       = Color(0xFF5C6BC0),
    onSecondary     = Color.White,
    background      = Color(0xFFF4F5FA),
    surface         = Color(0xFFFFFFFF),
    onSurface       = Color(0xFF1A1A2E),
    onBackground    = Color(0xFF1A1A2E),
    error           = Color(0xFFD32F2F),
    onError         = Color.White,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AppColors) {
                Surface {
                    NotesScreen()
                }
            }
        }
    }
}
