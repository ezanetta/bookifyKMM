package com.ezanetta.bookifykmm

import androidx.compose.runtime.Composable
import com.ezanetta.bookifykmm.di.AppGraph
import com.ezanetta.bookifykmm.presentation.screen.BookifyScreen
import com.ezanetta.bookifykmm.presentation.theme.BookifyTheme

@Composable
fun App(graph: AppGraph) {
    BookifyTheme {
        BookifyScreen(graph = graph)
    }
}
