package com.ezanetta.bookifykmm

import com.ezanetta.bookifykmm.data.dto.AuthorDto
import com.ezanetta.bookifykmm.data.dto.BookListResponseDto
import com.ezanetta.bookifykmm.data.dto.WorkDto
import com.ezanetta.bookifykmm.data.network.BookApiService
import com.ezanetta.bookifykmm.data.repository.BookRepositoryImpl
import com.ezanetta.bookifykmm.domain.model.Genre
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookRepositoryImplTest {

    private lateinit var fakeApiService: FakeBookApiService
    private lateinit var repository: BookRepositoryImpl

    private val fantasyResponse = BookListResponseDto(
        works = listOf(
            WorkDto(
                title = "The Hobbit",
                authors = listOf(AuthorDto(name = "J.R.R. Tolkien")),
                coverId = 12345L,
                subject = listOf("Fantasy")
            )
        )
    )

    private val horrorResponse = BookListResponseDto(
        works = listOf(
            WorkDto(
                title = "It",
                authors = listOf(AuthorDto(name = "Stephen King")),
                coverId = 99999L,
                subject = listOf("Horror")
            )
        )
    )

    @BeforeTest
    fun setup() {
        fakeApiService = FakeBookApiService()
        repository = BookRepositoryImpl(fakeApiService)
    }

    @Test
    fun `getBooksByGenre fetches from API on first request`() = runTest {
        fakeApiService.setup("fantasy", fantasyResponse)

        val result = repository.getBooksByGenre(Genre.FANTASY)

        assertTrue(result.isSuccess)
        assertEquals("The Hobbit", result.getOrNull()?.first()?.title)
        assertEquals(1, fakeApiService.callCount["fantasy"])
    }

    @Test
    fun `getBooksByGenre returns cached result without calling API on second request`() = runTest {
        fakeApiService.setup("fantasy", fantasyResponse)

        repository.getBooksByGenre(Genre.FANTASY)
        repository.getBooksByGenre(Genre.FANTASY)

        assertEquals(1, fakeApiService.callCount["fantasy"])
    }

    @Test
    fun `getBooksByGenre fetches each genre independently`() = runTest {
        fakeApiService.setup("fantasy", fantasyResponse)
        fakeApiService.setup("horror", horrorResponse)

        val fantasy = repository.getBooksByGenre(Genre.FANTASY)
        val horror = repository.getBooksByGenre(Genre.HORROR)

        assertEquals("The Hobbit", fantasy.getOrNull()?.first()?.title)
        assertEquals("It", horror.getOrNull()?.first()?.title)
        assertEquals(1, fakeApiService.callCount["fantasy"])
        assertEquals(1, fakeApiService.callCount["horror"])
    }

    @Test
    fun `getBooksByGenre does not cache a failed request`() = runTest {
        fakeApiService.setupError("fantasy", Exception("Network error"))

        val firstResult = repository.getBooksByGenre(Genre.FANTASY)
        assertTrue(firstResult.isFailure)

        fakeApiService.clearError("fantasy")
        fakeApiService.setup("fantasy", fantasyResponse)

        val secondResult = repository.getBooksByGenre(Genre.FANTASY)
        assertTrue(secondResult.isSuccess)
        assertEquals(2, fakeApiService.callCount["fantasy"])
    }

    @Test
    fun `getBooksByGenre returns failure when API throws`() = runTest {
        fakeApiService.setupError("thriller", Exception("Timeout"))

        val result = repository.getBooksByGenre(Genre.THRILLER)

        assertTrue(result.isFailure)
        assertEquals("Timeout", result.exceptionOrNull()?.message)
    }
}

private class FakeBookApiService : BookApiService {
    private val responses = mutableMapOf<String, BookListResponseDto>()
    private val errors = mutableMapOf<String, Exception>()
    val callCount = mutableMapOf<String, Int>()

    fun setup(genre: String, response: BookListResponseDto) {
        responses[genre] = response
    }

    fun setupError(genre: String, error: Exception) {
        errors[genre] = error
    }

    fun clearError(genre: String) {
        errors.remove(genre)
    }

    override suspend fun getBooksByGenre(genre: String, limit: Int): BookListResponseDto {
        callCount[genre] = (callCount[genre] ?: 0) + 1
        errors[genre]?.let { throw it }
        return responses[genre] ?: throw Exception("Not configured for $genre")
    }
}
