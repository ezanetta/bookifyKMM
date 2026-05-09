package com.ezanetta.bookifykmm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezanetta.bookifykmm.domain.model.AppTab
import com.ezanetta.bookifykmm.domain.model.AppTheme
import com.ezanetta.bookifykmm.domain.model.Book
import com.ezanetta.bookifykmm.domain.model.Genre
import com.ezanetta.bookifykmm.domain.repository.BookRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookifyUiState(
    val tab: AppTab = AppTab.HOME,
    val genre: Genre = Genre.FANTASY,
    val openBook: Book? = null,
    val wishlist: Set<String> = emptySet(),
    val theme: AppTheme = AppTheme.SAGE,
    // Books keyed by genre — populated lazily
    val booksByGenre: Map<Genre, List<Book>> = emptyMap(),
    val loading: Boolean = true,
    val error: String? = null,
) {
    val currentBooks: List<Book> get() = booksByGenre[genre] ?: emptyList()
    val allBooks: List<Book> get() = booksByGenre.values.flatten().distinctBy { it.key }
    val wishlistedBooks: List<Book> get() = allBooks.filter { it.key in wishlist }
}

class BookifyViewModel(
    private val repository: BookRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BookifyUiState())
    val state: StateFlow<BookifyUiState> = _state.asStateFlow()

    init {
        loadAllGenres()
    }

    fun selectGenre(genre: Genre) {
        _state.update { it.copy(genre = genre, error = null) }
        if (_state.value.booksByGenre[genre] == null) {
            loadGenre(genre)
        }
    }

    fun openBook(book: Book) {
        _state.update { it.copy(openBook = book) }
    }

    fun closeBook() {
        _state.update { it.copy(openBook = null) }
    }

    fun selectTab(tab: AppTab) {
        _state.update { it.copy(tab = tab, openBook = null) }
    }

    fun toggleWishlist(bookKey: String) {
        _state.update { state ->
            val updated = if (bookKey in state.wishlist) state.wishlist - bookKey else state.wishlist + bookKey
            state.copy(wishlist = updated)
        }
    }

    fun selectTheme(theme: AppTheme) {
        _state.update { it.copy(theme = theme) }
    }

    fun retryLoad() {
        loadGenre(_state.value.genre)
    }

    private fun loadAllGenres() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            val results = Genre.entries.map { genre ->
                async { genre to repository.getBooksByGenre(genre) }
            }.awaitAll()

            val booksByGenre = mutableMapOf<Genre, List<Book>>()
            var firstError: String? = null
            for ((genre, result) in results) {
                result
                    .onSuccess { books -> booksByGenre[genre] = books }
                    .onFailure { e -> if (firstError == null) firstError = e.message }
            }
            _state.update { it.copy(loading = false, booksByGenre = booksByGenre, error = firstError) }
        }
    }

    private fun loadGenre(genre: Genre) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            repository.getBooksByGenre(genre)
                .onSuccess { books ->
                    _state.update { it.copy(loading = false, booksByGenre = it.booksByGenre + (genre to books)) }
                }
                .onFailure { e ->
                    _state.update { it.copy(loading = false, error = e.message ?: "Failed to load books") }
                }
        }
    }
}
