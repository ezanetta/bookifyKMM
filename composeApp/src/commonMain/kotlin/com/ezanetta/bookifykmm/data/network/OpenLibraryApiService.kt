package com.ezanetta.bookifykmm.data.network

import com.ezanetta.bookifykmm.data.dto.BookListResponseDto
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class OpenLibraryApiService(private val client: HttpClient) : BookApiService {

    override suspend fun getBooksByGenre(genre: String, limit: Int): BookListResponseDto {
        return client.get("https://openlibrary.org/subjects/$genre.json") {
            parameter("limit", limit)
        }.body()
    }
}
