package com.ezanetta.bookifykmm.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezanetta.bookifykmm.domain.model.Genre
import com.ezanetta.bookifykmm.presentation.theme.DmSansFamily
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyColors

private val INDICATOR_HEIGHT = 36.dp
private val RAIL_SHAPE = RoundedCornerShape(999.dp)

@Composable
fun GenreSwitcher(
    selectedGenre: Genre,
    onGenreSelect: (Genre) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalBookifyColors.current
    val dmSans = DmSansFamily
    val density = LocalDensity.current
    val genres = Genre.entries

    data class TabBounds(val left: Dp, val width: Dp)

    val tabBounds = remember { Array(genres.size) { TabBounds(0.dp, 0.dp) } }
    var boundsReady by remember { mutableStateOf(false) }
    val selectedIndex = genres.indexOf(selectedGenre)

    val animSpec = tween<Dp>(durationMillis = 280, easing = FastOutSlowInEasing)
    val indicatorLeft = remember { Animatable(0.dp, Dp.VectorConverter) }
    val indicatorWidth = remember { Animatable(0.dp, Dp.VectorConverter) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(boundsReady, selectedIndex) {
        if (!boundsReady) return@LaunchedEffect
        val target = tabBounds.getOrNull(selectedIndex) ?: return@LaunchedEffect
        if (!initialized) {
            indicatorLeft.snapTo(target.left)
            indicatorWidth.snapTo(target.width)
            initialized = true
        } else {
            launch { indicatorLeft.animateTo(target.left, animSpec) }
            indicatorWidth.animateTo(target.width, animSpec)
        }
    }

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RAIL_SHAPE)
            .background(Color(0x0A000000), RAIL_SHAPE)
            .border(0.5.dp, Color(0x0D000000), RAIL_SHAPE)
            .padding(4.dp),
    ) {
        // Sliding indicator drawn behind labels
        if (initialized && indicatorWidth.value > 0.dp) {
            Box(
                modifier = Modifier
                    .offset(x = indicatorLeft.value)
                    .width(indicatorWidth.value)
                    .height(INDICATOR_HEIGHT)
                    .shadow(
                        elevation = 2.dp,
                        shape = RAIL_SHAPE,
                        ambientColor = colors.accent.copy(alpha = 0.2f),
                        spotColor = colors.accent.copy(alpha = 0.2f),
                    )
                    .background(colors.accent, RAIL_SHAPE),
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            genres.forEachIndexed { index, genre ->
                val active = genre == selectedGenre
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(INDICATOR_HEIGHT)
                        .onGloballyPositioned { coords ->
                            val leftDp = with(density) { coords.positionInParent().x.toDp() }
                            val widthDp = with(density) { coords.size.width.toDp() }
                            tabBounds[index] = TabBounds(leftDp, widthDp)
                            if (index == genres.size - 1) boundsReady = true
                        }
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { onGenreSelect(genre) },
                        ),
                ) {
                    Text(
                        text = genre.displayName,
                        color = if (active) colors.accentInk else colors.ink.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = dmSans,
                        letterSpacing = androidx.compose.ui.unit.TextUnit(0.05f, androidx.compose.ui.unit.TextUnitType.Sp),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
