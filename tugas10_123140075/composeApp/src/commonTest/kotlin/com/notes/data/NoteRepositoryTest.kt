package com.notes.data

import app.cash.turbine.test
import com.notes.data.repository.NoteRepositoryImpl
import com.notes.domain.model.Note
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.*

/**
 * Unit tests for NoteRepositoryImpl
 * 5 test cases covering core repository operations
 */
class NoteRepositoryTest {

    private lateinit var repository: NoteRepositoryImpl

    @Before
    fun setup() {
        repository = NoteRepositoryImpl()
    }

    // Test 1: getAllNotes emits empty list initially
    @Test
    fun getAllNotes_emitsEmptyListInitially() = runTest {
        repository.getAllNotes().test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 2: insertNote adds note and emits updated list
    @Test
    fun insertNote_addsNoteAndListIsUpdated() = runTest {
        val note = Note(title = "Test Note", content = "Test Content")

        repository.getAllNotes().test {
            awaitItem() // empty initial

            val id = repository.insertNote(note)
            val updated = awaitItem()

            assertTrue(id > 0)
            assertEquals(1, updated.size)
            assertEquals("Test Note", updated[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 3: deleteNote removes note from list
    @Test
    fun deleteNote_removesNoteFromRepository() = runTest {
        val note = Note(title = "To Delete", content = "")
        val id = repository.insertNote(note)

        repository.getAllNotes().test {
            awaitItem() // list with note
            repository.deleteNote(id)
            val afterDelete = awaitItem()
            assertTrue(afterDelete.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 4: updateNote modifies existing note
    @Test
    fun updateNote_modifiesExistingNoteCorrectly() = runTest {
        val note = Note(title = "Original", content = "Old content")
        val id = repository.insertNote(note)

        repository.getAllNotes().test {
            awaitItem() // list with original

            val updated = note.copy(id = id, title = "Updated", content = "New content")
            repository.updateNote(updated)

            val result = awaitItem()
            assertEquals("Updated", result[0].title)
            assertEquals("New content", result[0].content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 5: getNoteById returns correct note
    @Test
    fun getNoteById_returnsCorrectNoteById() = runTest {
        val note1 = Note(title = "Note 1", content = "Content 1")
        val note2 = Note(title = "Note 2", content = "Content 2")
        val id1 = repository.insertNote(note1)
        repository.insertNote(note2)

        val found = repository.getNoteById(id1)
        assertNotNull(found)
        assertEquals("Note 1", found.title)
    }

    // Test 6 (bonus): deleteAllNotes clears all notes
    @Test
    fun deleteAllNotes_removesAllNotes() = runTest {
        repository.insertNote(Note(title = "A", content = ""))
        repository.insertNote(Note(title = "B", content = ""))

        repository.getAllNotes().test {
            awaitItem() // list with 2 notes
            repository.deleteAllNotes()
            val empty = awaitItem()
            assertTrue(empty.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}