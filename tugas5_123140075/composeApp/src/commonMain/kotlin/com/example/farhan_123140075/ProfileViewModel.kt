package com.example.farhan_123140075

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    // Fungsi Masuk ke Mode Edit
    fun startEditing() {
        _uiState.update { it.copy(
            isEditing = true,
            tempName = it.name,
            tempBio = it.bio
        ) }
    }

    // Update teks saat ngetik
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(tempName = newName) }
    }

    fun onBioChange(newBio: String) {
        _uiState.update { it.copy(tempBio = newBio) }
    }

    // Simpan Perubahan
    fun saveProfile() {
        _uiState.update { it.copy(
            isEditing = false,
            name = it.tempName,
            bio = it.tempBio
        ) }
    }

    // Batal Edit
    fun cancelEdit() {
        _uiState.update { it.copy(isEditing = false) }
    }
}