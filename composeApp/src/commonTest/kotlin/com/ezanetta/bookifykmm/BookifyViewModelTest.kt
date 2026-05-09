package com.ezanetta.bookifykmm

import app.cash.turbine.test
import com.ezanetta.bookifykmm.domain.model.AppTab
import com.ezanetta.bookifykmm.domain.model.Book
import com.ezanetta.bookifykmm.domain.model.Genre
import com.ezanetta.bookifykmm.domain.repository.BookRepository
import com.ezanetta.bookifykmm.presentation.viewmodel.BookifyViewModel
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookifyViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeMultiGenreRepository

    private val fantasyBook1 = Book(
        title = "Alice's Adventures in Wonderland",
        author = "Lewis Carroll",
        subjects = listOf("Fantasy", "Children"),
        coverUrl = "https://covers.openlibrary.org/b/id/1001-M.jpg",
    )
    private val fantasyBook2 = Book(
        title = "The Wonderful Wizard of Oz",
        author = "L. Frank Baum",
        subjects = listOf("Fantasy", "Adventure"),
        coverUrl = "https://covers.openlibrary.org/b/id/1002-M.jpg",
    )
    private val scifiBook = Book(
        title = "The Time Machine",
        author = "H. G. Wells",
        subjects = listOf("Science Fiction"),
        coverUrl = "https://covers.openlibrary.org/b/id/2001-M.jpg",
    )
    private val thrillerBook = Book(
        title = "The Hound of the Baskervilles",
        author = "Arthur Conan Doyle",
        subjects = listOf("Detective", "Thriller"),
        coverUrl = "https://covers.openlibrary.org/b/id/3001-M.jpg",
    )
    private val horrorBook = Book(
        title = "Dracula",
        author = "Bram Stoker",
        subjects = listOf("Horror", "Gothic"),
        coverUrl = "https://covers.openlibrary.org/b/id/4001-M.jpg",
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMultiGenreRepository().apply {
            setup(Genre.FANTASY, Result.success(listOf(fantasyBook1, fantasyBook2)))
            setup(Genre.SCIENCE_FICTION, Result.success(listOf(scifiBook)))
            setup(Genre.THRILLER, Result.success(listOf(thrillerBook)))
            setup(Genre.HORROR, Result.success(listOf(horrorBook)))
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial state ────────────────────────────────────────────────────────

    @Test
    fun `initial tab is HOME`() = runTest {
        val vm = BookifyViewModel(repository)
        assertEquals(AppTab.HOME, vm.state.value.tab)
    }

    @Test
    fun `initial genre is FANTASY`() = runTest {
        val vm = BookifyViewModel(repository)
        assertEquals(Genre.FANTASY, vm.state.value.genre)
    }

    @Test
    fun `initial openBook is null`() = runTest {
        val vm = BookifyViewModel(repository)
        assertNull(vm.state.value.openBook)
    }

    @Test
    fun `initial wishlist is empty`() = runTest {
        val vm = BookifyViewModel(repository)
        assertTrue(vm.state.value.wishlist.isEmpty())
    }

    @Test
    fun `initial loading is true before coroutines run`() = runTest {
        val vm = BookifyViewModel(repository)
        vm.state.test {
            assertTrue(awaitItem().loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Genre loading ────────────────────────────────────────────────────────

    @Test
    fun `all four genres are loaded on init`() = runTest {
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value
        assertEquals(listOf(fantasyBook1, fantasyBook2), state.booksByGenre[Genre.FANTASY])
        assertEquals(listOf(scifiBook), state.booksByGenre[Genre.SCIENCE_FICTION])
        assertEquals(listOf(thrillerBook), state.booksByGenre[Genre.THRILLER])
        assertEquals(listOf(horrorBook), state.booksByGenre[Genre.HORROR])
    }

    @Test
    fun `loading is false after all genres load`() = runTest {
        val vm = BookifyViewModel(repository)
        vm.state.test {
            awaitItem() // loading = true
            testDispatcher.scheduler.advanceUntilIdle()
            assertFalse(awaitItem().loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `currentBooks returns books for the selected genre`() = runTest {
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(fantasyBook1, fantasyBook2), vm.state.value.currentBooks)
    }

    @Test
    fun `currentBooks updates after selectGenre`() = runTest {
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.selectGenre(Genre.HORROR)

        assertEquals(listOf(horrorBook), vm.state.value.currentBooks)
    }

    @Test
    fun `selectGenre updates the genre field`() = runTest {
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.selectGenre(Genre.SCIENCE_FICTION)

        assertEquals(Genre.SCIENCE_FICTION, vm.state.value.genre)
    }

    @Test
    fun `selectGenre with already-loaded genre does not trigger another API call`() = runTest {
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val callsBefore = repository.callCount[Genre.FANTASY] ?: 0
        vm.selectGenre(Genre.FANTASY) // already loaded by loadAllGenres

        assertEquals(callsBefore, repository.callCount[Genre.FANTASY])
    }

    @Test
    fun `error is set when at least one genre fails to load`() = runTest {
        repository.setup(Genre.HORROR, Result.failure(Exception("Network error")))
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Network error", vm.state.value.error)
    }

    @Test
    fun `successfully loaded genres are still available after a partial failure`() = runTest {
        repository.setup(Genre.HORROR, Result.failure(Exception("Network error")))
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(fantasyBook1, fantasyBook2), vm.state.value.booksByGenre[Genre.FANTASY])
        assertEquals(listOf(scifiBook), vm.state.value.booksByGenre[Genre.SCIENCE_FICTION])
        assertEquals(listOf(thrillerBook), vm.state.value.booksByGenre[Genre.THRILLER])
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @Test
    fun `openBook sets openBook in state`() = runTest {
        val vm = BookifyViewModel(repository)

        vm.openBook(fantasyBook1)

        assertEquals(fantasyBook1, vm.state.value.openBook)
    }

    @Test
    fun `closeBook clears openBook`() = runTest {
        val vm = BookifyViewModel(repository)
        vm.openBook(fantasyBook1)

        vm.closeBook()

        assertNull(vm.state.value.openBook)
    }

    @Test
    fun `selectTab updates the tab`() = runTest {
        val vm = BookifyViewModel(repository)

        vm.selectTab(AppTab.WISHLIST)

        assertEquals(AppTab.WISHLIST, vm.state.value.tab)
    }

    @Test
    fun `selectTab clears openBook`() = runTest {
        val vm = BookifyViewModel(repository)
        vm.openBook(fantasyBook1)

        vm.selectTab(AppTab.WISHLIST)

        assertNull(vm.state.value.openBook)
    }

    @Test
    fun `selectTab back to HOME also clears openBook`() = runTest {
        val vm = BookifyViewModel(repository)
        vm.selectTab(AppTab.WISHLIST)
        vm.openBook(horrorBook)

        vm.selectTab(AppTab.HOME)

        assertNull(vm.state.value.openBook)
    }

    // ── Wishlist — add / remove ───────────────────────────────────────────────

    @Test
    fun `toggleWishlist adds the book key when it is not in the wishlist`() = runTest {
        val vm = BookifyViewModel(repository)

        vm.toggleWishlist(fantasyBook1.key)

        assertTrue(fantasyBook1.key in vm.state.value.wishlist)
    }

    @Test
    fun `toggleWishlist removes the book key when it is already in the wishlist`() = runTest {
        val vm = BookifyViewModel(repository)
        vm.toggleWishlist(fantasyBook1.key)

        vm.toggleWishlist(fantasyBook1.key)

        assertFalse(fantasyBook1.key in vm.state.value.wishlist)
    }

    @Test
    fun `toggling the same book twice leaves the wishlist empty`() = runTest {
        val vm = BookifyViewModel(repository)

        vm.toggleWishlist(fantasyBook1.key)
        vm.toggleWishlist(fantasyBook1.key)

        assertTrue(vm.state.value.wishlist.isEmpty())
    }

    @Test
    fun `multiple books can be wishlisted independently`() = runTest {
        val vm = BookifyViewModel(repository)

        vm.toggleWishlist(fantasyBook1.key)
        vm.toggleWishlist(fantasyBook2.key)
        vm.toggleWishlist(horrorBook.key)

        assertEquals(3, vm.state.value.wishlist.size)
        assertTrue(fantasyBook1.key in vm.state.value.wishlist)
        assertTrue(fantasyBook2.key in vm.state.value.wishlist)
        assertTrue(horrorBook.key in vm.state.value.wishlist)
    }

    @Test
    fun `removing one book does not affect other wishlisted books`() = runTest {
        val vm = BookifyViewModel(repository)
        vm.toggleWishlist(fantasyBook1.key)
        vm.toggleWishlist(fantasyBook2.key)

        vm.toggleWishlist(fantasyBook1.key) // remove only fantasyBook1

        assertFalse(fantasyBook1.key in vm.state.value.wishlist)
        assertTrue(fantasyBook2.key in vm.state.value.wishlist)
    }

    // ── Wishlist — derived state ──────────────────────────────────────────────

    @Test
    fun `wishlistedBooks is empty when nothing is wishlisted`() = runTest {
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.state.value.wishlistedBooks.isEmpty())
    }

    @Test
    fun `wishlistedBooks returns the correct books after adding`() = runTest {
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.toggleWishlist(fantasyBook1.key)
        vm.toggleWishlist(horrorBook.key)

        val wishlisted = vm.state.value.wishlistedBooks
        assertEquals(2, wishlisted.size)
        assertTrue(fantasyBook1 in wishlisted)
        assertTrue(horrorBook in wishlisted)
    }

    @Test
    fun `wishlistedBooks excludes a book after it is removed`() = runTest {
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        vm.toggleWishlist(fantasyBook1.key)
        vm.toggleWishlist(fantasyBook2.key)

        vm.toggleWishlist(fantasyBook1.key) // remove

        val wishlisted = vm.state.value.wishlistedBooks
        assertFalse(fantasyBook1 in wishlisted)
        assertTrue(fantasyBook2 in wishlisted)
    }

    @Test
    fun `wishlistedBooks can contain books from different genres`() = runTest {
        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.toggleWishlist(fantasyBook1.key)
        vm.toggleWishlist(scifiBook.key)
        vm.toggleWishlist(thrillerBook.key)

        val wishlisted = vm.state.value.wishlistedBooks
        assertTrue(fantasyBook1 in wishlisted)
        assertTrue(scifiBook in wishlisted)
        assertTrue(thrillerBook in wishlisted)
    }

    // ── allBooks deduplication ────────────────────────────────────────────────

    @Test
    fun `allBooks has no duplicate keys when the same book appears in multiple genres`() = runTest {
        // API sometimes returns the same work in two different genre buckets
        val duplicateBook = fantasyBook1 // same coverUrl → same key
        repository.setup(Genre.HORROR, Result.success(listOf(duplicateBook, horrorBook)))

        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val keys = vm.state.value.allBooks.map { it.key }
        assertEquals(keys.distinct(), keys)
    }

    @Test
    fun `wishlisting a book that appears in two genres does not create duplicates in wishlistedBooks`() = runTest {
        val duplicateBook = fantasyBook1
        repository.setup(Genre.HORROR, Result.success(listOf(duplicateBook, horrorBook)))

        val vm = BookifyViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.toggleWishlist(fantasyBook1.key)

        assertEquals(1, vm.state.value.wishlistedBooks.size)
    }
}

private class FakeMultiGenreRepository : BookRepository {
    private val results = mutableMapOf<Genre, Result<List<Book>>>()
    val callCount = mutableMapOf<Genre, Int>()

    fun setup(genre: Genre, result: Result<List<Book>>) {
        results[genre] = result
    }

    override suspend fun getBooksByGenre(genre: Genre): Result<List<Book>> {
        callCount[genre] = (callCount[genre] ?: 0) + 1
        return results[genre] ?: Result.success(emptyList())
    }
}
