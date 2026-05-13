package com.notes.presentation

import app.cash.turbine.test
import com.notes.data.repository.NoteRepositoryImpl
import com.notes.domain.model.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.*

/**
 * Flow tests using Turbine
 * Tests reactive data stream behavior
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotesFlowTest {

    private lateinit var repository: NoteRepositoryImpl

    @BeforeTest
    fun setup() {
        repository = NoteRepositoryImpl()
    }

    // Flow Test 1: Flow emits multiple states as notes are added
    @Test
    fun `flow emits updated list on each note insertion`() = runTest {
        repository.getAllNotes().test {
            val empty = awaitItem()
            assertTrue(empty.isEmpty())

            repository.insertNote(Note(title = "Note 1", content = ""))
            val one = awaitItem()
            assertEquals(1, one.size)

            repository.insertNote(Note(title = "Note 2", content = ""))
            val two = awaitItem()
            assertEquals(2, two.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // Flow Test 2: Flow emits correct state after delete
    @Test
    fun `flow emits correct state after delete operation`() = runTest {
        val id = repository.insertNote(Note(title = "Delete Me", content = ""))

        repository.getAllNotes().test {
            val withNote = awaitItem()
            assertEquals(1, withNote.size)

            repository.deleteNote(id)

            val afterDelete = awaitItem()
            assertTrue(afterDelete.isEmpty())
            assertNull(afterDelete.find { it.id == id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    // Flow Test 3: Flow emits correct state after update
    // FIX: insertNote dulu SEBELUM masuk .test{} block, bukan di luar
    @Test
    fun `flow emits updated note after update operation`() = runTest {
        // Arrange: insert note DULU sebelum mulai observe flow
        val id = repository.insertNote(Note(title = "Old Title", content = "Old"))

        repository.getAllNotes().test {
            // Consume item pertama (list dengan note asli)
            val initial = awaitItem()
            assertEquals(1, initial.size)
            assertEquals("Old Title", initial[0].title)

            // Act: update di dalam block test
            repository.updateNote(Note(id = id, title = "New Title", content = "New"))

            // Assert: tunggu emission berikutnya
            val updated = awaitItem()
            assertEquals(1, updated.size)
            assertEquals("New Title", updated[0].title)
            assertEquals("New", updated[0].content)

            cancelAndIgnoreRemainingEvents()
        }
    }
}