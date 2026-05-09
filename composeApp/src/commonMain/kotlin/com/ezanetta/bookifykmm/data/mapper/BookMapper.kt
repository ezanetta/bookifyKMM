package com.ezanetta.bookifykmm.data.mapper

import com.ezanetta.bookifykmm.data.dto.WorkDto
import com.ezanetta.bookifykmm.domain.model.Book

private const val COVER_BASE_URL = "https://covers.openlibrary.org/b/id/"
private const val COVER_SUFFIX = "-M.jpg"
private const val MAX_SUBJECTS = 5

fun WorkDto.toDomain(): Book = Book(
    title = title.orEmpty(),
    author = authors?.firstOrNull()?.name.orEmpty(),
    subjects = subject?.take(MAX_SUBJECTS).orEmpty(),
    coverUrl = coverId?.let { "$COVER_BASE_URL$it$COVER_SUFFIX" }.orEmpty()
)
