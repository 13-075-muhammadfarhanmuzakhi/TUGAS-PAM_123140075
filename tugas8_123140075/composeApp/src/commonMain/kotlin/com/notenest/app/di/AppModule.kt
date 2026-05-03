package com.notenest.app.di

import com.notenest.app.data.local.DatabaseProvider
import com.notenest.app.data.local.NoteLocalDataSource
import com.notenest.app.data.local.SettingsManager
import com.notenest.app.data.repository.NoteRepository
import com.notenest.app.platform.BatteryInfo
import com.notenest.app.platform.DeviceInfo
import com.notenest.app.platform.NetworkMonitor
import com.notenest.app.viewmodel.NotesViewModel
import com.notenest.app.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val platformModule = module {
    single { DeviceInfo() }
    single { NetworkMonitor() }
    single { BatteryInfo() }
}

val dataModule = module {
    single { DatabaseProvider.getDatabase(get()) }
    single { NoteLocalDataSource(get()) }
    single { NoteRepository(get()) }
}

val viewModelModule = module {
    factory { NotesViewModel(get()) }
    factory { SettingsViewModel(get()) }
}

val appModules = listOf(platformModule, dataModule, viewModelModule)