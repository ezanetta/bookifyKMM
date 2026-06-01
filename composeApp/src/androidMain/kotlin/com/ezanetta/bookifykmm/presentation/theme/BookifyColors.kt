package com.ezanetta.bookifykmm.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.ezanetta.bookifykmm.domain.model.AppTheme

data class BookifyColors(
    val bg: Color,
    val surface: Color,
    val ink: Color,
    val muted: Color,
    val accent: Color,
    val accentInk: Color,
    val dark: Boolean,
)

val LibraryColors = BookifyColors(
    bg = Color(0xFFF4ECDB),
    surface = Color(0xFFFBF6E9),
    ink = Color(0xFF1F1812),
    muted = Color(0xFF6F6051),
    accent = Color(0xFF9C3E26),
    accentInk = Color(0xFFFBF6E9),
    dark = false,
)

val BoneColors = BookifyColors(
    bg = Color(0xFFF5F1EA),
    surface = Color(0xFFFFFFFF),
    ink = Color(0xFF1A1715),
    muted = Color(0xFF76706A),
    accent = Color(0xFF7A5A26),
    accentInk = Color(0xFFFFFFFF),
    dark = false,
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

val InkColors = BookifyColors(
    bg = Color(0xFF13110E),
    surface = Color(0xFF1C1916),
    ink = Color(0xFFF0E6CF),
    muted = Color(0xFF9B8F7A),
    accent = Color(0xFFD39A44),
    accentInk = Color(0xFF13110E),
    dark = true,
)

fun AppTheme.toColors(): BookifyColors = when (this) {
    AppTheme.LIBRARY -> LibraryColors
    AppTheme.BONE -> BoneColors
    AppTheme.SAGE -> SageColors
    AppTheme.INK -> InkColors
}

val LocalBookifyColors = staticCompositionLocalOf { SageColors }
