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