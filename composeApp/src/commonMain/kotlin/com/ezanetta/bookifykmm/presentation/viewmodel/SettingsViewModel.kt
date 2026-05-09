package com.ezanetta.bookifykmm.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ezanetta.bookifykmm.domain.model.AppTheme
import com.ezanetta.bookifykmm.domain.repository.ThemeRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.StateFlow

@Inject
class SettingsViewModel(private val themeRepository: ThemeRepository) : ViewModel() {

    val selectedTheme: StateFlow<AppTheme> = themeRepository.selectedTheme

    fun selectTheme(theme: AppTheme) {
        themeRepository.setTheme(theme)
    }
}
