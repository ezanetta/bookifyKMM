package com.ezanetta.bookifykmm.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BookifyDensity(
    val pad: Dp,
    val gap: Dp,
    val titleSize: TextUnit,
    val titleLineHeight: Float,
    val authorSize: TextUnit,
    val tagSize: TextUnit,
    val tagPadX: Dp,
    val tagPadY: Dp,
    val tagGap: Dp,
    val listCoverW: Dp,
    val listGap: Dp,
    val sectionGap: Dp,
)

val SpaciousDensity = BookifyDensity(
    pad = 20.dp,
    gap = 16.dp,
    titleSize = 17.sp,
    titleLineHeight = 1.22f,
    authorSize = 13.sp,
    tagSize = 11.5.sp,
    tagPadX = 10.dp,
    tagPadY = 4.dp,
    tagGap = 6.dp,
    listCoverW = 84.dp,
    listGap = 16.dp,
    sectionGap = 26.dp,
)

val LocalBookifyDensity = staticCompositionLocalOf { SpaciousDensity }
