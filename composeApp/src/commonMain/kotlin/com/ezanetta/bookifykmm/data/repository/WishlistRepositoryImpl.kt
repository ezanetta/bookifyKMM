package com.ezanetta.bookifykmm.data.repository

import com.ezanetta.bookifykmm.data.local.LocalStorage
import com.ezanetta.bookifykmm.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val KEY_WISHLIST = "wishlist"
private const val DELIMITER = "|"

class WishlistRepositoryImpl(private val localStorage: LocalStorage) : WishlistRepository {

    private val _wishlist = MutableStateFlow(loadWishlist())
    override val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    override fun toggle(bookKey: String) {
        val updated = if (bookKey in _wishlist.value) _wishlist.value - bookKey else _wishlist.value + bookKey
        _wishlist.value = updated
        persistWishlist(updated)
    }

    private fun loadWishlist(): Set<String> {
        val stored = localStorage.getStringOrNull(KEY_WISHLIST) ?: return emptySet()
        return stored.split(DELIMITER).filter { it.isNotEmpty() }.toSet()
    }

    private fun persistWishlist(wishlist: Set<String>) {
        localStorage.putString(KEY_WISHLIST, wishlist.joinToString(DELIMITER))
    }
}
