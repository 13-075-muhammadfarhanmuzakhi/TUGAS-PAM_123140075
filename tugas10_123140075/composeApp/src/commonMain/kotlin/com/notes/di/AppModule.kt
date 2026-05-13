package com.notes.di

import com.notes.data.repository.NoteRepository
import com.notes.data.repository.NoteRepositoryImpl
import com.notes.presentation.viewmodel.NotesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// Data Module - handles data layer dependencies
val dataModule = module {
    single<NoteRepository> { NoteRepositoryImpl() }
}

// ViewModel Module - handles presentation layer dependencies
val viewModelModule = module {
    viewModel { NotesViewModel(get()) }
}

// All modules combined
val appModules = listOf(dataModule, viewModelModule)
