package com.ezanetta.bookifykmm

import com.ezanetta.bookifykmm.data.repository.WishlistRepositoryImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WishlistRepositoryTest {

    private fun repo() = WishlistRepositoryImpl()

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `wishlist is empty on creation`() {
        assertTrue(repo().wishlist.value.isEmpty())
    }

    // ── toggle — add ──────────────────────────────────────────────────────────

    @Test
    fun `toggle adds a key that is not present`() {
        val r = repo()

        r.toggle("key1")

        assertTrue("key1" in r.wishlist.value)
    }

    @Test
    fun `toggle multiple distinct keys adds all of them`() {
        val r = repo()

        r.toggle("key1")
        r.toggle("key2")
        r.toggle("key3")

        assertEquals(setOf("key1", "key2", "key3"), r.wishlist.value)
    }

    // ── toggle — remove ───────────────────────────────────────────────────────

    @Test
    fun `toggle removes a key that is already present`() {
        val r = repo()
        r.toggle("key1")

        r.toggle("key1")

        assertFalse("key1" in r.wishlist.value)
    }

    @Test
    fun `toggling the same key twice leaves the wishlist empty`() {
        val r = repo()

        r.toggle("key1")
        r.toggle("key1")

        assertTrue(r.wishlist.value.isEmpty())
    }

    @Test
    fun `removing one key does not affect other keys`() {
        val r = repo()
        r.toggle("key1")
        r.toggle("key2")

        r.toggle("key1")

        assertFalse("key1" in r.wishlist.value)
        assertTrue("key2" in r.wishlist.value)
    }

    // ── StateFlow emission ────────────────────────────────────────────────────

    @Test
    fun `wishlist value reflects the latest state after multiple toggles`() {
        val r = repo()

        r.toggle("key1")
        r.toggle("key2")
        r.toggle("key1") // remove key1

        assertEquals(setOf("key2"), r.wishlist.value)
    }
}
