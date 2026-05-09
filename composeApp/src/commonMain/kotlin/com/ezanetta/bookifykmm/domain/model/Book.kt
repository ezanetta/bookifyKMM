package com.ezanetta.bookifykmm.domain.model

data class Book(
    val title: String,
    val author: String,
    val subjects: List<String>,
    val coverUrl: String,
) {
    val key: String get() = coverUrl.ifEmpty { "${title}_$author" }
}
