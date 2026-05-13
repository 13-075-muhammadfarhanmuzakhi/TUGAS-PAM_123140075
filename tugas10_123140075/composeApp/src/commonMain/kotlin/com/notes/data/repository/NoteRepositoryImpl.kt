package com.notes.data.repository

import com.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NoteRepositoryImpl : NoteRepository {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L

    override fun getAllNotes(): Flow<List<Note>> = _notes.asStateFlow()

    override suspend fun getNoteById(id: Long): Note? =
        _notes.value.find { it.id == id }

    override suspend fun insertNote(note: Note): Long {
        val id = nextId++
        _notes.update { current -> current + note.copy(id = id) }
        return id
    }

    override suspend fun updateNote(note: Note) {
        _notes.update { current ->
            current.map { if (it.id == note.id) note else it }
        }
    }

    override suspend fun deleteNote(id: Long) {
        _notes.update { current -> current.filter { it.id != id } }
    }

    override suspend fun deleteAllNotes() {
        _notes.update { emptyList() }
    }
}
