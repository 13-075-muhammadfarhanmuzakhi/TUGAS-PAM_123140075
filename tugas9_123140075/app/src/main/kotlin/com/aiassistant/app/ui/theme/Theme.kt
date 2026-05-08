package com.aiassistant.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Indigo900 = Color(0xFF1A1A2E)
val Indigo800 = Color(0xFF16213E)
val Indigo700 = Color(0xFF0F3460)
val Violet500 = Color(0xFF7C3AED)
val Violet400 = Color(0xFF8B5CF6)
val Violet300 = Color(0xFFA78BFA)
val Cyan400   = Color(0xFF22D3EE)
val Cyan300   = Color(0xFF67E8F9)
val Rose500   = Color(0xFFF43F5E)
val Emerald400 = Color(0xFF34D399)
val Amber400  = Color(0xFFFBBF24)
val White     = Color(0xFFFFFFFF)
val Gray50    = Color(0xFFF9FAFB)
val Gray100   = Color(0xFFF3F4F6)
val Gray200   = Color(0xFFE5E7EB)
val Gray700   = Color(0xFF374151)
val Gray800   = Color(0xFF1F2937)
val Gray900   = Color(0xFF111827)

private val DarkColors = darkColorScheme(
    primary         = Violet400,
    onPrimary       = White,
    primaryContainer = Violet500.copy(alpha = 0.3f),
    secondary       = Cyan400,
    onSecondary     = Indigo900,
    tertiary        = Rose500,
    background      = Indigo900,
    onBackground    = White,
    surface         = Indigo800,
    onSurface       = White,
    surfaceVariant  = Gray800,
    onSurfaceVariant = Gray200,
    outline         = Violet500.copy(alpha = 0.4f),
    error           = Rose500
)

private val LightColors = lightColorScheme(
    primary         = Violet500,
    onPrimary       = White,
    primaryContainer = Violet300.copy(alpha = 0.2f),
    secondary       = Color(0xFF0891B2),
    onSecondary     = White,
    tertiary        = Rose500,
    background      = Gray50,
    onBackground    = Gray900,
    surface         = White,
    onSurface       = Gray900,
    surfaceVariant  = Gray100,
    onSurfaceVariant = Gray700,
    outline         = Gray200,
    error           = Rose500
)

@Composable
fun AIAssistantTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}