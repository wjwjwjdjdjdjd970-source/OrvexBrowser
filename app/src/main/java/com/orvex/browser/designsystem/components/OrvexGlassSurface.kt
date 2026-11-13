package com.orvex.browser.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.orvex.browser.designsystem.theme.OrvexTheme

/**
 * Kontrollü cam yüzey: yarı saydam lacivert dolgu ve üstten aşağı
 * sönen ince bir ışık kenarlığı. Gerçek bulanıklık kullanmaz.
 */
@Composable
fun OrvexGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = OrvexTheme.colors
    val borderBrush = Brush.verticalGradient(
        listOf(colors.glassHighlight, colors.outline)
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(colors.glassFill)
            .border(OrvexTheme.dimens.hairline, borderBrush, shape),
        content = content
    )
}
