package com.ezanetta.bookifykmm.data.repository

import com.ezanetta.bookifykmm.data.mapper.toDomain
import com.ezanetta.bookifykmm.data.network.BookApiService
import com.ezanetta.bookifykmm.domain.model.Book
import com.ezanetta.bookifykmm.domain.model.Genre
import com.ezanetta.bookifykmm.domain.repository.BookRepository

class BookRepositoryImpl(
    private val apiService: BookApiService
) : BookRepository {

    private val cache = mutableMapOf<Genre, List<Book>>()

    override suspend fun getBooksByGenre(genre: Genre): Result<List<Book>> {
        cache[genre]?.let { return Result.success(it) }

        return runCatching {
            apiService.getBooksByGenre(genre.apiValue).works.map { it.toDomain() }
        }.onSuccess { books ->
            cache[genre] = books
        }
    }
}
