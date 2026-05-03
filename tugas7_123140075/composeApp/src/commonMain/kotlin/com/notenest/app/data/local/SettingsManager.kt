package com.notenest.app.data.local

import com.notenest.app.model.AppSettings
import com.notenest.app.model.AppTheme
import com.notenest.app.model.SortOrder
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SettingsManager(settings: ObservableSettings) {

    private val flowSettings: FlowSettings = settings.toFlowSettings()

    companion object {
        private const val KEY_THEME = "app_theme"
        private const val KEY_SORT_ORDER = "sort_order"
        private const val KEY_SHOW_PINNED_FIRST = "show_pinned_first"
        private const val KEY_COMPACT_VIEW = "compact_view"
        private const val KEY_ACCENT_COLOR = "accent_color"
    }

    val themeFlow: Flow<String> = flowSettings.getStringFlow(KEY_THEME, AppTheme.SYSTEM.name)
    val sortOrderFlow: Flow<String> = flowSettings.getStringFlow(KEY_SORT_ORDER, SortOrder.UPDATED_DESC.name)
    val showPinnedFirstFlow: Flow<Boolean> = flowSettings.getBooleanFlow(KEY_SHOW_PINNED_FIRST, true)
    val compactViewFlow: Flow<Boolean> = flowSettings.getBooleanFlow(KEY_COMPACT_VIEW, false)
    val accentColorFlow: Flow<String> = flowSettings.getStringFlow(KEY_ACCENT_COLOR, "#6C63FF")

    val appSettingsFlow: Flow<AppSettings> = combine(
        themeFlow,
        sortOrderFlow,
        showPinnedFirstFlow,
        compactViewFlow,
        accentColorFlow
    ) { theme, sortOrder, showPinned, compact, accent ->
        AppSettings(
            theme = AppTheme.valueOf(theme),
            sortOrder = SortOrder.valueOf(sortOrder),
            showPinnedFirst = showPinned,
            compactView = compact,
            accentColor = accent
        )
    }

    suspend fun setTheme(theme: AppTheme) { flowSettings.putString(KEY_THEME, theme.name) }
    suspend fun setSortOrder(order: SortOrder) { flowSettings.putString(KEY_SORT_ORDER, order.name) }
    suspend fun setShowPinnedFirst(value: Boolean) { flowSettings.putBoolean(KEY_SHOW_PINNED_FIRST, value) }
    suspend fun setCompactView(value: Boolean) { flowSettings.putBoolean(KEY_COMPACT_VIEW, value) }
    suspend fun setAccentColor(hex: String) { flowSettings.putString(KEY_ACCENT_COLOR, hex) }
}