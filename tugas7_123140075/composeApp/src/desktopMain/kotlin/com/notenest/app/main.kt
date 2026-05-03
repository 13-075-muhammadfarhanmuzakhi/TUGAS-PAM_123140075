package com.notenest.app

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.notenest.app.data.local.DatabaseDriverFactory
import com.notenest.app.data.local.DatabaseProvider
import com.notenest.app.data.local.NoteLocalDataSource
import com.notenest.app.data.local.SettingsManager
import com.notenest.app.data.repository.NoteRepository
import com.notenest.app.viewmodel.NotesViewModel
import com.notenest.app.viewmodel.SettingsViewModel
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

fun main() = application {
    // Setup database
    val driverFactory = DatabaseDriverFactory()
    val database = DatabaseProvider.getDatabase(driverFactory)
    val localDataSource = NoteLocalDataSource(database)
    val noteRepository = NoteRepository(localDataSource)

    // Setup settings - PreferencesSettings implements ObservableSettings
    val preferences = Preferences.userRoot().node("com.notenest.app")
    val settings: ObservableSettings = PreferencesSettings(preferences)
    val settingsManager = SettingsManager(settings)

    // ViewModels
    val notesViewModel = NotesViewModel(noteRepository)
    val settingsViewModel = SettingsViewModel(settingsManager)

    Window(
        onCloseRequest = {
            notesViewModel.dispose()
            settingsViewModel.dispose()
            exitApplication()
        },
        title = "NoteNest",
        state = WindowState(width = 1100.dp, height = 720.dp)
    ) {
        App(
            notesViewModel = notesViewModel,
            settingsViewModel = settingsViewModel
        )
    }
}