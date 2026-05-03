package com.notenest.app.viewmodel

import com.notenest.app.data.local.SettingsManager
import com.notenest.app.model.AppSettings
import com.notenest.app.model.AppTheme
import com.notenest.app.model.SortOrder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SettingsViewModel(private val settingsManager: SettingsManager) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val settings: StateFlow<AppSettings> = settingsManager.appSettingsFlow
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setTheme(theme: AppTheme) { scope.launch { settingsManager.setTheme(theme) } }
    fun setSortOrder(order: SortOrder) { scope.launch { settingsManager.setSortOrder(order) } }
    fun setShowPinnedFirst(value: Boolean) { scope.launch { settingsManager.setShowPinnedFirst(value) } }
    fun setCompactView(value: Boolean) { scope.launch { settingsManager.setCompactView(value) } }
    fun setAccentColor(hex: String) { scope.launch { settingsManager.setAccentColor(hex) } }
    fun dispose() { scope.cancel() }
}