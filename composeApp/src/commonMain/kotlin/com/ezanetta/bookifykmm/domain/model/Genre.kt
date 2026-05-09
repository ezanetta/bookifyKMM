package com.ezanetta.bookifykmm.domain.model

enum class Genre(val displayName: String, val apiValue: String) {
    FANTASY("Fantasy", "fantasy"),
    SCIENCE_FICTION("Sci-Fi", "science_fiction"),
    THRILLER("Thriller", "thriller"),
    HORROR("Horror", "horror")
}
