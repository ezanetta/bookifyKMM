package com.ezanetta.bookifykmm.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun BookifyHeader(
    eyebrow: String = "YOUR SHELF",
    title: String = "Bookify",
    selectedTheme: AppTheme = AppTheme.SAGE,
    onSelectTheme: (AppTheme) -> Unit = {},
) {
    val colors = LocalBookifyColors.current
    val dmSans = DmSansFamily
    val newsreader = NewsreaderFamily
    var pickerOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
            ThemeButton(
                selectedTheme = selectedTheme,
                pickerOpen = pickerOpen,
                onClick = { pickerOpen = !pickerOpen },
            )
        }

        AnimatedVisibility(
            visible = pickerOpen,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            ThemePickerPanel(
                selectedTheme = selectedTheme,
                onSelect = { theme ->
                    onSelectTheme(theme)
                    pickerOpen = false
                },
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ThemeButton(
    selectedTheme: AppTheme,
    pickerOpen: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalBookifyColors.current
    val themeColors = selectedTheme.toColors()
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(themeColors.accent)
            .border(
                width = if (pickerOpen) 2.dp else 0.5.dp,
                color = if (pickerOpen) colors.ink.copy(alpha = 0.2f) else Color(0x1A000000),
                shape = CircleShape,
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
    )
}

@Composable
private fun ThemePickerPanel(
    selectedTheme: AppTheme,
    onSelect: (AppTheme) -> Unit,
) {
    val colors = LocalBookifyColors.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 12.dp)
            .background(colors.surface, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .border(0.5.dp, colors.ink.copy(alpha = 0.07f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        AppTheme.entries.forEach { theme ->
            ThemeSwatch(
                appTheme = theme,
                selected = theme == selectedTheme,
                onClick = { onSelect(theme) },
            )
        }
    }
}

@Composable
private fun ThemeSwatch(
    appTheme: AppTheme,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalBookifyColors.current
    val tc = appTheme.toColors()
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(2.dp, colors.ink.copy(alpha = 0.3f), CircleShape),
            )
        }
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(tc.accent),
        )
    }
}
