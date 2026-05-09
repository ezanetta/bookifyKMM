package com.ezanetta.bookifykmm.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezanetta.bookifykmm.domain.model.Book
import com.ezanetta.bookifykmm.presentation.components.BookCoverImage
import com.ezanetta.bookifykmm.presentation.components.Tag
import com.ezanetta.bookifykmm.presentation.theme.BookifyColors
import com.ezanetta.bookifykmm.presentation.theme.DmSansFamily
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyColors
import com.ezanetta.bookifykmm.presentation.theme.NewsreaderFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    book: Book,
    wishlisted: Boolean,
    onBack: () -> Unit,
    onToggleWishlist: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalBookifyColors.current
    val newsreader = NewsreaderFamily
    val dmSans = DmSansFamily

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .verticalScroll(rememberScrollState()),
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
                ChevronLeftIcon(tint = colors.ink)
                Text(
                    text = "Shelf",
                    color = colors.ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = dmSans,
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onToggleWishlist,
                    ),
            ) {
                WishlistBookmarkIcon(wishlisted = wishlisted, colors = colors)
            }
        }

        // Hero cover
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(210.dp)
                    .graphicsLayer {
                        rotationY = -3f
                        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
                    }
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(4.dp), ambientColor = Color(0x29000000), spotColor = Color(0x24000000))
                    .clip(RoundedCornerShape(4.dp)),
            ) {
                BookCoverImage(
                    coverUrl = book.coverUrl,
                    title = book.title,
                    modifier = Modifier
                        .width(210.dp)
                        .aspectRatio(1f / 1.5f),
                )
            }
        }

        // Title block
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = book.title,
                color = colors.ink,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                fontFamily = newsreader,
                letterSpacing = (-0.4).sp,
                lineHeight = (26 * 1.1).sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = book.author,
                color = colors.muted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = dmSans,
            )
        }

        // Tags
        Spacer(Modifier.height(18.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            book.subjects.forEach { subject ->
                Tag(
                    text = subject,
                    modifier = Modifier,
                )
            }
        }

        // Wishlist CTA
        Spacer(Modifier.height(32.dp))
        WishlistButton(
            wishlisted = wishlisted,
            onToggle = onToggleWishlist,
            colors = colors,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun WishlistButton(
    wishlisted: Boolean,
    onToggle: () -> Unit,
    colors: BookifyColors,
    modifier: Modifier = Modifier,
) {
    val dmSans = DmSansFamily
    val shape = RoundedCornerShape(14.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(52.dp)
            .then(
                if (wishlisted) {
                    Modifier
                        .background(Color.Transparent, shape)
                        .border(0.5.dp, Color(0x24000000), shape)
                } else {
                    Modifier
                        .shadow(elevation = 4.dp, shape = shape, ambientColor = colors.accent.copy(alpha = 0.2f), spotColor = colors.accent.copy(alpha = 0.2f))
                        .background(colors.accent, shape)
                }
            )
            .clip(shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onToggle,
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (wishlisted) {
                CheckIcon(tint = colors.ink)
                Text(
                    text = "In your wishlist",
                    color = colors.ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = dmSans,
                    letterSpacing = 0.2.sp,
                )
            } else {
                OutlinedBookmarkIcon(tint = colors.accentInk)
                Text(
                    text = "Add to wishlist",
                    color = colors.accentInk,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = dmSans,
                    letterSpacing = 0.2.sp,
                )
            }
        }
    }
}

@Composable
private fun ChevronLeftIcon(tint: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.625f, h * 0.1875f)
            lineTo(w * 0.3125f, h * 0.5f)
            lineTo(w * 0.625f, h * 0.8125f)
        }
        drawPath(path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun WishlistBookmarkIcon(wishlisted: Boolean, colors: BookifyColors) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.222f, h * 0.111f)
            lineTo(w * 0.778f, h * 0.111f)
            lineTo(w * 0.778f, h * 0.889f)
            lineTo(w * 0.5f, h * 0.722f)
            lineTo(w * 0.222f, h * 0.889f)
            close()
        }
        if (wishlisted) {
            drawPath(path, color = Color(colors.accent.red, colors.accent.green, colors.accent.blue, 1f))
        }
        drawPath(
            path,
            color = if (wishlisted) Color(colors.accent.red, colors.accent.green, colors.accent.blue, 1f) else colors.ink,
            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

@Composable
private fun OutlinedBookmarkIcon(tint: Color) {
    Canvas(modifier = Modifier.size(15.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.25f, h * 0.125f)
            lineTo(w * 0.75f, h * 0.125f)
            lineTo(w * 0.75f, h * 0.875f)
            lineTo(w * 0.5f, h * 0.71875f)
            lineTo(w * 0.25f, h * 0.875f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CheckIcon(tint: Color) {
    Canvas(modifier = Modifier.size(15.dp)) {
        val w = size.width; val h = size.height
        val path = Path().apply {
            moveTo(w * 0.1875f, h * 0.5f)
            lineTo(w * 0.4375f, h * 0.71875f)
            lineTo(w * 0.8125f, h * 0.28125f)
        }
        drawPath(path, color = tint, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
