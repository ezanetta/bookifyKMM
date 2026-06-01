package com.ezanetta.bookifykmm.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezanetta.bookifykmm.domain.model.AppTheme
import com.ezanetta.bookifykmm.presentation.theme.DmSansFamily
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyColors
import com.ezanetta.bookifykmm.presentation.theme.NewsreaderFamily
import com.ezanetta.bookifykmm.presentation.theme.toColors

@Composable
fun SettingsScreen(
    selectedTheme: AppTheme,
    onSelectTheme: (AppTheme) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalBookifyColors.current
    val dmSans = DmSansFamily
    val newsreader = NewsreaderFamily

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onBack,
                    )
                    .padding(horizontal = 4.dp, vertical = 8.dp),
            ) {
                SettingsChevronIcon(tint = colors.ink)
                Text(
                    text = "Back",
                    color = colors.ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = dmSans,
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "PREFERENCES",
                color = colors.muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = dmSans,
                letterSpacing = 1.6.sp,
            )
            Text(
                text = "Settings",
                color = colors.ink,
                fontSize = 34.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                fontFamily = newsreader,
                letterSpacing = (-0.8).sp,
                lineHeight = 34.sp,
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = "APPEARANCE",
                color = colors.muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = dmSans,
                letterSpacing = 1.6.sp,
            )

            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(14.dp))
                    .border(
                        0.5.dp,
                        if (colors.dark) Color(0x14FFFFFF) else Color(0x0A000000),
                        RoundedCornerShape(14.dp),
                    ),
            ) {
                AppTheme.entries.forEachIndexed { index, theme ->
                    ThemeOptionRow(
                        theme = theme,
                        selected = theme == selectedTheme,
                        onClick = { onSelectTheme(theme) },
                    )
                    if (index < AppTheme.entries.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 58.dp)
                                .height(0.5.dp)
                                .background(
                                    if (colors.dark) Color(0x0FFFFFFF) else Color(0x0F000000)
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    theme: AppTheme,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalBookifyColors.current
    val tc = theme.toColors()
    val dmSans = DmSansFamily

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        // Color preview: bg ring + accent fill
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(tc.bg)
                .border(0.5.dp, if (colors.dark) Color(0x1FFFFFFF) else Color(0x1A000000), CircleShape),
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(tc.accent),
            )
        }

        Text(
            text = theme.displayName,
            color = colors.ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = dmSans,
            modifier = Modifier.weight(1f),
        )

        if (selected) {
            CheckmarkIcon(tint = colors.accent)
        }
    }
}

@Composable
private fun SettingsChevronIcon(tint: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val sw = w * 0.1f
        val path = Path().apply {
            moveTo(w * 0.625f, h * 0.2f)
            lineTo(w * 0.25f, h * 0.5f)
            lineTo(w * 0.625f, h * 0.8f)
        }
        drawPath(path, color = tint, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CheckmarkIcon(tint: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        val sw = w * 0.1f
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.5f)
            lineTo(w * 0.42f, h * 0.76f)
            lineTo(w * 0.85f, h * 0.26f)
        }
        drawPath(path, color = tint, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
