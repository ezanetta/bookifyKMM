package com.ezanetta.bookifykmm.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class BookifyColors(
    val bg: Color,
    val surface: Color,
    val ink: Color,
    val muted: Color,
    val accent: Color,
    val accentInk: Color,
    val dark: Boolean,
)

val SageColors = BookifyColors(
    bg = Color(0xFFE8E7D8),
    surface = Color(0xFFF4F3E8),
    ink = Color(0xFF1D2118),
    muted = Color(0xFF646B58),
    accent = Color(0xFF3A5A3A),
    accentInk = Color(0xFFF4F3E8),
    dark = false,
)

val LocalBookifyColors = staticCompositionLocalOf { SageColors }
