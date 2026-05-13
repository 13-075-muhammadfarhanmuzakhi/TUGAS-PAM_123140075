package com.notes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.data.repository.NoteRepository
import com.notes.domain.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            repository.getAllNotes()
                .catch { e -> _uiState.value = NotesUiState.Error(e.message ?: "Unknown error") }
                .collect { notes -> _uiState.value = NotesUiState.Success(notes) }
        }
    }

    fun addNote(title: String, content: String) {
        if (title.isBlank()) {
            _uiState.value = NotesUiState.Error("Title cannot be empty")
            return
        }
        viewModelScope.launch {
            repository.insertNote(Note(title = title.trim(), content = content.trim()))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            repository.deleteAllNotes()
        }
    }
}
