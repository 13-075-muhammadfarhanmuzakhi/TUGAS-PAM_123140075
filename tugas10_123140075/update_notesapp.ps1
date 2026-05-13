# =============================================================
# NotesApp Updater - Week 10 Assignment
# Jalankan dari ROOT folder project (tempat settings.gradle.kts)
# Usage: cd "C:\Users\A s u s\AndroidStudioProjects\NotesApp"
#        .\update_notesapp.ps1
# =============================================================

Write-Host ""
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "  NotesApp Updater - Tugas Minggu 10" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

# Cek apakah di folder yang benar
if (-not (Test-Path ".\settings.gradle.kts")) {
    Write-Host "ERROR: Jalankan script ini dari ROOT folder project!" -ForegroundColor Red
    Write-Host "Contoh: cd 'C:\Users\...\NotesApp' lalu .\update_notesapp.ps1" -ForegroundColor Yellow
    exit 1
}

# ============================================================
# FILE 1: build.gradle.kts (tambah mockk ke commonTest)
# ============================================================
Write-Host "[1/3] Updating build.gradle.kts..." -ForegroundColor Yellow

$buildGradle = @'
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kover)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.coroutines.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.koin.test)
            implementation(libs.mockk)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.activity.compose)
        }

        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.compose.ui.test.junit4)
                implementation(libs.compose.ui.test.manifest)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.mockk)
            }
        }
    }
}

android {
    namespace = "com.notes"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.notes"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kover {
    reports {
        filters {
            excludes {
                classes("*Activity*", "*Application*", "*Screen*", "*.di.*")
            }
        }
    }
}
'@
Set-Content -Path ".\composeApp\build.gradle.kts" -Value $buildGradle -Encoding UTF8
Write-Host "    OK build.gradle.kts" -ForegroundColor Green

# ============================================================
# FILE 2: NotesScreen.kt (UI Redesign Total)
# ============================================================
Write-Host "[2/3] Updating NotesScreen.kt (UI Redesign)..." -ForegroundColor Yellow

$notesScreen = @'
package com.notes.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notes.domain.model.Note
import com.notes.presentation.viewmodel.NotesUiState
import com.notes.presentation.viewmodel.NotesViewModel
import org.koin.compose.viewmodel.koinViewModel

// Warna pastel untuk kartu catatan
private val noteColors = listOf(
    Color(0xFFFFF9C4), // Kuning pastel
    Color(0xFFDCEDC8), // Hijau pastel
    Color(0xFFBBDEFB), // Biru pastel
    Color(0xFFF8BBD9), // Pink pastel
    Color(0xFFE1BEE7), // Ungu pastel
    Color(0xFFFFE0B2), // Oranye pastel
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: NotesViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val noteCount = when (val s = uiState) {
        is NotesUiState.Success -> s.notes.size
        else -> 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Notes",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        )
                        val subtitle = if (noteCount == 0) "Belum ada catatan"
                                       else "$noteCount catatan tersimpan"
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag(TestTags.ADD_BUTTON),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is NotesUiState.Loading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Memuat catatan...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                is NotesUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "(!)", fontSize = 48.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is NotesUiState.Success -> {
                    if (state.notes.isEmpty()) {
                        EmptyStateView(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(TestTags.EMPTY_STATE)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(TestTags.NOTES_LIST),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.notes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onDelete = { viewModel.deleteNote(note.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddNoteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { t, c ->
                viewModel.addNote(t, c)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun EmptyStateView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(120.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "~", fontSize = 60.sp,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Belum ada catatan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Tap tombol + untuk membuat\ncatatan pertama kamu!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun NoteCard(note: Note, onDelete: () -> Unit) {
    val colorIndex = (note.id % noteColors.size).toInt()
    val bgColor = noteColors[colorIndex]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TestTags.NOTE_ITEM),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onDelete() }
                        .testTag(TestTags.DELETE_BUTTON),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "X",
                        fontSize = 13.sp,
                        color = Color(0xFF888888),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (note.content.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Divider(color = Color.Black.copy(alpha = 0.08f), thickness = 1.dp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF333333),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = relativeTime(note.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF999999)
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.06f)
                ) {
                    Text(
                        text = "  #${note.id}  ",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF777777)
                    )
                }
            }
        }
    }
}

@Composable
fun AddNoteDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Catatan Baru",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = { Text("Judul *") },
                    placeholder = { Text("Masukkan judul catatan...") },
                    isError = titleError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.TITLE_INPUT),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
                if (titleError) {
                    Text(
                        text = "Judul tidak boleh kosong!",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi catatan (opsional)") },
                    placeholder = { Text("Tulis isi catatan di sini...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag(TestTags.CONTENT_INPUT),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        onConfirm(title.trim(), content.trim())
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Simpan", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Batal")
            }
        }
    )
}

fun relativeTime(millis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - millis
    val minutes = diff / 60_000L
    val hours = minutes / 60L
    val days = hours / 24L
    return when {
        minutes < 1L  -> "Baru saja"
        minutes < 60L -> "$minutes menit lalu"
        hours < 24L   -> "$hours jam lalu"
        days < 7L     -> "$days hari lalu"
        else          -> "${days / 7L} minggu lalu"
    }
}
'@
$nsPath = ".\composeApp\src\commonMain\kotlin\com\notes\presentation\screen\NotesScreen.kt"
Set-Content -Path $nsPath -Value $notesScreen -Encoding UTF8
Write-Host "    OK NotesScreen.kt" -ForegroundColor Green

# ============================================================
# FILE 3: MainActivity.kt (Custom Theme - Indigo)
# ============================================================
Write-Host "[3/3] Updating MainActivity.kt (Custom Theme)..." -ForegroundColor Yellow

$mainActivity = @'
package com.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.notes.presentation.screen.NotesScreen

// Skema warna: Indigo / Deep Blue
private val AppColors = lightColorScheme(
    primary         = Color(0xFF3F51B5),   // Indigo
    onPrimary       = Color.White,
    primaryContainer= Color(0xFFE8EAF6),
    secondary       = Color(0xFF5C6BC0),
    onSecondary     = Color.White,
    background      = Color(0xFFF4F5FA),
    surface         = Color(0xFFFFFFFF),
    onSurface       = Color(0xFF1A1A2E),
    onBackground    = Color(0xFF1A1A2E),
    error           = Color(0xFFD32F2F),
    onError         = Color.White,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AppColors) {
                Surface {
                    NotesScreen()
                }
            }
        }
    }
}
'@
$maPath = ".\composeApp\src\androidMain\kotlin\com\notes\MainActivity.kt"
Set-Content -Path $maPath -Value $mainActivity -Encoding UTF8
Write-Host "    OK MainActivity.kt" -ForegroundColor Green

# ============================================================
# SELESAI
# ============================================================
Write-Host ""
Write-Host "=======================================" -ForegroundColor Green
Write-Host "  BERHASIL! Semua file terupdate." -ForegroundColor Green
Write-Host "=======================================" -ForegroundColor Green
Write-Host ""
Write-Host "Langkah selanjutnya:" -ForegroundColor Cyan
Write-Host "  1. Buka Android Studio" -ForegroundColor White
Write-Host "  2. Klik File > Sync Project with Gradle Files" -ForegroundColor White
Write-Host "  3. Run aplikasi (Shift+F10)" -ForegroundColor White
Write-Host ""
Write-Host "Untuk run semua test:" -ForegroundColor Cyan
Write-Host "  ./gradlew :composeApp:testDebugUnitTest" -ForegroundColor White
Write-Host "  (UI test jalankan dari device/emulator)" -ForegroundColor White
Write-Host ""
