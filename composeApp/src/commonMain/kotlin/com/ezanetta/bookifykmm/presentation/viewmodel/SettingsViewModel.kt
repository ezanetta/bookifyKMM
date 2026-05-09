package com.ezanetta.bookifykmm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ezanetta.bookifykmm.domain.model.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _selectedTheme = MutableStateFlow(AppTheme.SAGE)
    val selectedTheme: StateFlow<AppTheme> = _selectedTheme.asStateFlow()

    fun selectTheme(theme: AppTheme) {
        _selectedTheme.value = theme
    }
}
