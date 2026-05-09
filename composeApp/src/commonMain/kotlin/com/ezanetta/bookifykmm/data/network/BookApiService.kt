package com.ezanetta.bookifykmm.data.network

import com.ezanetta.bookifykmm.data.dto.BookListResponseDto

interface BookApiService {
    suspend fun getBooksByGenre(genre: String, limit: Int = 20): BookListResponseDto
}
