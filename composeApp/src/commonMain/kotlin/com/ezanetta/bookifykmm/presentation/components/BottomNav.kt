package com.ezanetta.bookifykmm.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezanetta.bookifykmm.domain.model.AppTab
import com.ezanetta.bookifykmm.presentation.theme.DmSansFamily
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyColors

@Composable
fun BookifyBottomNav(
    selectedTab: AppTab,
    wishlistCount: Int,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalBookifyColors.current
    val dmSans = DmSansFamily
    val bgColor = if (colors.dark) Color(0xD91C1916) else Color(0xB3FFFFFF)
    val borderColor = if (colors.dark) Color(0x14FFFFFF) else Color(0x0F000000)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 0.5.dp.toPx(),
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            NavTab(
                label = "Home",
                active = selectedTab == AppTab.HOME,
                badge = 0,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(AppTab.HOME) },
                icon = { active -> HomeIcon(active = active, tint = if (active) colors.accent else colors.muted) },
            )
            NavTab(
                label = "Wishlist",
                active = selectedTab == AppTab.WISHLIST,
                badge = wishlistCount,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(AppTab.WISHLIST) },
                icon = { active -> BookmarkIcon(active = active, tint = if (active) colors.accent else colors.muted) },
            )
        }
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
private fun NavTab(
    label: String,
    active: Boolean,
    badge: Int,
    onClick: () -> Unit,
    icon: @Composable (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalBookifyColors.current
    val dmSans = DmSansFamily

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = 4.dp, vertical = 6.dp),
    ) {
        Box {
            icon(active)
            if (badge > 0) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset(x = 9.dp, y = (-3).dp)
                        .size(width = if (badge > 9) 20.dp else 16.dp, height = 16.dp)
                        .background(colors.accent, RoundedCornerShape(8.dp))
                        .align(Alignment.TopEnd),
                ) {
                    Text(
                        text = badge.toString(),
                        color = colors.accentInk,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = dmSans,
                    )
                }
            }
        }
        Text(
            text = label,
            color = if (active) colors.accent else colors.muted,
            fontSize = 10.5.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
            fontFamily = dmSans,
            letterSpacing = androidx.compose.ui.unit.TextUnit(0.2f, androidx.compose.ui.unit.TextUnitType.Sp),
        )
    }
}

@Composable
private fun HomeIcon(active: Boolean, tint: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = if (active) 1.8.dp.toPx() else 1.5.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.136f, h * 0.432f)
            lineTo(w * 0.5f, h * 0.136f)
            lineTo(w * 0.864f, h * 0.432f)
            lineTo(w * 0.864f, h * 0.864f)
            cubicTo(w * 0.864f, h * 0.864f, w * 0.864f, h * 0.909f, w * 0.818f, h * 0.909f)
            lineTo(w * 0.636f, h * 0.909f)
            lineTo(w * 0.636f, h * 0.636f)
            lineTo(w * 0.364f, h * 0.636f)
            lineTo(w * 0.364f, h * 0.909f)
            lineTo(w * 0.182f, h * 0.909f)
            cubicTo(w * 0.136f, h * 0.909f, w * 0.136f, h * 0.864f, w * 0.136f, h * 0.864f)
            close()
        }
        if (active) {
            drawPath(path, color = tint.copy(alpha = 0.12f))
        }
        drawPath(path, color = tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun BookmarkIcon(active: Boolean, tint: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val w = size.width
        val h = size.height
        val strokeWidth = if (active) 1.8.dp.toPx() else 1.5.dp.toPx()
        val path = Path().apply {
            moveTo(w * 0.227f, h * 0.136f)
            lineTo(w * 0.773f, h * 0.136f)
            lineTo(w * 0.773f, h * 0.909f)
            lineTo(w * 0.5f, h * 0.75f)
            lineTo(w * 0.227f, h * 0.909f)
            close()
        }
        if (active) {
            drawPath(path, color = tint.copy(alpha = 0.12f))
        }
        drawPath(path, color = tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
