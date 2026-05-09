package com.ezanetta.bookifykmm.data.repository

import com.ezanetta.bookifykmm.data.local.LocalStorage
import com.ezanetta.bookifykmm.domain.model.AppTheme
import com.ezanetta.bookifykmm.domain.repository.ThemeRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val KEY_THEME = "selected_theme"

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
class ThemeRepositoryImpl(private val localStorage: LocalStorage) : ThemeRepository {

    private val _selectedTheme = MutableStateFlow(loadTheme())
    override val selectedTheme: StateFlow<AppTheme> = _selectedTheme.asStateFlow()

    override fun setTheme(theme: AppTheme) {
        localStorage.putString(KEY_THEME, theme.name)
        _selectedTheme.value = theme
    }

    private fun loadTheme(): AppTheme {
        val stored = localStorage.getStringOrNull(KEY_THEME) ?: return AppTheme.SAGE
        return AppTheme.entries.find { it.name == stored } ?: AppTheme.SAGE
    }
}
