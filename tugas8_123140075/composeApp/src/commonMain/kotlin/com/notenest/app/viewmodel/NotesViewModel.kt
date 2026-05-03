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