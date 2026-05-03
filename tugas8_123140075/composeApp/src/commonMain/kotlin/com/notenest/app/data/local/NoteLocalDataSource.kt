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