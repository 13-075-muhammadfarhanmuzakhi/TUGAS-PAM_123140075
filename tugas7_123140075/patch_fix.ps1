# ============================================================
# NoteNest - PATCH SCRIPT (Fix Build Errors)
# Jalankan di root folder project (notenest/)
# powershell -ExecutionPolicy Bypass -File patch_fix.ps1
# ============================================================

Write-Host "=== NoteNest Patch - Fixing Build Errors ===" -ForegroundColor Cyan

function Write-File {
    param([string]$Path, [string]$Content)
    $dir = Split-Path $Path -Parent
    if ($dir -and !(Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    [System.IO.File]::WriteAllText($Path, $Content, [System.Text.Encoding]::UTF8)
    Write-Host "  [FIXED] $Path" -ForegroundColor Green
}

# ============================================================
# FIX 1: composeApp/build.gradle.kts
# Tambah materialIconsExtended dependency
# ============================================================
Write-Host ""
Write-Host "Fix 1: Menambahkan materialIconsExtended..." -ForegroundColor Yellow

Write-File "composeApp\build.gradle.kts" @'
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            // FIX: Material Icons Extended untuk PushPin, Sort, GridView, Storage, dll
            implementation(compose.materialIconsExtended)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.uuid)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.sqldelight.sqlite.driver)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.notenest.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "NoteNest"
            packageVersion = "1.0.0"
            description = "A beautiful notes application"
            copyright = "2025 NoteNest"
        }
    }
}

sqldelight {
    databases {
        create("NoteNestDatabase") {
            packageName.set("com.notenest.app.db")
        }
    }
}
'@

# ============================================================
# FIX 2: SettingsManager.kt
# Ganti Settings -> ObservableSettings agar toFlowSettings() bisa dipanggil
# ============================================================
Write-Host "Fix 2: Memperbaiki SettingsManager (ObservableSettings)..." -ForegroundColor Yellow

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\data\local\SettingsManager.kt" @'
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
'@

# ============================================================
# FIX 3: main.kt
# Cast PreferencesSettings ke ObservableSettings
# ============================================================
Write-Host "Fix 3: Memperbaiki main.kt (cast ke ObservableSettings)..." -ForegroundColor Yellow

Write-File "composeApp\src\desktopMain\kotlin\com\notenest\app\main.kt" @'
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
'@

# ============================================================
# SELESAI
# ============================================================
Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host " Semua fix berhasil diterapkan!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Sekarang jalankan:" -ForegroundColor Yellow
Write-Host '  $env:JAVA_HOME = "D:\D sementara\androidstudio\jbr"' -ForegroundColor Cyan
Write-Host "  .\gradlew :composeApp:run --no-configuration-cache" -ForegroundColor Cyan
Write-Host ""
