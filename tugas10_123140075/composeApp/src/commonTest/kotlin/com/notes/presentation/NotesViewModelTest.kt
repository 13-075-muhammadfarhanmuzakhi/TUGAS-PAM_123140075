package com.notes.presentation

import app.cash.turbine.test
import com.notes.data.repository.NoteRepository
import com.notes.domain.model.Note
import com.notes.presentation.viewmodel.NotesUiState
import com.notes.presentation.viewmodel.NotesViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

/**
 * Unit tests for NotesViewModel using MockK and Turbine
 * 4+ test cases with mock repository
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<NoteRepository>()

    private val testNote = Note(id = 1L, title = "Test Note", content = "Test Content")

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // Test 1: Initial state transitions Loading -> Success
    @Test
    fun `initial state is Loading then Success with data`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns flowOf(listOf(testNote))

        val viewModel = NotesViewModel(mockRepository)

        viewModel.uiState.test {
            val loading = awaitItem()
            assertIs<NotesUiState.Loading>(loading)

            testDispatcher.scheduler.advanceUntilIdle()

            val success = awaitItem()
            assertIs<NotesUiState.Success>(success)
            assertEquals(1, success.notes.size)
            assertEquals("Test Note", success.notes[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // Test 2: addNote calls repository insertNote
    @Test
    fun `addNote calls repository insertNote with correct data`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns flowOf(emptyList())
        coEvery { mockRepository.insertNote(any()) } returns 1L

        val viewModel = NotesViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addNote("New Note", "New Content")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            mockRepository.insertNote(match { it.title == "New Note" && it.content == "New Content" })
        }
    }

    // Test 3: addNote with blank title sets Error state
    @Test
    fun `addNote with blank title sets Error state`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns flowOf(emptyList())

        val viewModel = NotesViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addNote("", "Content")

        val state = viewModel.uiState.value
        assertIs<NotesUiState.Error>(state)
        assertEquals("Title cannot be empty", state.message)

        // Verify insertNote was never called
        coVerify(exactly = 0) { mockRepository.insertNote(any()) }
    }

    // Test 4: deleteNote calls repository deleteNote
    @Test
    fun `deleteNote calls repository with correct id`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns flowOf(listOf(testNote))
        coEvery { mockRepository.deleteNote(any()) } just Runs

        val viewModel = NotesViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteNote(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { mockRepository.deleteNote(1L) }
    }

    // Test 5 (bonus): updateNote calls repository updateNote
    @Test
    fun `updateNote calls repository updateNote with updated note`() = runTest {
        coEvery { mockRepository.getAllNotes() } returns flowOf(listOf(testNote))
        coEvery { mockRepository.updateNote(any()) } just Runs

        val viewModel = NotesViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedNote = testNote.copy(title = "Updated Title")
        viewModel.updateNote(updatedNote)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockRepository.updateNote(match { it.title == "Updated Title" }) }
    }
}
