package com.ezanetta.bookifykmm.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun BookifyTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalBookifyDensity provides SpaciousDensity,
        content = content,
    )
}
