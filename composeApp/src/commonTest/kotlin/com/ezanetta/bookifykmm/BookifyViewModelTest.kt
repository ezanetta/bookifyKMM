package com.ezanetta.bookifykmm

import app.cash.turbine.test
import com.ezanetta.bookifykmm.domain.model.AppTab
import com.ezanetta.bookifykmm.domain.model.Book
import com.ezanetta.bookifykmm.domain.model.Genre
import com.ezanetta.bookifykmm.domain.repository.BookRepository
import com.ezanetta.bookifykmm.domain.repository.WishlistRepository
import com.ezanetta.bookifykmm.presentation.viewmodel.BookifyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private lateinit var wishlistRepository: FakeWishlistRepository

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
        wishlistRepository = FakeWishlistRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildVm() = BookifyViewModel(repository, wishlistRepository)

    // ── Initial state ────────────────────────────────────────────────────────

    @Test
    fun `initial tab is HOME`() = runTest {
        val vm = buildVm()
        assertEquals(AppTab.HOME, vm.state.value.tab)
    }

    @Test
    fun `initial genre is FANTASY`() = runTest {
        val vm = buildVm()
        assertEquals(Genre.FANTASY, vm.state.value.genre)
    }

    @Test
    fun `initial openBook is null`() = runTest {
        val vm = buildVm()
        assertNull(vm.state.value.openBook)
    }

    @Test
    fun `initial wishlist is empty`() = runTest {
        val vm = buildVm()
        assertTrue(vm.state.value.wishlist.isEmpty())
    }

    @Test
    fun `initial loading is true before coroutines run`() = runTest {
        val vm = buildVm()
        vm.state.test {
            assertTrue(awaitItem().loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Genre loading ────────────────────────────────────────────────────────

    @Test
    fun `all four genres are loaded on init`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.state.value
        assertEquals(listOf(fantasyBook1, fantasyBook2), state.booksByGenre[Genre.FANTASY])
        assertEquals(listOf(scifiBook), state.booksByGenre[Genre.SCIENCE_FICTION])
        assertEquals(listOf(thrillerBook), state.booksByGenre[Genre.THRILLER])
        assertEquals(listOf(horrorBook), state.booksByGenre[Genre.HORROR])
    }

    @Test
    fun `loading is false after all genres load`() = runTest {
        val vm = buildVm()
        vm.state.test {
            awaitItem() // loading = true
            testDispatcher.scheduler.advanceUntilIdle()
            assertFalse(awaitItem().loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `currentBooks returns books for the selected genre`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(fantasyBook1, fantasyBook2), vm.state.value.currentBooks)
    }

    @Test
    fun `currentBooks updates after selectGenre`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.selectGenre(Genre.HORROR)

        assertEquals(listOf(horrorBook), vm.state.value.currentBooks)
    }

    @Test
    fun `selectGenre updates the genre field`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.selectGenre(Genre.SCIENCE_FICTION)

        assertEquals(Genre.SCIENCE_FICTION, vm.state.value.genre)
    }

    @Test
    fun `selectGenre with already-loaded genre does not trigger another API call`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        val callsBefore = repository.callCount[Genre.FANTASY] ?: 0
        vm.selectGenre(Genre.FANTASY) // already loaded by loadAllGenres

        assertEquals(callsBefore, repository.callCount[Genre.FANTASY])
    }

    @Test
    fun `error is set when at least one genre fails to load`() = runTest {
        repository.setup(Genre.HORROR, Result.failure(Exception("Network error")))
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Network error", vm.state.value.error)
    }

    @Test
    fun `successfully loaded genres are still available after a partial failure`() = runTest {
        repository.setup(Genre.HORROR, Result.failure(Exception("Network error")))
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(listOf(fantasyBook1, fantasyBook2), vm.state.value.booksByGenre[Genre.FANTASY])
        assertEquals(listOf(scifiBook), vm.state.value.booksByGenre[Genre.SCIENCE_FICTION])
        assertEquals(listOf(thrillerBook), vm.state.value.booksByGenre[Genre.THRILLER])
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @Test
    fun `openBook sets openBook in state`() = runTest {
        val vm = buildVm()

        vm.openBook(fantasyBook1)

        assertEquals(fantasyBook1, vm.state.value.openBook)
    }

    @Test
    fun `closeBook clears openBook`() = runTest {
        val vm = buildVm()
        vm.openBook(fantasyBook1)

        vm.closeBook()

        assertNull(vm.state.value.openBook)
    }

    @Test
    fun `selectTab updates the tab`() = runTest {
        val vm = buildVm()

        vm.selectTab(AppTab.WISHLIST)

        assertEquals(AppTab.WISHLIST, vm.state.value.tab)
    }

    @Test
    fun `selectTab clears openBook`() = runTest {
        val vm = buildVm()
        vm.openBook(fantasyBook1)

        vm.selectTab(AppTab.WISHLIST)

        assertNull(vm.state.value.openBook)
    }

    @Test
    fun `selectTab back to HOME also clears openBook`() = runTest {
        val vm = buildVm()
        vm.selectTab(AppTab.WISHLIST)
        vm.openBook(horrorBook)

        vm.selectTab(AppTab.HOME)

        assertNull(vm.state.value.openBook)
    }

    // ── Wishlist — mirrors WishlistRepository ────────────────────────────────

    @Test
    fun `wishlist in state reflects repository additions`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        wishlistRepository.toggle(fantasyBook1.key)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fantasyBook1.key in vm.state.value.wishlist)
    }

    @Test
    fun `wishlist in state reflects repository removals`() = runTest {
        val vm = buildVm()
        wishlistRepository.toggle(fantasyBook1.key)
        testDispatcher.scheduler.advanceUntilIdle()

        wishlistRepository.toggle(fantasyBook1.key)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(fantasyBook1.key in vm.state.value.wishlist)
    }

    // ── Wishlist — derived state ──────────────────────────────────────────────

    @Test
    fun `wishlistedBooks is empty when nothing is wishlisted`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.state.value.wishlistedBooks.isEmpty())
    }

    @Test
    fun `wishlistedBooks returns the correct books after adding`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        wishlistRepository.toggle(fantasyBook1.key)
        wishlistRepository.toggle(horrorBook.key)
        testDispatcher.scheduler.advanceUntilIdle()

        val wishlisted = vm.state.value.wishlistedBooks
        assertEquals(2, wishlisted.size)
        assertTrue(fantasyBook1 in wishlisted)
        assertTrue(horrorBook in wishlisted)
    }

    @Test
    fun `wishlistedBooks excludes a book after it is removed`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()
        wishlistRepository.toggle(fantasyBook1.key)
        wishlistRepository.toggle(fantasyBook2.key)
        testDispatcher.scheduler.advanceUntilIdle()

        wishlistRepository.toggle(fantasyBook1.key) // remove
        testDispatcher.scheduler.advanceUntilIdle()

        val wishlisted = vm.state.value.wishlistedBooks
        assertFalse(fantasyBook1 in wishlisted)
        assertTrue(fantasyBook2 in wishlisted)
    }

    @Test
    fun `wishlistedBooks can contain books from different genres`() = runTest {
        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        wishlistRepository.toggle(fantasyBook1.key)
        wishlistRepository.toggle(scifiBook.key)
        wishlistRepository.toggle(thrillerBook.key)
        testDispatcher.scheduler.advanceUntilIdle()

        val wishlisted = vm.state.value.wishlistedBooks
        assertTrue(fantasyBook1 in wishlisted)
        assertTrue(scifiBook in wishlisted)
        assertTrue(thrillerBook in wishlisted)
    }

    // ── Settings navigation ───────────────────────────────────────────────────

    @Test
    fun `initial openSettings is false`() = runTest {
        val vm = buildVm()
        assertFalse(vm.state.value.openSettings)
    }

    @Test
    fun `openSettings sets openSettings to true`() = runTest {
        val vm = buildVm()

        vm.openSettings()

        assertTrue(vm.state.value.openSettings)
    }

    @Test
    fun `openSettings clears openBook`() = runTest {
        val vm = buildVm()
        vm.openBook(fantasyBook1)

        vm.openSettings()

        assertNull(vm.state.value.openBook)
    }

    @Test
    fun `closeSettings sets openSettings to false`() = runTest {
        val vm = buildVm()
        vm.openSettings()

        vm.closeSettings()

        assertFalse(vm.state.value.openSettings)
    }

    @Test
    fun `selectTab closes settings`() = runTest {
        val vm = buildVm()
        vm.openSettings()

        vm.selectTab(AppTab.WISHLIST)

        assertFalse(vm.state.value.openSettings)
    }

    @Test
    fun `closeSettings does not affect other state`() = runTest {
        val vm = buildVm()
        vm.selectTab(AppTab.WISHLIST)
        wishlistRepository.toggle(fantasyBook1.key)
        testDispatcher.scheduler.advanceUntilIdle()
        vm.openSettings()

        vm.closeSettings()

        assertEquals(AppTab.WISHLIST, vm.state.value.tab)
        assertTrue(fantasyBook1.key in vm.state.value.wishlist)
    }

    // ── allBooks deduplication ────────────────────────────────────────────────

    @Test
    fun `allBooks has no duplicate keys when the same book appears in multiple genres`() = runTest {
        val duplicateBook = fantasyBook1
        repository.setup(Genre.HORROR, Result.success(listOf(duplicateBook, horrorBook)))

        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        val keys = vm.state.value.allBooks.map { it.key }
        assertEquals(keys.distinct(), keys)
    }

    @Test
    fun `wishlisting a book that appears in two genres does not create duplicates in wishlistedBooks`() = runTest {
        val duplicateBook = fantasyBook1
        repository.setup(Genre.HORROR, Result.success(listOf(duplicateBook, horrorBook)))

        val vm = buildVm()
        testDispatcher.scheduler.advanceUntilIdle()

        wishlistRepository.toggle(fantasyBook1.key)
        testDispatcher.scheduler.advanceUntilIdle()

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

private class FakeWishlistRepository : WishlistRepository {
    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    override val wishlist: StateFlow<Set<String>> = _wishlist

    override fun toggle(bookKey: String) {
        _wishlist.value = if (bookKey in _wishlist.value) _wishlist.value - bookKey else _wishlist.value + bookKey
    }
}
