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
