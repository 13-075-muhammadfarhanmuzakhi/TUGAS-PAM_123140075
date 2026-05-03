# ============================================================
# NoteNest Tugas 8 - Koin DI + expect/actual Platform Features
# Taruh di root folder notenest/, lalu jalankan:
# powershell -ExecutionPolicy Bypass -File setup_tugas8.ps1
# ============================================================

Write-Host "=== NoteNest Tugas 8 Setup ===" -ForegroundColor Cyan
Write-Host "Menambahkan Koin DI + DeviceInfo + NetworkMonitor + BatteryInfo..." -ForegroundColor Yellow

function Write-File {
    param([string]$Path, [string]$Content)
    $dir = Split-Path $Path -Parent
    if ($dir -and !(Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    [System.IO.File]::WriteAllText($Path, $Content, [System.Text.Encoding]::UTF8)
    Write-Host "  [OK] $Path" -ForegroundColor Green
}

# ============================================================
# 1. UPDATE build.gradle.kts - tambah koin-compose-viewmodel
# ============================================================

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
# 2. UPDATE libs.versions.toml - tambah koin-compose-viewmodel
# ============================================================

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

# ============================================================
# 3. COMMON: DeviceInfo expect class
# ============================================================

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\platform\DeviceInfo.kt" @'
package com.notenest.app.platform

expect class DeviceInfo() {
    fun getDeviceName(): String
    fun getOsName(): String
    fun getOsVersion(): String
    fun getAppVersion(): String
    fun getJvmVersion(): String
}
'@

# ============================================================
# 4. COMMON: NetworkMonitor expect class
# ============================================================

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\platform\NetworkMonitor.kt" @'
package com.notenest.app.platform

import kotlinx.coroutines.flow.Flow

expect class NetworkMonitor() {
    fun isConnected(): Boolean
    fun observeConnectivity(): Flow<Boolean>
}
'@

# ============================================================
# 5. COMMON: BatteryInfo expect class (BONUS)
# ============================================================

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\platform\BatteryInfo.kt" @'
package com.notenest.app.platform

expect class BatteryInfo() {
    fun getBatteryLevel(): Int
    fun isCharging(): Boolean
    fun getBatteryStatus(): String
}
'@

# ============================================================
# 6. DESKTOP actual: DeviceInfo
# ============================================================

Write-File "composeApp\src\desktopMain\kotlin\com\notenest\app\platform\DeviceInfo.desktop.kt" @'
package com.notenest.app.platform

import java.net.InetAddress

actual class DeviceInfo {
    actual fun getDeviceName(): String {
        return try {
            InetAddress.getLocalHost().hostName
        } catch (e: Exception) {
            System.getProperty("os.name", "Desktop") + " PC"
        }
    }

    actual fun getOsName(): String {
        return System.getProperty("os.name", "Unknown OS")
    }

    actual fun getOsVersion(): String {
        return System.getProperty("os.version", "Unknown")
    }

    actual fun getAppVersion(): String {
        return "1.0.0"
    }

    actual fun getJvmVersion(): String {
        return System.getProperty("java.version", "Unknown")
    }
}
'@

# ============================================================
# 7. DESKTOP actual: NetworkMonitor
# ============================================================

Write-File "composeApp\src\desktopMain\kotlin\com\notenest\app\platform\NetworkMonitor.desktop.kt" @'
package com.notenest.app.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.InetSocketAddress
import java.net.Socket

actual class NetworkMonitor {
    actual fun isConnected(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    actual fun observeConnectivity(): Flow<Boolean> = flow {
        var lastState = isConnected()
        emit(lastState)
        while (true) {
            delay(3000)
            val current = isConnected()
            if (current != lastState) {
                lastState = current
                emit(current)
            }
        }
    }.flowOn(Dispatchers.IO)
}
'@

# ============================================================
# 8. DESKTOP actual: BatteryInfo (BONUS)
# ============================================================

Write-File "composeApp\src\desktopMain\kotlin\com\notenest\app\platform\BatteryInfo.desktop.kt" @'
package com.notenest.app.platform

import java.io.File

actual class BatteryInfo {
    actual fun getBatteryLevel(): Int {
        return try {
            val osName = System.getProperty("os.name", "").lowercase()
            when {
                osName.contains("linux") -> {
                    val capacityFile = File("/sys/class/power_supply/BAT0/capacity")
                    if (capacityFile.exists()) capacityFile.readText().trim().toIntOrNull() ?: -1
                    else -1
                }
                osName.contains("windows") -> {
                    val process = Runtime.getRuntime().exec(
                        arrayOf("WMIC", "Path", "Win32_Battery", "Get", "EstimatedChargeRemaining")
                    )
                    val output = process.inputStream.bufferedReader().readLines()
                    output.getOrNull(1)?.trim()?.toIntOrNull() ?: -1
                }
                else -> -1
            }
        } catch (e: Exception) { -1 }
    }

    actual fun isCharging(): Boolean {
        return try {
            val osName = System.getProperty("os.name", "").lowercase()
            when {
                osName.contains("linux") -> {
                    val statusFile = File("/sys/class/power_supply/BAT0/status")
                    if (statusFile.exists()) statusFile.readText().trim() == "Charging"
                    else false
                }
                osName.contains("windows") -> {
                    val process = Runtime.getRuntime().exec(
                        arrayOf("WMIC", "Path", "Win32_Battery", "Get", "BatteryStatus")
                    )
                    val output = process.inputStream.bufferedReader().readLines()
                    val status = output.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
                    status == 2
                }
                else -> false
            }
        } catch (e: Exception) { false }
    }

    actual fun getBatteryStatus(): String {
        val level = getBatteryLevel()
        val charging = isCharging()
        return when {
            level == -1 -> "Desktop/AC Power"
            charging -> "Charging ($level%)"
            else -> "$level%"
        }
    }
}
'@

# ============================================================
# 9. COMMON: Koin AppModule
# ============================================================

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\di\AppModule.kt" @'
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
'@

# ============================================================
# 10. DESKTOP: KoinInitializer
# ============================================================

Write-File "composeApp\src\desktopMain\kotlin\com\notenest\app\di\DesktopModule.kt" @'
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
'@

# ============================================================
# 11. UPDATE App.kt - tambah KoinApplication
# ============================================================

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\App.kt" @'
package com.notenest.app

import androidx.compose.animation.*
import androidx.compose.runtime.*
import com.notenest.app.di.appModules
import com.notenest.app.model.AppTheme
import com.notenest.app.ui.screens.MainScreen
import com.notenest.app.ui.screens.SettingsScreen
import com.notenest.app.ui.theme.NoteNestTheme
import com.notenest.app.viewmodel.NotesViewModel
import com.notenest.app.viewmodel.SettingsViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

enum class Screen { MAIN, SETTINGS }

@Composable
fun App(extraModules: List<org.koin.core.module.Module> = emptyList()) {
    KoinApplication(application = {
        modules(appModules + extraModules)
    }) {
        AppContent()
    }
}

@Composable
private fun AppContent() {
    val settingsViewModel: SettingsViewModel = koinInject()
    val notesViewModel: NotesViewModel = koinInject()
    val settings by settingsViewModel.settings.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.MAIN) }

    val isDarkTheme = when (settings.theme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    NoteNestTheme(darkTheme = isDarkTheme) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn(initialAlpha = 0f) togetherWith fadeOut() }
        ) { screen ->
            when (screen) {
                Screen.MAIN -> MainScreen(
                    viewModel = notesViewModel,
                    onNavigateToSettings = { currentScreen = Screen.SETTINGS }
                )
                Screen.SETTINGS -> SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { currentScreen = Screen.MAIN }
                )
            }
        }
    }
}

@Composable
expect fun isSystemInDarkTheme(): Boolean
'@

# ============================================================
# 12. UPDATE main.kt - pakai Koin desktop module
# ============================================================

Write-File "composeApp\src\desktopMain\kotlin\com\notenest\app\main.kt" @'
package com.notenest.app

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.notenest.app.di.desktopModule

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "NoteNest",
        state = WindowState(width = 1100.dp, height = 720.dp)
    ) {
        App(extraModules = listOf(desktopModule))
    }
}
'@

# ============================================================
# 13. UPDATE IsSystemInDarkTheme.kt (tetap sama)
# ============================================================

Write-File "composeApp\src\desktopMain\kotlin\com\notenest\app\IsSystemInDarkTheme.kt" @'
package com.notenest.app

import androidx.compose.runtime.Composable

@Composable
actual fun isSystemInDarkTheme(): Boolean {
    val osName = System.getProperty("os.name", "").lowercase()
    return when {
        osName.contains("mac") -> {
            try {
                val process = Runtime.getRuntime().exec(
                    arrayOf("defaults", "read", "-g", "AppleInterfaceStyle")
                )
                process.inputStream.bufferedReader().readText().trim()
                    .equals("Dark", ignoreCase = true)
            } catch (_: Exception) { false }
        }
        else -> false
    }
}
'@

# ============================================================
# 14. UPDATE NetworkStatusBanner component
# ============================================================

Write-File "composeApp\src\commonMain\kotlin\com\notenest\app\ui\components\NetworkStatusBanner.kt" @'
package com.notenest.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notenest.app.platform.NetworkMonitor
import org.koin.compose.koinInject

@Composable
fun NetworkStatusBanner(modifier: Modifier = Modifier) {
    val networkMonitor: NetworkMonitor = koinInject()
    val isConnected by networkMonitor.observeConnectivity()
        .collectAsState(initial = true)

    AnimatedVisibility(
        visible = !isConnected,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.error,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = "Offline",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "No Internet Connection",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}
'@

# ============================================================
# 15. UPDATE MainScreen.kt - tambah NetworkStatusBanner
# ============================================================

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
    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearError() }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        AppSidebar(
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.setCategory(it) },
            onSettingsClick = onNavigateToSettings,
            noteCounts = noteCounts,
            modifier = Modifier.width(220.dp)
        )
        VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Scaffold(
            modifier = Modifier.weight(1f),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showNewNoteDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, "New Note", modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("New Note", style = MaterialTheme.typography.labelLarge)
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Network Status Banner di bagian paling atas
                NetworkStatusBanner()

                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedCategory?.let { "${it.emoji} ${it.label}" } ?: "All Notes",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${noteCounts["total"] ?: 0} notes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { useGridLayout = !useGridLayout },
                                modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (useGridLayout) Icons.Outlined.ViewList else Icons.Outlined.GridView,
                                    contentDescription = "Toggle layout",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box {
                                IconButton(
                                    onClick = { showSortMenu = true },
                                    modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                ) {
                                    Icon(Icons.Outlined.Sort, "Sort", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                                    Text("Sort by", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                                    SortOrder.entries.forEach { order ->
                                        DropdownMenuItem(
                                            text = { Text(order.label, style = MaterialTheme.typography.bodyMedium) },
                                            onClick = { viewModel.setSortOrder(order); showSortMenu = false },
                                            leadingIcon = if (sortOrder == order) { { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) } } else null
                                        )
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
                                LazyVerticalStaggeredGrid(
                                    columns = StaggeredGridCells.Adaptive(minSize = 240.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalItemSpacing = 12.dp,
                                    contentPadding = PaddingValues(bottom = 80.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(notes, key = { it.id }) { note ->
                                        NoteCard(note = note, onClick = { editingNote = note }, onDelete = { noteToDelete = note }, onTogglePin = { viewModel.togglePin(note.id) })
                                    }
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(bottom = 80.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
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
    }

    if (showNewNoteDialog) {
        NoteEditorDialog(note = null, onDismiss = { showNewNoteDialog = false }, onSave = { title, content, category, colorHex, _ -> viewModel.createNote(title, content, category, colorHex) })
    }
    editingNote?.let { note ->
        NoteEditorDialog(note = note, onDismiss = { editingNote = null }, onSave = { title, content, category, colorHex, isPinned ->
            viewModel.updateNote(note.id, title, content, category, colorHex, isPinned); editingNote = null })
    }
    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            icon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Note") },
            text = { Text("Delete \"${note.title.ifBlank { "Untitled" }}\"? This cannot be undone.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = { Button(onClick = { viewModel.deleteNote(note.id); noteToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Delete") } },
            dismissButton = { OutlinedButton(onClick = { noteToDelete = null }) { Text("Cancel") } },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
'@

# ============================================================
# 16. UPDATE SettingsScreen.kt - tambah Device Info & Battery section
# ============================================================

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
import com.notenest.app.platform.BatteryInfo
import com.notenest.app.platform.DeviceInfo
import com.notenest.app.platform.NetworkMonitor
import com.notenest.app.ui.components.parseColor
import com.notenest.app.viewmodel.SettingsViewModel
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()
    val deviceInfo: DeviceInfo = koinInject()
    val networkMonitor: NetworkMonitor = koinInject()
    val batteryInfo: BatteryInfo = koinInject()
    val isConnected by networkMonitor.observeConnectivity().collectAsState(initial = true)

    val accentOptions = listOf(
        "#6C63FF" to "Purple", "#00BCD4" to "Teal", "#FF7043" to "Orange",
        "#4CAF50" to "Green", "#E91E63" to "Pink", "#2196F3" to "Blue"
    )

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.width(220.dp).fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(Modifier.height(16.dp))
            Text("Settings", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
            Text("Customize your experience", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            Spacer(Modifier.height(24.dp))
            listOf("\uD83C\uDFA8" to "Appearance", "\uD83D\uDCCB" to "Notes", "\uD83D\uDCF1" to "Device Info", "\u2139\uFE0F" to "About").forEachIndexed { i, (emoji, label) ->
                Surface(
                    color = if (i == 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(emoji, style = MaterialTheme.typography.bodyMedium)
                        Text(label, style = MaterialTheme.typography.bodyMedium, color = if (i == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        VerticalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        Column(
            modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(scrollState).padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Appearance
            SettingsSection(title = "\uD83C\uDFA8 Appearance") {
                SettingItem(label = "Theme", subtitle = "Choose app color scheme") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppTheme.entries.forEach { theme ->
                            FilterChip(
                                selected = settings.theme == theme,
                                onClick = { viewModel.setTheme(theme) },
                                label = { Text(theme.label, style = MaterialTheme.typography.labelMedium) },
                                leadingIcon = if (settings.theme == theme) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) } } else null
                            )
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Accent Color", subtitle = "Highlight color for UI elements") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        accentOptions.forEach { (hex, name) ->
                            val color = parseColor(hex)
                            val isSelected = settings.accentColor == hex
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(color)
                                    .border(width = if (isSelected) 3.dp else 0.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)
                                    .clickable { viewModel.setAccentColor(hex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) Icon(Icons.Default.Check, name, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Notes
            SettingsSection(title = "\uD83D\uDCCB Notes") {
                SettingItem(label = "Default Sort Order", subtitle = "How notes are ordered") {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expanded = true }, shape = RoundedCornerShape(10.dp)) {
                            Text(settings.sortOrder.label)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            SortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = { Text(order.label, style = MaterialTheme.typography.bodyMedium) },
                                    onClick = { viewModel.setSortOrder(order); expanded = false },
                                    leadingIcon = if (settings.sortOrder == order) { { Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) } } else null
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Compact List View", subtitle = "Show notes in condensed format") {
                    Switch(checked = settings.compactView, onCheckedChange = { viewModel.setCompactView(it) })
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Show Pinned First", subtitle = "Display pinned notes at the top") {
                    Switch(checked = settings.showPinnedFirst, onCheckedChange = { viewModel.setShowPinnedFirst(it) })
                }
            }

            // Device Info (dari expect/actual DeviceInfo)
            SettingsSection(title = "\uD83D\uDCF1 Device Info") {
                SettingItem(label = "Device Name", subtitle = "Current machine hostname") {
                    Text(deviceInfo.getDeviceName(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Operating System", subtitle = deviceInfo.getOsName()) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)) {
                        Text(deviceInfo.getOsVersion(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Network Status", subtitle = "Current internet connectivity") {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isConnected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error))
                        Text(
                            text = if (isConnected) "Connected" else "Offline",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isConnected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Battery", subtitle = "Power status") {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = if (batteryInfo.isCharging()) Icons.Default.BatteryChargingFull else Icons.Default.Battery5Bar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(batteryInfo.getBatteryStatus(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "JVM Version", subtitle = "Java runtime version") {
                    Text(deviceInfo.getJvmVersion(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // About
            SettingsSection(title = "\u2139\uFE0F About") {
                SettingItem(label = "NoteNest", subtitle = "Pengembangan Aplikasi Mobile - ITERA") {
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)) {
                        Text("v1.0.0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "Storage", subtitle = "SQLDelight + Multiplatform Settings") {
                    Icon(Icons.Outlined.Storage, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "DI Framework", subtitle = "Koin 4.0 - Dependency Injection") {
                    Icon(Icons.Outlined.AccountTree, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                SettingItem(label = "App Version", subtitle = deviceInfo.getAppVersion()) {
                    Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 8.dp))
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))) {
            Column(modifier = Modifier.padding(4.dp)) { content() }
        }
    }
}

@Composable
fun SettingItem(label: String, subtitle: String, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
        control()
    }
}
'@

# ============================================================
# DONE
# ============================================================

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host " Tugas 8 berhasil di-setup!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "File baru yang ditambahkan:" -ForegroundColor Yellow
Write-Host "  + platform/DeviceInfo.kt (expect)" -ForegroundColor White
Write-Host "  + platform/NetworkMonitor.kt (expect)" -ForegroundColor White
Write-Host "  + platform/BatteryInfo.kt (expect + BONUS)" -ForegroundColor White
Write-Host "  + platform/DeviceInfo.desktop.kt (actual)" -ForegroundColor White
Write-Host "  + platform/NetworkMonitor.desktop.kt (actual)" -ForegroundColor White
Write-Host "  + platform/BatteryInfo.desktop.kt (actual)" -ForegroundColor White
Write-Host "  + di/AppModule.kt (Koin modules)" -ForegroundColor White
Write-Host "  + di/DesktopModule.kt (Koin desktop)" -ForegroundColor White
Write-Host "  + ui/components/NetworkStatusBanner.kt" -ForegroundColor White
Write-Host ""
Write-Host "File yang diupdate:" -ForegroundColor Yellow
Write-Host "  ~ App.kt (KoinApplication)" -ForegroundColor White
Write-Host "  ~ main.kt (pakai Koin)" -ForegroundColor White
Write-Host "  ~ MainScreen.kt (NetworkStatusBanner)" -ForegroundColor White
Write-Host "  ~ SettingsScreen.kt (Device Info section)" -ForegroundColor White
Write-Host ""
Write-Host "Sekarang jalankan:" -ForegroundColor Yellow
Write-Host '  $env:JAVA_HOME = "D:\D sementara\androidstudio\jbr"' -ForegroundColor Cyan
Write-Host "  .\gradlew :composeApp:run --no-configuration-cache" -ForegroundColor Cyan
Write-Host ""
