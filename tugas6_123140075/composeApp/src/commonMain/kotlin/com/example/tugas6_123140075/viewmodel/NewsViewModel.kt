package com.example.tugas6_123140075.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugas6_123140075.data.Article
import com.example.tugas6_123140075.data.NewsRepository
import com.example.tugas6_123140075.data.NewsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class NewsUiState {
    data object Loading : NewsUiState()
    data class Success(val articles: List<Article>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

data class NewsScreenState(
    val uiState: NewsUiState = NewsUiState.Loading,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
)

class NewsViewModel(
    private val repository: NewsRepository = NewsRepositoryImpl(
        apiKey = "YOUR_API_KEY_HERE",
        baseUrl = "https://newsapi.org/v2/"
    )
) : ViewModel() {

    private val _screenState = MutableStateFlow(NewsScreenState())
    val screenState: StateFlow<NewsScreenState> = _screenState.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _screenState.update { it.copy(uiState = NewsUiState.Loading) }
            repository.getTopHeadlines()
                .onSuccess { articles ->
                    _screenState.update {
                        it.copy(uiState = NewsUiState.Success(articles), isRefreshing = false)
                    }
                }
                .onFailure { error ->
                    _screenState.update {
                        it.copy(
                            uiState = NewsUiState.Error(error.message ?: "Terjadi kesalahan"),
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    fun refreshNews() {
        viewModelScope.launch {
            _screenState.update { it.copy(isRefreshing = true) }
            repository.getTopHeadlines()
                .onSuccess { articles ->
                    _screenState.update {
                        it.copy(uiState = NewsUiState.Success(articles), isRefreshing = false)
                    }
                }
                .onFailure { error ->
                    _screenState.update {
                        it.copy(
                            uiState = NewsUiState.Error(error.message ?: "Gagal memuat ulang"),
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    fun searchNews(query: String) {
        _screenState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadNews()
            return
        }
        viewModelScope.launch {
            _screenState.update { it.copy(uiState = NewsUiState.Loading) }
            repository.searchNews(query)
                .onSuccess { articles ->
                    _screenState.update { it.copy(uiState = NewsUiState.Success(articles)) }
                }
                .onFailure { error ->
                    _screenState.update {
                        it.copy(uiState = NewsUiState.Error(error.message ?: "Gagal mencari"))
                    }
                }
        }
    }
}