package com.ezanetta.bookifykmm.data.network

import com.ezanetta.bookifykmm.data.dto.BookListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class OpenLibraryApiService(private val client: HttpClient) : BookApiService {

    override suspend fun getBooksByGenre(genre: String, limit: Int): BookListResponseDto {
        return client.get("https://openlibrary.org/subjects/$genre.json") {
            parameter("limit", limit)
        }.body()
    }
}
