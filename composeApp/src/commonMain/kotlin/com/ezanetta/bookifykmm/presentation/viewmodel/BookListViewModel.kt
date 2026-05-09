package com.ezanetta.bookifykmm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezanetta.bookifykmm.domain.model.Book
import com.ezanetta.bookifykmm.domain.model.Genre
import com.ezanetta.bookifykmm.domain.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BookListUiState {
    data object Loading : BookListUiState
    data class Success(val books: List<Book>) : BookListUiState
    data class Error(val message: String) : BookListUiState
}

class BookListViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _selectedGenre = MutableStateFlow(Genre.FANTASY)
    val selectedGenre: StateFlow<Genre> = _selectedGenre.asStateFlow()

    private val _uiState = MutableStateFlow<BookListUiState>(BookListUiState.Loading)
    val uiState: StateFlow<BookListUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    fun selectGenre(genre: Genre) {
        if (_selectedGenre.value == genre) return
        _selectedGenre.value = genre
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = BookListUiState.Loading
            repository.getBooksByGenre(_selectedGenre.value)
                .onSuccess { books ->
                    _uiState.value = BookListUiState.Success(books)
                }
                .onFailure { throwable ->
                    _uiState.value = BookListUiState.Error(
                        throwable.message ?: "An unexpected error occurred"
                    )
                }
        }
    }
}
