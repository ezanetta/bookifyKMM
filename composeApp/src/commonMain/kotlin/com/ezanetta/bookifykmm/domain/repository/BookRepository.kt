package com.ezanetta.bookifykmm.domain.repository

import com.ezanetta.bookifykmm.domain.model.Book
import com.ezanetta.bookifykmm.domain.model.Genre

interface BookRepository {
    suspend fun getBooksByGenre(genre: Genre): Result<List<Book>>
}
