package com.ezanetta.bookifykmm

import com.ezanetta.bookifykmm.domain.model.AppTheme
import com.ezanetta.bookifykmm.presentation.viewmodel.SettingsViewModel
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsViewModelTest {

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial theme is SAGE`() {
        val vm = SettingsViewModel()
        assertEquals(AppTheme.SAGE, vm.selectedTheme.value)
    }

    // ── Theme selection ───────────────────────────────────────────────────────

    @Test
    fun `selectTheme updates selectedTheme`() {
        val vm = SettingsViewModel()

        vm.selectTheme(AppTheme.INK)

        assertEquals(AppTheme.INK, vm.selectedTheme.value)
    }

    @Test
    fun `selectTheme can select every available theme`() {
        val vm = SettingsViewModel()

        AppTheme.entries.forEach { theme ->
            vm.selectTheme(theme)
            assertEquals(theme, vm.selectedTheme.value)
        }
    }

    @Test
    fun `selecting the same theme twice keeps it selected`() {
        val vm = SettingsViewModel()
        vm.selectTheme(AppTheme.LIBRARY)

        vm.selectTheme(AppTheme.LIBRARY)

        assertEquals(AppTheme.LIBRARY, vm.selectedTheme.value)
    }

    @Test
    fun `theme selection can be changed multiple times`() {
        val vm = SettingsViewModel()

        vm.selectTheme(AppTheme.INK)
        vm.selectTheme(AppTheme.BONE)
        vm.selectTheme(AppTheme.SAGE)

        assertEquals(AppTheme.SAGE, vm.selectedTheme.value)
    }
}
