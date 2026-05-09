package com.ezanetta.bookifykmm.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import bookifykmm.composeapp.generated.resources.Res
import bookifykmm.composeapp.generated.resources.dm_sans_bold
import bookifykmm.composeapp.generated.resources.dm_sans_medium
import bookifykmm.composeapp.generated.resources.dm_sans_regular
import bookifykmm.composeapp.generated.resources.dm_sans_semibold
import bookifykmm.composeapp.generated.resources.newsreader_italic
import bookifykmm.composeapp.generated.resources.newsreader_medium
import bookifykmm.composeapp.generated.resources.newsreader_mediumitalic
import bookifykmm.composeapp.generated.resources.newsreader_regular
import org.jetbrains.compose.resources.Font

val NewsreaderFamily: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.newsreader_regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.newsreader_italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.newsreader_medium, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.newsreader_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    )

val DmSansFamily: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.dm_sans_regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.dm_sans_medium, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.dm_sans_semibold, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.dm_sans_bold, FontWeight.Bold, FontStyle.Normal),
    )
