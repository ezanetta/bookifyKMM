package com.ezanetta.bookifykmm.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezanetta.bookifykmm.presentation.theme.BookifyColors
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
        SearchButton(colors)
    }
}

@Composable
private fun SearchButton(colors: BookifyColors) {
    Surface(
        shape = CircleShape,
        color = colors.surface,
        modifier = Modifier
            .size(36.dp)
            .border(0.5.dp, Color(0x14000000), CircleShape),
    ) {
        Box(contentAlignment = Alignment.Center) {
            SearchIcon(tint = colors.ink)
        }
    }
}

@Composable
private fun SearchIcon(tint: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val cx = size.width * 0.4375f
        val cy = size.height * 0.4375f
        val r = size.width * 0.3125f
        val sw = size.width * 0.0875f
        drawCircle(
            color = tint,
            radius = r,
            center = Offset(cx, cy),
            style = Stroke(width = sw),
        )
        drawLine(
            color = tint,
            start = Offset(cx + r * 0.72f, cy + r * 0.72f),
            end = Offset(size.width * 0.9f, size.height * 0.9f),
            strokeWidth = sw,
            cap = StrokeCap.Round,
        )
    }
}
