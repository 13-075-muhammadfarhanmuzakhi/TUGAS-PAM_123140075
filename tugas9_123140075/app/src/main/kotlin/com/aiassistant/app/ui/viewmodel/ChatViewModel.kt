package com.aiassistant.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiassistant.app.data.models.ChatMessage
import com.aiassistant.app.data.preferences.AppPreferences
import com.aiassistant.app.data.repository.GroqRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = ""
)

class ChatViewModel(private val prefs: AppPreferences) : ViewModel() {
    private val repository = GroqRepository()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isLoading) return
        val userMsg = ChatMessage(content = text, isUser = true)
        _uiState.update {
            it.copy(messages = it.messages + userMsg, inputText = "", isLoading = true, error = null)
        }
        viewModelScope.launch {
            try {
                val apiKey = "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"
                val systemPrompt = prefs.systemPrompt.first()
                repository.chat(apiKey, _uiState.value.messages, systemPrompt)
                    .onSuccess { reply ->
                        _uiState.update {
                            it.copy(
                                messages = it.messages + ChatMessage(content = reply, isUser = false),
                                isLoading = false
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                messages = it.messages + ChatMessage(
                                    content = "Error: ${error.message}",
                                    isUser = false,
                                    isError = true
                                ),
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        messages = it.messages + ChatMessage(
                            content = "Failed: ${e.message}",
                            isUser = false,
                            isError = true
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearChat() { _uiState.update { ChatUiState() } }
    fun dismissError() { _uiState.update { it.copy(error = null) } }
}