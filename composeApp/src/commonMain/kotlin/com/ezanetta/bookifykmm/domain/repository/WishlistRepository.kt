package com.ezanetta.bookifykmm.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface WishlistRepository {
    val wishlist: StateFlow<Set<String>>
    fun toggle(bookKey: String)
}
