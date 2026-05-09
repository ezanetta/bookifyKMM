package com.ezanetta.bookifykmm.presentation.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bookifykmm.composeapp.generated.resources.Res
import bookifykmm.composeapp.generated.resources.cover_placeholder
import coil3.compose.AsyncImage
import com.ezanetta.bookifykmm.domain.model.Book
import org.jetbrains.compose.resources.painterResource
import com.ezanetta.bookifykmm.presentation.theme.DmSansFamily
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyColors
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyDensity
import com.ezanetta.bookifykmm.presentation.theme.NewsreaderFamily

@Composable
fun GridCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalBookifyColors.current
    val d = LocalBookifyDensity.current
    val newsreader = NewsreaderFamily
    val dmSans = DmSansFamily

    Column(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick,
        ),
    ) {
        BookCoverImage(
            coverUrl = book.coverUrl,
            title = book.title,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1.5f)
                .clip(RoundedCornerShape(3.dp)),
        )
        Spacer(Modifier.height(10.dp))
        Column(modifier = Modifier.padding(horizontal = 2.dp)) {
            Text(
                text = book.title,
                color = colors.ink,
                fontSize = d.titleSize,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Normal,
                fontFamily = newsreader,
                lineHeight = d.titleSize * d.titleLineHeight,
                letterSpacing = (-0.1).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = book.author,
                color = colors.muted,
                fontSize = d.authorSize,
                fontFamily = dmSans,
            )
            Spacer(Modifier.height(8.dp))
            TagsRow(subjects = book.subjects, maxTags = 2)
        }
    }
}

@Composable
fun ListCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalBookifyColors.current
    val d = LocalBookifyDensity.current
    val newsreader = NewsreaderFamily
    val dmSans = DmSansFamily
    val shape = RoundedCornerShape(18.dp)

    Row(
        horizontalArrangement = Arrangement.spacedBy(d.listGap),
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = shape, ambientColor = Color(0x08000000), spotColor = Color(0x0A000000))
            .background(colors.surface, shape)
            .border(0.5.dp, Color(0x0A000000), shape)
            .clip(shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(d.pad),
    ) {
        BookCoverImage(
            coverUrl = book.coverUrl,
            title = book.title,
            modifier = Modifier
                .width(d.listCoverW)
                .aspectRatio(1f / 1.5f)
                .clip(RoundedCornerShape(2.5.dp)),
        )
        Column(modifier = Modifier.weight(1f).padding(top = 2.dp)) {
            Text(
                text = book.title,
                color = colors.ink,
                fontSize = (d.titleSize.value + 1f).sp,
                fontWeight = FontWeight.Medium,
                fontFamily = newsreader,
                lineHeight = d.titleSize * d.titleLineHeight,
                letterSpacing = (-0.2).sp,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = book.author,
                color = colors.muted,
                fontSize = d.authorSize,
                fontFamily = dmSans,
            )
            Spacer(Modifier.height(10.dp))
            TagsRow(subjects = book.subjects, maxTags = book.subjects.size)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsRow(subjects: List<String>, maxTags: Int) {
    val d = LocalBookifyDensity.current
    if (subjects.isEmpty()) return
    FlowRow(horizontalArrangement = Arrangement.spacedBy(d.tagGap), verticalArrangement = Arrangement.spacedBy(d.tagGap)) {
        subjects.take(maxTags).forEach { subject ->
            Tag(text = subject, maxChars = 16)
        }
        if (subjects.size > maxTags) {
            val colors = LocalBookifyColors.current
            val dmSans = DmSansFamily
            Text(
                text = "+${subjects.size - maxTags}",
                color = colors.muted.copy(alpha = 0.7f),
                fontSize = d.tagSize,
                fontWeight = FontWeight.Medium,
                fontFamily = dmSans,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }
}

@Composable
fun BookCoverImage(
    coverUrl: String,
    title: String,
    modifier: Modifier = Modifier,
) {
    val placeholder = painterResource(Res.drawable.cover_placeholder)
    AsyncImage(
        model = coverUrl.ifEmpty { null },
        contentDescription = title,
        contentScale = ContentScale.Crop,
        placeholder = placeholder,
        error = placeholder,
        fallback = placeholder,
        modifier = modifier,
    )
}
