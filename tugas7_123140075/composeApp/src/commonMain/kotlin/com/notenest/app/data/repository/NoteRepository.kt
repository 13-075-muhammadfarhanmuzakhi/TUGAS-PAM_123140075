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