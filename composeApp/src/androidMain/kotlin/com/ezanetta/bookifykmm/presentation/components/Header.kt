package com.ezanetta.bookifykmm.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
    onSettingsClick: () -> Unit = {},
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
        GearButton(onClick = onSettingsClick)
    }
}

@Composable
private fun GearButton(onClick: () -> Unit) {
    val colors = LocalBookifyColors.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(colors.surface)
            .border(
                0.5.dp,
                if (colors.dark) Color(0x14FFFFFF) else Color(0x14000000),
                CircleShape,
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
    ) {
        GearIcon(tint = colors.ink)
    }
}

@Composable
private fun GearIcon(tint: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val sw = size.width * 0.09f
        val outerR = size.width * 0.46f
        val innerR = size.width * 0.31f
        val holeR = size.width * 0.14f
        val teeth = 8
        val total = teeth * 2

        val path = Path()
        repeat(total) { i ->
            val angle = (i.toDouble() / total * 2 * kotlin.math.PI - kotlin.math.PI / 2).toFloat()
            val radius = if (i % 2 == 0) outerR else innerR
            val x = cx + radius * kotlin.math.cos(angle.toDouble()).toFloat()
            val y = cy + radius * kotlin.math.sin(angle.toDouble()).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        drawPath(path, color = tint, style = Stroke(width = sw, join = StrokeJoin.Round))
        drawCircle(
            color = tint,
            radius = holeR,
            center = androidx.compose.ui.geometry.Offset(cx, cy),
            style = Stroke(width = sw),
        )
    }
}
