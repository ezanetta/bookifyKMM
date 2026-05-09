package com.ezanetta.bookifykmm

import com.ezanetta.bookifykmm.domain.repository.WishlistRepository
import com.ezanetta.bookifykmm.presentation.viewmodel.DetailViewModel
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var wishlistRepository: FakeDetailWishlistRepository

    private val bookKey = "https://covers.openlibrary.org/b/id/1001-M.jpg"
    private val otherKey = "https://covers.openlibrary.org/b/id/2001-M.jpg"

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        wishlistRepository = FakeDetailWishlistRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `isWishlisted is false before initialize`() = runTest {
        val vm = DetailViewModel(wishlistRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.isWishlisted.value)
    }

    @Test
    fun `isWishlisted is false after initialize when book is not in wishlist`() = runTest {
        val vm = DetailViewModel(wishlistRepository)
        vm.initialize(bookKey)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.isWishlisted.value)
    }

    // ── toggleWishlist ────────────────────────────────────────────────────────

    @Test
    fun `toggleWishlist adds the book to the wishlist`() = runTest {
        val vm = DetailViewModel(wishlistRepository)
        vm.initialize(bookKey)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.toggleWishlist()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.isWishlisted.value)
        assertTrue(bookKey in wishlistRepository.wishlist.value)
    }

    @Test
    fun `toggleWishlist removes the book when it is already wishlisted`() = runTest {
        val vm = DetailViewModel(wishlistRepository)
        vm.initialize(bookKey)
        wishlistRepository.toggle(bookKey)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.toggleWishlist()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.isWishlisted.value)
        assertFalse(bookKey in wishlistRepository.wishlist.value)
    }

    @Test
    fun `toggling twice leaves the book unwishlisted`() = runTest {
        val vm = DetailViewModel(wishlistRepository)
        vm.initialize(bookKey)
        testDispatcher.scheduler.advanceUntilIdle()

        vm.toggleWishlist()
        vm.toggleWishlist()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.isWishlisted.value)
    }

    @Test
    fun `toggleWishlist before initialize does nothing`() = runTest {
        val vm = DetailViewModel(wishlistRepository)

        vm.toggleWishlist()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(wishlistRepository.wishlist.value.isEmpty())
    }

    // ── isWishlisted reacts to external repository changes ───────────────────

    @Test
    fun `isWishlisted becomes true when another caller adds the book to the repository`() = runTest {
        val vm = DetailViewModel(wishlistRepository)
        vm.initialize(bookKey)
        testDispatcher.scheduler.advanceUntilIdle()

        wishlistRepository.toggle(bookKey)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.isWishlisted.value)
    }

    @Test
    fun `isWishlisted is false for a different book in the wishlist`() = runTest {
        val vm = DetailViewModel(wishlistRepository)
        vm.initialize(bookKey)
        wishlistRepository.toggle(otherKey)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.isWishlisted.value)
    }

    @Test
    fun `initialize with a new key updates isWishlisted correctly`() = runTest {
        val vm = DetailViewModel(wishlistRepository)
        vm.initialize(bookKey)
        wishlistRepository.toggle(otherKey)
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.isWishlisted.value)

        vm.initialize(otherKey)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(vm.isWishlisted.value)
    }
}

private class FakeDetailWishlistRepository : WishlistRepository {
    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    override val wishlist: StateFlow<Set<String>> = _wishlist

    override fun toggle(bookKey: String) {
        _wishlist.value = if (bookKey in _wishlist.value) _wishlist.value - bookKey else _wishlist.value + bookKey
    }
}
