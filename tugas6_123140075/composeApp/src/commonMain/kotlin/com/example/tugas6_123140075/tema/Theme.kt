package com.example.tugas6_123140075.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryBlueLight.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryBlueDark,
    secondary = SecondaryOrange,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryOrangeLight.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryOrangeDark,
    background = BackgroundLight,
    surface = SurfaceLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDarkTheme,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryBlue.copy(alpha = 0.3f),
    onPrimaryContainer = PrimaryBlueLight,
    secondary = SecondaryOrangeLight,
    onSecondary = OnPrimaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
)

@Composable
fun NewsReaderAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}