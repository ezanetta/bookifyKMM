package com.ezanetta.bookifykmm

import com.ezanetta.bookifykmm.data.repository.ThemeRepositoryImpl
import com.ezanetta.bookifykmm.domain.model.AppTheme
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeRepositoryTest {

    private fun repo(settings: MapSettings = MapSettings()) = ThemeRepositoryImpl(settings)

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `default theme is SAGE when storage is empty`() {
        assertEquals(AppTheme.SAGE, repo().selectedTheme.value)
    }

    @Test
    fun `theme is restored from storage on creation`() {
        val settings = MapSettings()
        repo(settings).setTheme(AppTheme.INK)

        assertEquals(AppTheme.INK, repo(settings).selectedTheme.value)
    }

    // ── setTheme ──────────────────────────────────────────────────────────────

    @Test
    fun `setTheme updates selectedTheme immediately`() {
        val r = repo()

        r.setTheme(AppTheme.LIBRARY)

        assertEquals(AppTheme.LIBRARY, r.selectedTheme.value)
    }

    @Test
    fun `setTheme can select every available theme`() {
        val r = repo()

        AppTheme.entries.forEach { theme ->
            r.setTheme(theme)
            assertEquals(theme, r.selectedTheme.value)
        }
    }

    @Test
    fun `setTheme with the same theme twice keeps it selected`() {
        val r = repo()
        r.setTheme(AppTheme.BONE)

        r.setTheme(AppTheme.BONE)

        assertEquals(AppTheme.BONE, r.selectedTheme.value)
    }

    @Test
    fun `theme selection can be changed multiple times`() {
        val r = repo()

        r.setTheme(AppTheme.INK)
        r.setTheme(AppTheme.BONE)
        r.setTheme(AppTheme.SAGE)

        assertEquals(AppTheme.SAGE, r.selectedTheme.value)
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    @Test
    fun `setTheme persists the selection to storage`() {
        val settings = MapSettings()
        repo(settings).setTheme(AppTheme.BONE)

        assertEquals(AppTheme.BONE, repo(settings).selectedTheme.value)
    }

    @Test
    fun `last setTheme call wins after multiple changes`() {
        val settings = MapSettings()
        val r = repo(settings)
        r.setTheme(AppTheme.INK)
        r.setTheme(AppTheme.LIBRARY)
        r.setTheme(AppTheme.BONE)

        assertEquals(AppTheme.BONE, repo(settings).selectedTheme.value)
    }
}
