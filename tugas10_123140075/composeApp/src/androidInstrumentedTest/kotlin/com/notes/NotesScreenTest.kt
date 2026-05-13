package com.notes

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.notes.data.repository.NoteRepository
import com.notes.data.repository.NoteRepositoryImpl
import com.notes.presentation.screen.NotesScreen
import com.notes.presentation.screen.TestTags
import com.notes.presentation.viewmodel.NotesViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for NotesScreen using Compose Test
 * 3+ test cases testing user interactions
 */
class NotesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NotesViewModel

    @Before
    fun setup() {
        repository = NoteRepositoryImpl()
        viewModel = NotesViewModel(repository)
    }

    private fun setContent() {
        composeTestRule.setContent {
            MaterialTheme {
                NotesScreen(viewModel = viewModel)
            }
        }
    }

    // UI Test 1: Empty state is displayed initially
    @Test
    fun notesScreen_showsEmptyState_initially() {
        setContent()
        composeTestRule
            .onNodeWithTag(TestTags.EMPTY_STATE)
            .assertIsDisplayed()
    }

    // UI Test 2: Adding a note displays it in the list
    @Test
    fun notesScreen_addNote_showsNoteInList() {
        setContent()

        composeTestRule
            .onNodeWithTag(TestTags.TITLE_INPUT)
            .performTextInput("Shopping List")

        composeTestRule
            .onNodeWithTag(TestTags.ADD_BUTTON)
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithText("Shopping List")
            .assertIsDisplayed()
    }

    // UI Test 3: Add button is displayed and enabled
    @Test
    fun notesScreen_addButton_isDisplayedAndEnabled() {
        setContent()
        composeTestRule
            .onNodeWithTag(TestTags.ADD_BUTTON)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    // UI Test 4 (bonus): Title input accepts text
    // FIX: performTextInput return Unit, tidak bisa di-chain dengan assertTextContains
    // Harus dipisah menjadi 2 baris
    @Test
    fun notesScreen_titleInput_acceptsTextInput() {
        setContent()

        composeTestRule
            .onNodeWithTag(TestTags.TITLE_INPUT)
            .performTextInput("My Title")

        // Pisah assertnya ke baris baru
        composeTestRule
            .onNodeWithTag(TestTags.TITLE_INPUT)
            .assertTextContains("My Title")
    }

    // UI Test 5 (bonus): Multiple notes displayed in list
    @Test
    fun notesScreen_multipleNotes_displayedInList() {
        setContent()

        composeTestRule.onNodeWithTag(TestTags.TITLE_INPUT).performTextInput("Note 1")
        composeTestRule.onNodeWithTag(TestTags.ADD_BUTTON).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(TestTags.TITLE_INPUT).performTextInput("Note 2")
        composeTestRule.onNodeWithTag(TestTags.ADD_BUTTON).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Note 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Note 2").assertIsDisplayed()
    }
}