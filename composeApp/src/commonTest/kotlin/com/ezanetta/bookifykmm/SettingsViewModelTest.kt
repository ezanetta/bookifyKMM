package com.ezanetta.bookifykmm

import com.ezanetta.bookifykmm.domain.model.AppTheme
import com.ezanetta.bookifykmm.domain.repository.ThemeRepository
import com.ezanetta.bookifykmm.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsViewModelTest {

    private fun buildVm(repo: FakeThemeRepository = FakeThemeRepository()) = SettingsViewModel(repo)

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    fun `initial theme reflects repository value`() {
        val repo = FakeThemeRepository(initial = AppTheme.SAGE)
        assertEquals(AppTheme.SAGE, buildVm(repo).selectedTheme.value)
    }

    // ── Theme selection ───────────────────────────────────────────────────────

    @Test
    fun `selectTheme updates selectedTheme`() {
        val vm = buildVm()

        vm.selectTheme(AppTheme.INK)

        assertEquals(AppTheme.INK, vm.selectedTheme.value)
    }

    @Test
    fun `selectTheme delegates to repository`() {
        val repo = FakeThemeRepository()
        val vm = SettingsViewModel(repo)

        vm.selectTheme(AppTheme.LIBRARY)

        assertEquals(AppTheme.LIBRARY, repo.selectedTheme.value)
    }

    @Test
    fun `selectTheme can select every available theme`() {
        val vm = buildVm()

        AppTheme.entries.forEach { theme ->
            vm.selectTheme(theme)
            assertEquals(theme, vm.selectedTheme.value)
        }
    }

    @Test
    fun `selecting the same theme twice keeps it selected`() {
        val vm = buildVm()
        vm.selectTheme(AppTheme.LIBRARY)

        vm.selectTheme(AppTheme.LIBRARY)

        assertEquals(AppTheme.LIBRARY, vm.selectedTheme.value)
    }

    @Test
    fun `theme selection can be changed multiple times`() {
        val vm = buildVm()

        vm.selectTheme(AppTheme.INK)
        vm.selectTheme(AppTheme.BONE)
        vm.selectTheme(AppTheme.SAGE)

        assertEquals(AppTheme.SAGE, vm.selectedTheme.value)
    }
}

private class FakeThemeRepository(initial: AppTheme = AppTheme.SAGE) : ThemeRepository {
    private val _selectedTheme = MutableStateFlow(initial)
    override val selectedTheme: StateFlow<AppTheme> = _selectedTheme

    override fun setTheme(theme: AppTheme) {
        _selectedTheme.value = theme
    }
}
