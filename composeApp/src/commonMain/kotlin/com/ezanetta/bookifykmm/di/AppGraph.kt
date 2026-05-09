package com.ezanetta.bookifykmm.di

import com.ezanetta.bookifykmm.presentation.viewmodel.BookifyViewModel
import com.ezanetta.bookifykmm.presentation.viewmodel.DetailViewModel
import com.ezanetta.bookifykmm.presentation.viewmodel.SettingsViewModel

interface AppGraph {
    fun bookifyViewModel(): BookifyViewModel
    fun detailViewModel(): DetailViewModel
    fun settingsViewModel(): SettingsViewModel
}
