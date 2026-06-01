package com.ezanetta.bookifykmm.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ezanetta.bookifykmm.presentation.theme.DmSansFamily
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyColors
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyDensity

@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    maxChars: Int = Int.MAX_VALUE,
) {
    val colors = LocalBookifyColors.current
    val d = LocalBookifyDensity.current
    val dmSans = DmSansFamily

    val display = if (text.length > maxChars) text.take(maxChars - 1).trimEnd() + "…" else text
    val shape = RoundedCornerShape(999.dp)

    Surface(
        shape = shape,
        color = Color(0x0B000000),
        modifier = modifier.border(0.5.dp, Color(0x0A000000), shape),
    ) {
        Text(
            text = display,
            color = colors.muted,
            fontSize = d.tagSize,
            fontWeight = FontWeight.Medium,
            fontFamily = dmSans,
            letterSpacing = androidx.compose.ui.unit.TextUnit(0.1f, androidx.compose.ui.unit.TextUnitType.Sp),
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.padding(horizontal = d.tagPadX, vertical = d.tagPadY),
        )
    }
}
