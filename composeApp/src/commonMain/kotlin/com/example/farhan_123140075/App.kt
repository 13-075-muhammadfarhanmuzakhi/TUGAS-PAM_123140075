package com.example.farhan_123140075

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun App() {
    // Pakai remember agar ViewModel tidak dibuat ulang terus
    // Ini cara paling aman di Desktop agar tidak kena exit value 1
    val viewModel = remember { ProfileViewModel() }
    ProfileScreen(viewModel)
}