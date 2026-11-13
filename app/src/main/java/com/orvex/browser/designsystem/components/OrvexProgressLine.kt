package com.orvex.browser.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.orvex.browser.designsystem.theme.OrvexMotion
import com.orvex.browser.designsystem.theme.OrvexTheme

/**
 * İnce yükleme çizgisi. Yükleme yokken yer kaplar ama görünmez,
 * böylece düzen zıplamaz.
 */
@Composable
fun OrvexProgressLine(
    visible: Boolean,
    progress: Int,
    modifier: Modifier = Modifier
) {
    val colors = OrvexTheme.colors
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0, 100) / 100f,
        animationSpec = OrvexMotion.medium<Float>(),
        label = "progressLine"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(OrvexTheme.dimens.progressHeight)
    ) {
        if (visible) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animated)
                    .background(colors.accent)
            )
        }
    }
}
