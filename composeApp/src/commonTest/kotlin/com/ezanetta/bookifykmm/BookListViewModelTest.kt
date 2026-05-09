package com.ezanetta.bookifykmm

import app.cash.turbine.test
import com.ezanetta.bookifykmm.domain.model.Book
import com.ezanetta.bookifykmm.domain.model.Genre
import com.ezanetta.bookifykmm.domain.repository.BookRepository
import com.ezanetta.bookifykmm.presentation.viewmodel.BookListUiState
import com.ezanetta.bookifykmm.presentation.viewmodel.BookListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeBookRepository

    private val fakeFantasyBooks = listOf(
        Book(
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            subjects = listOf("Fantasy", "Adventure", "Epic"),
            coverUrl = "https://covers.openlibrary.org/b/id/12345-M.jpg"
        )
    )

    private val fakeHorrorBooks = listOf(
        Book(
            title = "It",
            author = "Stephen King",
            subjects = listOf("Horror", "Fiction"),
            coverUrl = "https://covers.openlibrary.org/b/id/99999-M.jpg"
        )
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeBookRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading before coroutine runs`() = runTest {
        repository.setup(Genre.FANTASY, Result.success(fakeFantasyBooks))
        val viewModel = BookListViewModel(repository)

        viewModel.uiState.test {
            assertTrue(awaitItem() is BookListUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `initial selected genre is Fantasy`() = runTest {
        repository.setup(Genre.FANTASY, Result.success(fakeFantasyBooks))
        val viewModel = BookListViewModel(repository)

        assertEquals(Genre.FANTASY, viewModel.selectedGenre.value)
    }

    @Test
    fun `loadBooks emits Success when repository returns books`() = runTest {
        repository.setup(Genre.FANTASY, Result.success(fakeFantasyBooks))
        val viewModel = BookListViewModel(repository)

        viewModel.uiState.test {
            assertTrue(awaitItem() is BookListUiState.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            val success = awaitItem() as BookListUiState.Success
            assertEquals(fakeFantasyBooks, success.books)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadBooks emits Success with empty list`() = runTest {
        repository.setup(Genre.FANTASY, Result.success(emptyList()))
        val viewModel = BookListViewModel(repository)

        viewModel.uiState.test {
            assertTrue(awaitItem() is BookListUiState.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            val success = awaitItem() as BookListUiState.Success
            assertTrue(success.books.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadBooks emits Error with exception message when repository fails`() = runTest {
        repository.setup(Genre.FANTASY, Result.failure(Exception("Network error")))
        val viewModel = BookListViewModel(repository)

        viewModel.uiState.test {
            assertTrue(awaitItem() is BookListUiState.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            val error = awaitItem() as BookListUiState.Error
            assertEquals("Network error", error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadBooks emits Error with default message when exception has no message`() = runTest {
        repository.setup(Genre.FANTASY, Result.failure(Exception()))
        val viewModel = BookListViewModel(repository)

        viewModel.uiState.test {
            assertTrue(awaitItem() is BookListUiState.Loading)
            testDispatcher.scheduler.advanceUntilIdle()
            val error = awaitItem() as BookListUiState.Error
            assertEquals("An unexpected error occurred", error.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadBooks resets to Loading then emits Success on retry`() = runTest {
        repository.setup(Genre.FANTASY, Result.success(fakeFantasyBooks))
        val viewModel = BookListViewModel(repository)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertTrue(awaitItem() is BookListUiState.Success)

            viewModel.loadBooks()

            assertTrue(awaitItem() is BookListUiState.Loading)

            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(awaitItem() is BookListUiState.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectGenre changes selected genre and reloads books`() = runTest {
        repository.setup(Genre.FANTASY, Result.success(fakeFantasyBooks))
        repository.setup(Genre.HORROR, Result.success(fakeHorrorBooks))
        val viewModel = BookListViewModel(repository)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            assertTrue(awaitItem() is BookListUiState.Success)

            viewModel.selectGenre(Genre.HORROR)

            assertEquals(Genre.HORROR, viewModel.selectedGenre.value)
            assertTrue(awaitItem() is BookListUiState.Loading)

            testDispatcher.scheduler.advanceUntilIdle()
            val success = awaitItem() as BookListUiState.Success
            assertEquals(fakeHorrorBooks, success.books)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selectGenre with same genre does not trigger a reload`() = runTest {
        repository.setup(Genre.FANTASY, Result.success(fakeFantasyBooks))
        val viewModel = BookListViewModel(repository)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectGenre(Genre.FANTASY)

        assertEquals(1, repository.callCount[Genre.FANTASY])
    }
}

private class FakeBookRepository : BookRepository {
    private val results = mutableMapOf<Genre, Result<List<Book>>>()
    val callCount = mutableMapOf<Genre, Int>()

    fun setup(genre: Genre, result: Result<List<Book>>) {
        results[genre] = result
    }

    override suspend fun getBooksByGenre(genre: Genre): Result<List<Book>> {
        callCount[genre] = (callCount[genre] ?: 0) + 1
        return results[genre] ?: Result.failure(Exception("Not configured for $genre"))
    }
}
