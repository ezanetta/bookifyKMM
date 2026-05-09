package com.ezanetta.bookifykmm

import androidx.compose.runtime.Composable
import com.ezanetta.bookifykmm.presentation.screen.BookifyScreen
import com.ezanetta.bookifykmm.presentation.theme.BookifyTheme

@Composable
fun App() {
    BookifyTheme {
        BookifyScreen()
    }
}
