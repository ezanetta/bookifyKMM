package com.ezanetta.bookifykmm.domain.model

enum class Genre(val displayName: String, val apiValue: String) {
    FANTASY("Fantasy", "fantasy"),
    SCIENCE_FICTION("Science Fiction", "science_fiction"),
    THRILLER("Thriller", "thriller"),
    HORROR("Horror", "horror")
}
