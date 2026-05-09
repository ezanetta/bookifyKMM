package com.ezanetta.bookifykmm.data.repository

import com.ezanetta.bookifykmm.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WishlistRepositoryImpl : WishlistRepository {
    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    override val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    override fun toggle(bookKey: String) {
        _wishlist.value = if (bookKey in _wishlist.value) {
            _wishlist.value - bookKey
        } else {
            _wishlist.value + bookKey
        }
    }
}
