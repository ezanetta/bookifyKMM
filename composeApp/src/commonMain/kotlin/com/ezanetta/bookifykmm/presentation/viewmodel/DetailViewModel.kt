package com.ezanetta.bookifykmm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezanetta.bookifykmm.domain.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class DetailViewModel(
    private val wishlistRepository: WishlistRepository,
) : ViewModel() {

    private val _bookKey = MutableStateFlow<String?>(null)

    val isWishlisted: StateFlow<Boolean> = combine(_bookKey, wishlistRepository.wishlist) { key, wishlist ->
        key != null && key in wishlist
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun initialize(bookKey: String) {
        _bookKey.value = bookKey
    }

    fun toggleWishlist() {
        val key = _bookKey.value ?: return
        wishlistRepository.toggle(key)
    }
}
