package com.ezanetta.bookifykmm

import com.ezanetta.bookifykmm.data.dto.AuthorDto
import com.ezanetta.bookifykmm.data.dto.WorkDto
import com.ezanetta.bookifykmm.data.mapper.toDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookMapperTest {

    @Test
    fun `maps title and author correctly`() {
        val dto = WorkDto(
            title = "The Hobbit",
            authors = listOf(AuthorDto(name = "J.R.R. Tolkien")),
            coverId = null,
            subject = null
        )

        val book = dto.toDomain()

        assertEquals("The Hobbit", book.title)
        assertEquals("J.R.R. Tolkien", book.author)
    }

    @Test
    fun `takes only the first 5 subjects`() {
        val dto = WorkDto(
            title = "Test",
            authors = null,
            coverId = null,
            subject = listOf("A", "B", "C", "D", "E", "F", "G")
        )

        val book = dto.toDomain()

        assertEquals(5, book.subjects.size)
        assertEquals(listOf("A", "B", "C", "D", "E"), book.subjects)
    }

    @Test
    fun `maps exactly 5 subjects without truncation`() {
        val dto = WorkDto(
            title = "Test",
            authors = null,
            coverId = null,
            subject = listOf("A", "B", "C", "D", "E")
        )

        val book = dto.toDomain()

        assertEquals(5, book.subjects.size)
    }

    @Test
    fun `passes through fewer than 5 subjects unchanged`() {
        val dto = WorkDto(
            title = "Test",
            authors = null,
            coverId = null,
            subject = listOf("Fantasy", "Fiction")
        )

        val book = dto.toDomain()

        assertEquals(listOf("Fantasy", "Fiction"), book.subjects)
    }

    @Test
    fun `null subject maps to empty list`() {
        val dto = WorkDto(
            title = "Test",
            authors = null,
            coverId = null,
            subject = null
        )

        val book = dto.toDomain()

        assertTrue(book.subjects.isEmpty())
    }

    @Test
    fun `builds cover url from coverId`() {
        val dto = WorkDto(
            title = "Test",
            authors = null,
            coverId = 12345L,
            subject = null
        )

        val book = dto.toDomain()

        assertEquals("https://covers.openlibrary.org/b/id/12345-M.jpg", book.coverUrl)
    }

    @Test
    fun `null coverId maps to empty cover url`() {
        val dto = WorkDto(
            title = "Test",
            authors = null,
            coverId = null,
            subject = null
        )

        val book = dto.toDomain()

        assertTrue(book.coverUrl.isEmpty())
    }
}
