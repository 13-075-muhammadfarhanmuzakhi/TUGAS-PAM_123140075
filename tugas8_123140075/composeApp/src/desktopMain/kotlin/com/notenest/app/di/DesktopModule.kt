package com.notenest.app.di

import com.notenest.app.data.local.DatabaseDriverFactory
import com.notenest.app.data.local.SettingsManager
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import org.koin.dsl.module
import java.util.prefs.Preferences

val desktopModule = module {
    single { DatabaseDriverFactory() }
    single<ObservableSettings> {
        PreferencesSettings(Preferences.userRoot().node("com.notenest.app"))
    }
    single { SettingsManager(get()) }
}