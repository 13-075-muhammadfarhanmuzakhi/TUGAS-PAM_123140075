package com.notenest.app

import androidx.compose.animation.*
import androidx.compose.runtime.*
import com.notenest.app.model.AppTheme
import com.notenest.app.ui.screens.MainScreen
import com.notenest.app.ui.screens.SettingsScreen
import com.notenest.app.ui.theme.NoteNestTheme
import com.notenest.app.viewmodel.NotesViewModel
import com.notenest.app.viewmodel.SettingsViewModel

enum class Screen { MAIN, SETTINGS }

@Composable
fun App(notesViewModel: NotesViewModel, settingsViewModel: SettingsViewModel) {
    val settings by settingsViewModel.settings.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.MAIN) }
    val isDarkTheme = when (settings.theme) { AppTheme.DARK -> true; AppTheme.LIGHT -> false; AppTheme.SYSTEM -> isSystemInDarkTheme() }
    NoteNestTheme(darkTheme = isDarkTheme) {
        AnimatedContent(targetState = currentScreen, transitionSpec = { fadeIn(initialAlpha = 0f) togetherWith fadeOut() }) { screen ->
            when (screen) {
                Screen.MAIN -> MainScreen(viewModel = notesViewModel, onNavigateToSettings = { currentScreen = Screen.SETTINGS })
                Screen.SETTINGS -> SettingsScreen(viewModel = settingsViewModel, onBack = { currentScreen = Screen.MAIN })
            }
        }
    }
}

@Composable
expect fun isSystemInDarkTheme(): Boolean