# ============================================================
# NoteNest - Auto Setup Script for Windows (PowerShell)
# Usage: powershell -ExecutionPolicy Bypass -File setup_notenest.ps1
# ============================================================

Write-Host "=== NoteNest Setup Script ===" -ForegroundColor Cyan
Write-Host "Membuat semua folder dan file..." -ForegroundColor Yellow

function Write-File {
    param([string]$Path, [string]$Content)
    $dir = Split-Path $Path -Parent
    if ($dir -and !(Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
    [System.IO.File]::WriteAllText($Path, $Content, [System.Text.Encoding]::UTF8)
    Write-Host "  [OK] $Path" -ForegroundColor Green
}

Write-File "gradle\libs.versions.toml" @'
[versions]
kotlin = "2.0.21"
compose = "1.7.0"
coroutines = "1.9.0"
sqldelight = "2.0.2"
multiplatformSettings = "1.2.0"
kotlinxDatetime = "0.6.1"
koin = "4.0.0"
uuid = "0.8.4"

[libraries]
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
kotlinx-coroutines-swing = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-swing", version.ref = "coroutines" }
sqldelight-runtime = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-coroutines = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-sqlite-driver = { module = "app.cash.sqldelight:sqlite-driver", version.ref = "sqldelight" }
multiplatform-settings = { module = "com.russhwolf:multiplatform-settings", version.ref = "multiplatformSettings" }
multiplatform-settings-coroutines = { module = "com.russhwolf:multiplatform-settings-coroutines", version.ref = "multiplatformSettings" }
multiplatform-settings-no-arg = { module = "com.russhwolf:multiplatform-settings-no-arg", version.ref = "multiplatformSettings" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinxDatetime" }
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
uuid = { module = "com.benasher44:uuid", version.ref = "uuid" }

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
compose-multiplatform = { id = "org.jetbrains.compose", version.ref = "compose" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
sqldelight = { id = "app.cash.sqldelight", version.ref = "sqldelight" }
'@

Write-File "build.gradle.kts" @'
plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.sqldelight) apply false
}
'@

Write-File "settings.gradle.kts" @'
rootProject.name = "NoteNest"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":composeApp")
'@

Write-File "gradle\wrapper\gradle-wrapper.properties" @'
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.9-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
'@

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

Write-File "composeApp\src\commonMain\sqldelight\com\notenest\app\db\Note.sq" @'
CREATE TABLE NoteEntity (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    category TEXT NOT NULL DEFAULT 'general',
    is_pinned INTEGER NOT NULL DEFAULT 0,
    color_hex TEXT NOT NULL DEFAULT '#FFFFFF',
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE INDEX idx_note_updated ON NoteEntity(updated_at DESC);
CREATE INDEX idx_note_category ON NoteEntity(category);

selectAll:
SELECT * FROM NoteEntity ORDER BY is_pinned DESC, updated_at DESC;

selectById:
SELECT * FROM NoteEntity WHERE id = ?;

selectByCategory:
SELECT * FROM NoteEntity WHERE category = ? ORDER BY is_pinned DESC, updated_at DESC;

searchNotes:
SELECT * FROM NoteEntity
WHERE title LIKE '%' || ? || '%' OR content LIKE '%' || ? || '%'
ORDER BY is_pinned DESC, updated_at DESC;

insertNote:
INSERT INTO NoteEntity(id, title, content, category, is_pinned, color_hex, created_at, updated_at)
VALUES (?, ?, ?, ?, ?, ?, ?, ?);

updateNote:
UPDATE NoteEntity
SET title = ?, content = ?, category = ?, is_pinned = ?, color_hex = ?, updated_at = ?
WHERE id = ?;

deleteNote:
DELETE FROM NoteEntity WHERE id = ?;

deleteAllNotes:
DELETE FROM NoteEntity;

countNotes:
SELECT COUNT(*) FROM NoteEntity;

countByCategory:
SELECT COUNT(*) FROM NoteEntity WHERE category = ?;

selectPinned:
SELECT * FROM NoteEntity WHERE is_pinned = 1 ORDER BY updated_at DESC;

togglePin:
UPDATE NoteEntity SET is_pinned = ?, updated_at = ? WHERE id = ?;
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\model\Note.kt" @'
package com.notenest.app.model

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val isPinned: Boolean,
    val colorHex: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    val formattedDate: String
        get() {
            val instant = Instant.fromEpochMilliseconds(updatedAt)
            val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val day = local.dayOfMonth.toString().padStart(2, '0')
            val month = local.monthNumber.toString().padStart(2, '0')
            val year = local.year
            val hour = local.hour.toString().padStart(2, '0')
            val minute = local.minute.toString().padStart(2, '0')
            return "$day/$month/$year $hour:$minute"
        }

    val preview: String
        get() = if (content.length > 120) content.take(120) + "..." else content
}

enum class NoteCategory(val label: String, val emoji: String) {
    GENERAL("General", "\uD83D\uDCDD"),
    WORK("Work", "\uD83D\uDCBC"),
    PERSONAL("Personal", "\uD83C\uDFE0"),
    STUDY("Study", "\uD83D\uDCDA"),
    IDEA("Ideas", "\uD83D\uDCA1"),
    HEALTH("Health", "\u2764\uFE0F");

    companion object {
        fun fromString(value: String): NoteCategory =
            entries.firstOrNull { it.name.lowercase() == value.lowercase() } ?: GENERAL
    }
}

val noteColorPalette = listOf(
    "#FFFFFF", "#FFF9C4", "#F8BBD9", "#C8E6C9",
    "#BBDEFB", "#E1BEE7", "#FFCCBC", "#B2EBF2"
)
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\model\AppSettings.kt" @'
package com.notenest.app.model

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val sortOrder: SortOrder = SortOrder.UPDATED_DESC,
    val showPinnedFirst: Boolean = true,
    val compactView: Boolean = false,
    val accentColor: String = "#6C63FF"
)

enum class AppTheme(val label: String) {
    LIGHT("Light"), DARK("Dark"), SYSTEM("System Default")
}

enum class SortOrder(val label: String) {
    UPDATED_DESC("Last Modified"), UPDATED_ASC("Oldest Modified"),
    CREATED_DESC("Newest Created"), CREATED_ASC("Oldest Created"),
    TITLE_ASC("Title A-Z"), TITLE_DESC("Title Z-A")
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\data\local\DatabaseProvider.kt" @'
package com.notenest.app.data.local

import app.cash.sqldelight.db.SqlDriver
import com.notenest.app.db.NoteNestDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

object DatabaseProvider {
    private var instance: NoteNestDatabase? = null

    fun getDatabase(factory: DatabaseDriverFactory): NoteNestDatabase {
        return instance ?: NoteNestDatabase(factory.createDriver()).also { instance = it }
    }
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\data\local\NoteLocalDataSource.kt" @'
package com.notenest.app.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.notenest.app.db.NoteNestDatabase
import com.notenest.app.db.NoteEntity
import com.notenest.app.model.Note
import com.notenest.app.model.NoteCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NoteLocalDataSource(private val database: NoteNestDatabase) {
    private val queries = database.noteQueries

    fun getAllNotes(): Flow<List<Note>> =
        queries.selectAll().asFlow().mapToList(Dispatchers.Default).map { list -> list.map { it.toDomain() } }

    fun searchNotes(query: String): Flow<List<Note>> =
        queries.searchNotes(query, query).asFlow().mapToList(Dispatchers.Default).map { list -> list.map { it.toDomain() } }

    fun getNotesByCategory(category: String): Flow<List<Note>> =
        queries.selectByCategory(category).asFlow().mapToList(Dispatchers.Default).map { list -> list.map { it.toDomain() } }

    fun getPinnedNotes(): Flow<List<Note>> =
        queries.selectPinned().asFlow().mapToList(Dispatchers.Default).map { list -> list.map { it.toDomain() } }

    suspend fun getNoteById(id: String): Note? =
        withContext(Dispatchers.Default) { queries.selectById(id).executeAsOneOrNull()?.toDomain() }

    suspend fun insertNote(note: Note) = withContext(Dispatchers.Default) {
        queries.insertNote(id = note.id, title = note.title, content = note.content,
            category = note.category.name.lowercase(), is_pinned = if (note.isPinned) 1L else 0L,
            color_hex = note.colorHex, created_at = note.createdAt, updated_at = note.updatedAt)
    }

    suspend fun updateNote(note: Note) = withContext(Dispatchers.Default) {
        queries.updateNote(title = note.title, content = note.content, category = note.category.name.lowercase(),
            is_pinned = if (note.isPinned) 1L else 0L, color_hex = note.colorHex, updated_at = note.updatedAt, id = note.id)
    }

    suspend fun deleteNote(id: String) = withContext(Dispatchers.Default) { queries.deleteNote(id) }

    suspend fun togglePin(id: String, isPinned: Boolean, updatedAt: Long) = withContext(Dispatchers.Default) {
        queries.togglePin(is_pinned = if (isPinned) 1L else 0L, updated_at = updatedAt, id = id)
    }

    private fun NoteEntity.toDomain(): Note = Note(
        id = id, title = title, content = content,
        category = NoteCategory.fromString(category), isPinned = is_pinned == 1L,
        colorHex = color_hex, createdAt = created_at, updatedAt = updated_at
    )
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\data\local\SettingsManager.kt" @'
package com.notenest.app.data.local

import com.notenest.app.model.AppSettings
import com.notenest.app.model.AppTheme
import com.notenest.app.model.SortOrder
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SettingsManager(settings: Settings) {
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
        themeFlow, sortOrderFlow, showPinnedFirstFlow, compactViewFlow, accentColorFlow
    ) { theme, sortOrder, showPinned, compact, accent ->
        AppSettings(theme = AppTheme.valueOf(theme), sortOrder = SortOrder.valueOf(sortOrder),
            showPinnedFirst = showPinned, compactView = compact, accentColor = accent)
    }

    suspend fun setTheme(theme: AppTheme) { flowSettings.putString(KEY_THEME, theme.name) }
    suspend fun setSortOrder(order: SortOrder) { flowSettings.putString(KEY_SORT_ORDER, order.name) }
    suspend fun setShowPinnedFirst(value: Boolean) { flowSettings.putBoolean(KEY_SHOW_PINNED_FIRST, value) }
    suspend fun setCompactView(value: Boolean) { flowSettings.putBoolean(KEY_COMPACT_VIEW, value) }
    suspend fun setAccentColor(hex: String) { flowSettings.putString(KEY_ACCENT_COLOR, hex) }
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\data\repository\NoteRepository.kt" @'
package com.notenest.app.data.repository

import com.benasher44.uuid.uuid4
import com.notenest.app.data.local.NoteLocalDataSource
import com.notenest.app.model.Note
import com.notenest.app.model.NoteCategory
import com.notenest.app.model.SortOrder
import com.notenest.app.model.noteColorPalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class NoteRepository(private val localDataSource: NoteLocalDataSource) {
    fun getAllNotes(sortOrder: SortOrder = SortOrder.UPDATED_DESC): Flow<List<Note>> =
        localDataSource.getAllNotes().map { notes -> sortNotes(notes, sortOrder) }

    fun searchNotes(query: String): Flow<List<Note>> = localDataSource.searchNotes(query)

    fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> =
        localDataSource.getNotesByCategory(category.name.lowercase())

    suspend fun getNoteById(id: String): Note? = localDataSource.getNoteById(id)

    suspend fun createNote(title: String, content: String,
        category: NoteCategory = NoteCategory.GENERAL, colorHex: String = noteColorPalette.first()): Note {
        val now = Clock.System.now().toEpochMilliseconds()
        val note = Note(id = uuid4().toString(), title = title.trim(), content = content.trim(),
            category = category, isPinned = false, colorHex = colorHex, createdAt = now, updatedAt = now)
        localDataSource.insertNote(note)
        return note
    }

    suspend fun updateNote(id: String, title: String, content: String,
        category: NoteCategory, colorHex: String, isPinned: Boolean) {
        val existing = localDataSource.getNoteById(id) ?: return
        val updated = existing.copy(title = title.trim(), content = content.trim(),
            category = category, colorHex = colorHex, isPinned = isPinned,
            updatedAt = Clock.System.now().toEpochMilliseconds())
        localDataSource.updateNote(updated)
    }

    suspend fun deleteNote(id: String) = localDataSource.deleteNote(id)

    suspend fun togglePin(id: String) {
        val note = localDataSource.getNoteById(id) ?: return
        localDataSource.togglePin(id, !note.isPinned, Clock.System.now().toEpochMilliseconds())
    }

    private fun sortNotes(notes: List<Note>, sortOrder: SortOrder): List<Note> =
        when (sortOrder) {
            SortOrder.UPDATED_DESC -> notes.sortedByDescending { it.updatedAt }
            SortOrder.UPDATED_ASC -> notes.sortedBy { it.updatedAt }
            SortOrder.CREATED_DESC -> notes.sortedByDescending { it.createdAt }
            SortOrder.CREATED_ASC -> notes.sortedBy { it.createdAt }
            SortOrder.TITLE_ASC -> notes.sortedBy { it.title.lowercase() }
            SortOrder.TITLE_DESC -> notes.sortedByDescending { it.title.lowercase() }
        }
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\viewmodel\NotesViewModel.kt" @'
package com.notenest.app.viewmodel

import com.notenest.app.data.repository.NoteRepository
import com.notenest.app.model.Note
import com.notenest.app.model.NoteCategory
import com.notenest.app.model.SortOrder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class NotesViewModel(private val repository: NoteRepository) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<NoteCategory?>(null)
    val selectedCategory: StateFlow<NoteCategory?> = _selectedCategory.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.UPDATED_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val notes: StateFlow<List<Note>> = combine(_searchQuery, _selectedCategory, _sortOrder) { query, category, sort ->
        Triple(query, category, sort)
    }.flatMapLatest { (query, category, sort) ->
        when {
            query.isNotBlank() -> repository.searchNotes(query)
            category != null -> repository.getNotesByCategory(category)
            else -> repository.getAllNotes(sort)
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val noteCounts: StateFlow<Map<String, Int>> = notes.map { noteList ->
        buildMap {
            put("total", noteList.size)
            put("pinned", noteList.count { it.isPinned })
            NoteCategory.entries.forEach { cat -> put(cat.name, noteList.count { it.category == cat }) }
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setCategory(category: NoteCategory?) { _selectedCategory.value = category; _searchQuery.value = "" }
    fun setSortOrder(order: SortOrder) { _sortOrder.value = order }

    fun createNote(title: String, content: String, category: NoteCategory, colorHex: String) {
        if (title.isBlank() && content.isBlank()) return
        scope.launch {
            try { _isLoading.value = true; repository.createNote(title.ifBlank { "Untitled" }, content, category, colorHex) }
            catch (e: Exception) { _errorMessage.value = "Failed to create note: ${e.message}" }
            finally { _isLoading.value = false }
        }
    }

    fun updateNote(id: String, title: String, content: String, category: NoteCategory, colorHex: String, isPinned: Boolean) {
        scope.launch {
            try { repository.updateNote(id, title, content, category, colorHex, isPinned) }
            catch (e: Exception) { _errorMessage.value = "Failed to update note: ${e.message}" }
        }
    }

    fun deleteNote(id: String) {
        scope.launch {
            try { repository.deleteNote(id) }
            catch (e: Exception) { _errorMessage.value = "Failed to delete note: ${e.message}" }
        }
    }

    fun togglePin(id: String) {
        scope.launch {
            try { repository.togglePin(id) }
            catch (e: Exception) { _errorMessage.value = "Failed to pin note: ${e.message}" }
        }
    }

    fun clearError() { _errorMessage.value = null }
    fun dispose() { scope.cancel() }
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\viewmodel\SettingsViewModel.kt" @'
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
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\theme\Theme.kt" @'
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
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\theme\Typography.kt" @'
package com.notenest.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val NoteNestTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 30.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.1.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 23.sp, letterSpacing = 0.25.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 19.sp, letterSpacing = 0.25.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 15.sp, letterSpacing = 0.5.sp)
)
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\components\NoteCard.kt" @'
package com.notenest.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notenest.app.model.Note

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(note: Note, onClick: () -> Unit, onDelete: () -> Unit,
    onTogglePin: () -> Unit, compact: Boolean = false, modifier: Modifier = Modifier) {
    val noteColor = parseColor(note.colorHex)

    Card(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
        .combinedClickable(onClick = onClick, onLongClick = {}),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = noteColor.copy(alpha = 0.15f).compositeOver(MaterialTheme.colorScheme.surface)),
        border = BorderStroke(width = if (note.isPinned) 2.dp else 1.dp,
            color = if (note.isPinned) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (note.isPinned) 4.dp else 1.dp, hoveredElevation = 6.dp)) {
        Column(modifier = Modifier.padding(if (compact) 12.dp else 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                CategoryChip(emoji = note.category.emoji, label = note.category.label, color = noteColor)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isPinned) {
                        Icon(Icons.Filled.PushPin, "Pinned", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                    }
                    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(6.dp))
                        .background(noteColor.takeIf { note.colorHex != "#FFFFFF" } ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))
                }
            }
            Spacer(Modifier.height(if (compact) 6.dp else 10.dp))
            if (note.title.isNotBlank()) {
                Text(text = note.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (compact) 1 else 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
            }
            if (note.content.isNotBlank() && !compact) {
                Text(text = note.preview, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(10.dp))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = note.formattedDate, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                Row {
                    IconButton(onClick = onTogglePin, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (note.isPinned) "Unpin" else "Pin",
                            tint = if (note.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(emoji: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.25f).compositeOver(MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = emoji, style = MaterialTheme.typography.labelSmall)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}

fun parseColor(hex: String): Color {
    return try {
        val cleaned = hex.trimStart('#')
        val argb = when (cleaned.length) { 6 -> "FF$cleaned"; 8 -> cleaned; else -> "FFFFFFFF" }
        Color(argb.toLong(16).toInt())
    } catch (e: Exception) { Color.White }
}

fun Color.compositeOver(background: Color): Color {
    val alpha = this.alpha
    return Color(red = this.red * alpha + background.red * (1 - alpha),
        green = this.green * alpha + background.green * (1 - alpha),
        blue = this.blue * alpha + background.blue * (1 - alpha), alpha = 1f)
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\components\SearchBar.kt" @'
package com.notenest.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NoteSearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(value = query, onValueChange = onQueryChange, modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search notes...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
        leadingIcon = { Icon(Icons.Outlined.Search, "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
                IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Filled.Close, "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp)) }
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        singleLine = true, textStyle = MaterialTheme.typography.bodyMedium)
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\components\StateComponents.kt" @'
package com.notenest.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(title: String = "No notes yet", subtitle: String = "Click + New Note to get started",
    emoji: String = "\uD83D\uDCDD", modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = emoji, style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(text = title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Text("Loading notes...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\components\Sidebar.kt" @'
package com.notenest.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.notenest.app.model.NoteCategory

@Composable
fun AppSidebar(selectedCategory: NoteCategory?, onCategorySelected: (NoteCategory?) -> Unit,
    onSettingsClick: () -> Unit, noteCounts: Map<String, Int>, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        .padding(vertical = 24.dp, horizontal = 12.dp).verticalScroll(scrollState)) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(38.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("\uD83D\uDCD3", style = MaterialTheme.typography.titleLarge) }
            }
            Column {
                Text("NoteNest", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Text("v1.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            }
        }
        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(Modifier.height(16.dp))
        Text("LIBRARY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
        Spacer(Modifier.height(4.dp))
        SidebarItem(icon = { Icon(Icons.Outlined.Notes, null, modifier = Modifier.size(18.dp)) }, label = "All Notes", count = noteCounts["total"] ?: 0, isSelected = selectedCategory == null, onClick = { onCategorySelected(null) })
        SidebarItem(icon = { Icon(Icons.Outlined.PushPin, null, modifier = Modifier.size(18.dp)) }, label = "Pinned", count = noteCounts["pinned"] ?: 0, isSelected = false, onClick = {})
        Spacer(Modifier.height(16.dp))
        Text("CATEGORIES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
        Spacer(Modifier.height(4.dp))
        NoteCategory.entries.forEach { category ->
            SidebarItem(icon = { Text(category.emoji, style = MaterialTheme.typography.bodyMedium) }, label = category.label,
                count = noteCounts[category.name] ?: 0, isSelected = selectedCategory == category, onClick = { onCategorySelected(category) })
        }
        Spacer(Modifier.weight(1f))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(Modifier.height(12.dp))
        SidebarItem(icon = { Icon(Icons.Outlined.Settings, null, modifier = Modifier.size(18.dp)) }, label = "Settings", count = null, isSelected = false, onClick = onSettingsClick)
    }
}

@Composable
fun SidebarItem(icon: @Composable () -> Unit, label: String, count: Int?, isSelected: Boolean, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else androidx.compose.ui.graphics.Color.Transparent,
        shape = RoundedCornerShape(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CompositionLocalProvider(LocalContentColor provides if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) { icon() }
            Text(text = label, style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            if (count != null && count > 0) {
                Surface(shape = RoundedCornerShape(10.dp), color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)) {
                    Text(text = count.toString(), style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\components\NoteEditorDialog.kt" @'
package com.notenest.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.notenest.app.model.Note
import com.notenest.app.model.NoteCategory
import com.notenest.app.model.noteColorPalette

@Composable
fun NoteEditorDialog(note: Note? = null, onDismiss: () -> Unit,
    onSave: (title: String, content: String, category: NoteCategory, colorHex: String, isPinned: Boolean) -> Unit) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var selectedCategory by remember { mutableStateOf(note?.category ?: NoteCategory.GENERAL) }
    var selectedColor by remember { mutableStateOf(note?.colorHex ?: noteColorPalette.first()) }
    var isPinned by remember { mutableStateOf(note?.isPinned ?: false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    val isEditing = note != null

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isEditing) "Edit Note" else "New Note", style = MaterialTheme.typography.headlineMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { isPinned = !isPinned }) {
                            Icon(imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Default.PushPin, contentDescription = "Pin",
                                tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }
                        IconButton(onClick = onDismiss) { Icon(Icons.Outlined.Close, "Close") }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Note title...") }, shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.titleLarge, singleLine = true)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = content, onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 240.dp),
                    placeholder = { Text("Write your note here...") }, shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Category", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Box {
                        OutlinedButton(onClick = { showCategoryDropdown = true }, shape = RoundedCornerShape(10.dp)) {
                            Text("${selectedCategory.emoji} ${selectedCategory.label}")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = showCategoryDropdown, onDismissRequest = { showCategoryDropdown = false }) {
                            NoteCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) { Text(cat.emoji); Text(cat.label) } },
                                    onClick = { selectedCategory = cat; showCategoryDropdown = false },
                                    leadingIcon = if (selectedCategory == cat) { { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) } } else null)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Color", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        noteColorPalette.forEach { colorHex ->
                            val color = parseColor(colorHex); val isSelected = selectedColor == colorHex
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                                .background(if (colorHex == "#FFFFFF") MaterialTheme.colorScheme.surfaceVariant else color)
                                .border(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), shape = CircleShape)
                                .clickable { selectedColor = colorHex }, contentAlignment = Alignment.Center) {
                                if (isSelected) Icon(Icons.Default.Check, null, tint = if (colorHex == "#FFFFFF") MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Cancel") }
                    Button(onClick = { if (title.isNotBlank() || content.isNotBlank()) { onSave(title, content, selectedCategory, selectedColor, isPinned); onDismiss() } },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = title.isNotBlank() || content.isNotBlank()) {
                        Icon(if (isEditing) Icons.Default.Save else Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (isEditing) "Update" else "Save")
                    }
                }
            }
        }
    }
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\screens\MainScreen.kt" @'
package com.notenest.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notenest.app.model.Note
import com.notenest.app.model.SortOrder
import com.notenest.app.ui.components.*
import com.notenest.app.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: NotesViewModel, onNavigateToSettings: () -> Unit) {
    val notes by viewModel.notes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val noteCounts by viewModel.noteCounts.collectAsState()

    var showNewNoteDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    var useGridLayout by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMessage) { errorMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() } }

    Row(modifier = Modifier.fillMaxSize()) {
        AppSidebar(selectedCategory = selectedCategory, onCategorySelected = { viewModel.setCategory(it) },
            onSettingsClick = onNavigateToSettings, noteCounts = noteCounts, modifier = Modifier.width(220.dp))
        VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.fillMaxHeight())
        Scaffold(modifier = Modifier.weight(1f), snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = { showNewNoteDialog = true }, containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Filled.Add, "New Note", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("New Note", style = MaterialTheme.typography.labelLarge)
                }
            }, containerColor = MaterialTheme.colorScheme.background) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = selectedCategory?.let { "${it.emoji} ${it.label}" } ?: "All Notes", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
                        Text(text = "${noteCounts["total"] ?: 0} notes", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { useGridLayout = !useGridLayout }, modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                            Icon(imageVector = if (useGridLayout) Icons.Outlined.ViewList else Icons.Outlined.GridView, contentDescription = "Toggle layout", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Box {
                            IconButton(onClick = { showSortMenu = true }, modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                                Icon(Icons.Outlined.Sort, "Sort", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                                Text("Sort by", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                                SortOrder.entries.forEach { order ->
                                    DropdownMenuItem(text = { Text(order.label, style = MaterialTheme.typography.bodyMedium) },
                                        onClick = { viewModel.setSortOrder(order); showSortMenu = false },
                                        leadingIcon = if (sortOrder == order) { { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) } } else null)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                NoteSearchBar(query = searchQuery, onQueryChange = { viewModel.setSearchQuery(it) })
                Spacer(Modifier.height(16.dp))
                when {
                    isLoading -> LoadingState()
                    notes.isEmpty() && searchQuery.isNotBlank() -> EmptyState(title = "No results found", subtitle = "Try a different search term", emoji = "\uD83D\uDD0D")
                    notes.isEmpty() -> EmptyState(title = "No notes here", subtitle = "Click '+ New Note' to get started", emoji = selectedCategory?.emoji ?: "\uD83D\uDCDD")
                    else -> {
                        if (useGridLayout) {
                            LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(minSize = 240.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp), verticalItemSpacing = 12.dp,
                                contentPadding = PaddingValues(bottom = 80.dp), modifier = Modifier.fillMaxSize()) {
                                items(notes, key = { it.id }) { note ->
                                    NoteCard(note = note, onClick = { editingNote = note }, onDelete = { noteToDelete = note }, onTogglePin = { viewModel.togglePin(note.id) })
                                }
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 80.dp), modifier = Modifier.fillMaxSize()) {
                                items(notes, key = { it.id }) { note ->
                                    NoteCard(note = note, onClick = { editingNote = note }, onDelete = { noteToDelete = note }, onTogglePin = { viewModel.togglePin(note.id) }, compact = true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewNoteDialog) NoteEditorDialog(note = null, onDismiss = { showNewNoteDialog = false },
        onSave = { title, content, category, colorHex, _ -> viewModel.createNote(title, content, category, colorHex) })

    editingNote?.let { note ->
        NoteEditorDialog(note = note, onDismiss = { editingNote = null },
            onSave = { title, content, category, colorHex, isPinned -> viewModel.updateNote(note.id, title, content, category, colorHex, isPinned); editingNote = null })
    }

    noteToDelete?.let { note ->
        AlertDialog(onDismissRequest = { noteToDelete = null }, icon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Note") }, text = { Text("Delete \"${note.title.ifBlank { "Untitled" }}\"? This cannot be undone.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = { Button(onClick = { viewModel.deleteNote(note.id); noteToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Delete") } },
            dismissButton = { OutlinedButton(onClick = { noteToDelete = null }) { Text("Cancel") } }, shape = RoundedCornerShape(20.dp))
    }
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\screens\SettingsScreen.kt" @'
package com.notenest.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.notenest.app.model.AppTheme
import com.notenest.app.model.SortOrder
import com.notenest.app.ui.components.parseColor
import com.notenest.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()
    val accentOptions = listOf("#6C63FF" to "Purple","#00BCD4" to "Teal","#FF7043" to "Orange","#4CAF50" to "Green","#E91E63" to "Pink","#2196F3" to "Blue")

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.width(220.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(24.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surface, CircleShape)) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(16.dp))
            Text("Settings", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
            Text("Customize your experience", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
        VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Column(modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(scrollState).padding(28.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            SettingsSection(title = "\uD83C\uDFA8 Appearance") {
                SettingItem(label = "Theme", subtitle = "Choose app color scheme") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppTheme.entries.forEach { theme ->
                            FilterChip(selected = settings.theme == theme, onClick = { viewModel.setTheme(theme) },
                                label = { Text(theme.label, style = MaterialTheme.typography.labelMedium) },
                                leadingIcon = if (settings.theme == theme) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) } } else null)
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Accent Color", subtitle = "Highlight color for UI elements") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        accentOptions.forEach { (hex, name) ->
                            val color = parseColor(hex); val isSelected = settings.accentColor == hex
                            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color)
                                .border(width = if (isSelected) 3.dp else 0.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)
                                .clickable { viewModel.setAccentColor(hex) }, contentAlignment = Alignment.Center) {
                                if (isSelected) Icon(Icons.Default.Check, name, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            SettingsSection(title = "\uD83D\uDCCB Notes") {
                SettingItem(label = "Default Sort Order", subtitle = "How notes are ordered") {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expanded = true }, shape = RoundedCornerShape(10.dp)) {
                            Text(settings.sortOrder.label); Spacer(Modifier.width(4.dp)); Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(text = { Text(order.label, style = MaterialTheme.typography.bodyMedium) },
                                    onClick = { viewModel.setSortOrder(order); expanded = false },
                                    leadingIcon = if (settings.sortOrder == order) { { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) } } else null)
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Compact List View", subtitle = "Show notes in condensed format") { Switch(checked = settings.compactView, onCheckedChange = { viewModel.setCompactView(it) }) }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Show Pinned First", subtitle = "Display pinned notes at the top") { Switch(checked = settings.showPinnedFirst, onCheckedChange = { viewModel.setShowPinnedFirst(it) }) }
            }
            SettingsSection(title = "\u2139\uFE0F About") {
                SettingItem(label = "NoteNest", subtitle = "Pengembangan Aplikasi Mobile - ITERA") {
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)) {
                        Text("v1.0.0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Storage", subtitle = "SQLDelight + Multiplatform Settings") { Icon(Icons.Outlined.Storage, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Platform", subtitle = "Kotlin Multiplatform + Compose Desktop") { Icon(Icons.Outlined.DesktopWindows, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 8.dp))
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
            Column(modifier = Modifier.padding(4.dp)) { content() }
        }
    }
}

@Composable
fun SettingItem(label: String, subtitle: String, control: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
        control()
    }
}
'@

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\App.kt" @'
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
'@

Write-File "composeApp\src\desktopMain\kotlin\com\notenest\app\data\local\DatabaseDriverFactory.desktop.kt" @'
package com.notenest.app.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.notenest.app.db.NoteNestDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbDir = File(System.getProperty("user.home"), ".notenest")
        dbDir.mkdirs()
        val dbFile = File(dbDir, "notenest.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        try { NoteNestDatabase.Schema.create(driver) } catch (_: Exception) {}
        return driver
    }
}
'@

Write-File "composeApp\src\desktopMain\kotlin\com\notenest\app\IsSystemInDarkTheme.kt" @'
package com.notenest.app

import androidx.compose.runtime.Composable

@Composable
actual fun isSystemInDarkTheme(): Boolean {
    val osName = System.getProperty("os.name", "").lowercase()
    return when {
        osName.contains("mac") -> {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("defaults", "read", "-g", "AppleInterfaceStyle"))
                process.inputStream.bufferedReader().readText().trim().equals("Dark", ignoreCase = true)
            } catch (_: Exception) { false }
        }
        else -> false
    }
}
'@

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
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

fun main() = application {
    val driverFactory = DatabaseDriverFactory()
    val database = DatabaseProvider.getDatabase(driverFactory)
    val localDataSource = NoteLocalDataSource(database)
    val noteRepository = NoteRepository(localDataSource)
    val preferences = Preferences.userRoot().node("com.notenest.app")
    val settings = PreferencesSettings(preferences)
    val settingsManager = SettingsManager(settings)
    val notesViewModel = NotesViewModel(noteRepository)
    val settingsViewModel = SettingsViewModel(settingsManager)

    Window(onCloseRequest = { notesViewModel.dispose(); settingsViewModel.dispose(); exitApplication() },
        title = "NoteNest", state = WindowState(width = 1100.dp, height = 720.dp)) {
        App(notesViewModel = notesViewModel, settingsViewModel = settingsViewModel)
    }
}
'@

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host " SELESAI! Semua file berhasil dibuat!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Langkah selanjutnya:" -ForegroundColor Yellow
Write-Host "  1. Buka Android Studio" -ForegroundColor White
Write-Host "  2. File > Sync Project with Gradle Files" -ForegroundColor White
Write-Host "  3. Run > Run desktop" -ForegroundColor White
