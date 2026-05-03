package com.notenest.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryPurple = Color(0xFF6C63FF)
val PrimaryPurpleLight = Color(0xFF9B94FF)
val PrimaryPurpleDark = Color(0xFF4A42D1)
val AccentTeal = Color(0xFF00BCD4)
val NeutralGray50 = Color(0xFFF8F9FA)
val NeutralGray100 = Color(0xFFF1F3F4)
val NeutralGray200 = Color(0xFFE8EAED)
val NeutralGray600 = Color(0xFF80868B)
val NeutralGray900 = Color(0xFF202124)
val DarkSurface = Color(0xFF1E1E2E)
val DarkSurface2 = Color(0xFF252535)
val DarkSurface3 = Color(0xFF2D2D3F)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple, onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7FF), onPrimaryContainer = PrimaryPurpleDark,
    secondary = AccentTeal, onSecondary = Color.White,
    background = NeutralGray50, onBackground = NeutralGray900,
    surface = Color.White, onSurface = NeutralGray900,
    surfaceVariant = NeutralGray100, onSurfaceVariant = NeutralGray600,
    outline = NeutralGray200, error = Color(0xFFE53935), onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurpleLight, onPrimary = Color(0xFF2D2455),
    primaryContainer = Color(0xFF4A42D1), onPrimaryContainer = Color(0xFFEDE7FF),
    secondary = AccentTeal, onSecondary = Color(0xFF003A40),
    background = DarkSurface, onBackground = Color(0xFFE3E3F0),
    surface = DarkSurface2, onSurface = Color(0xFFE3E3F0),
    surfaceVariant = DarkSurface3, onSurfaceVariant = Color(0xFFAAAAAC),
    outline = Color(0xFF444458), error = Color(0xFFFF6B6B), onError = Color(0xFF4D0000)
)

@Composable
fun NoteNestTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = NoteNestTypography, content = content)
}