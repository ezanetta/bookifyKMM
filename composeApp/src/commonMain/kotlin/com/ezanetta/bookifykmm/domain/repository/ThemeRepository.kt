package com.ezanetta.bookifykmm.domain.repository

import com.ezanetta.bookifykmm.domain.model.AppTheme
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {
    val selectedTheme: StateFlow<AppTheme>
    fun setTheme(theme: AppTheme)
}
