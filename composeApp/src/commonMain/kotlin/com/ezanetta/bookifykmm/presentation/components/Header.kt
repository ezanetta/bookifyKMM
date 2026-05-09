package com.ezanetta.bookifykmm.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezanetta.bookifykmm.presentation.theme.DmSansFamily
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyColors
import com.ezanetta.bookifykmm.presentation.theme.NewsreaderFamily

@Composable
fun BookifyHeader(
    eyebrow: String = "YOUR SHELF",
    title: String = "Bookify",
) {
    val colors = LocalBookifyColors.current
    val dmSans = DmSansFamily
    val newsreader = NewsreaderFamily

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column {
            Text(
                text = eyebrow.uppercase(),
                color = colors.muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = dmSans,
                letterSpacing = 1.6.sp,
            )
            Text(
                text = title,
                color = colors.ink,
                fontSize = 34.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                fontFamily = newsreader,
                letterSpacing = (-0.8).sp,
                lineHeight = 34.sp,
            )
        }
    }
}

