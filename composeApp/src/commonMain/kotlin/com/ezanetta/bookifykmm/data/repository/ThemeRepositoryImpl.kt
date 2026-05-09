package com.ezanetta.bookifykmm.data.repository

import com.ezanetta.bookifykmm.data.local.LocalStorage
import com.ezanetta.bookifykmm.domain.model.AppTheme
import com.ezanetta.bookifykmm.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val KEY_THEME = "selected_theme"

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
