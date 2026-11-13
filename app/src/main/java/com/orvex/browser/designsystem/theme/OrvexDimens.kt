package com.orvex.browser.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Boşluk ve boyut sabitleri. Composable'lara sayı gömmek yerine buradan okunur. */
@Immutable
data class OrvexDimens(
    val spaceXs: Dp = 4.dp,
    val spaceSm: Dp = 8.dp,
    val spaceMd: Dp = 12.dp,
    val spaceLg: Dp = 16.dp,
    val spaceXl: Dp = 24.dp,
    val barHeight: Dp = 56.dp,
    val iconSize: Dp = 24.dp,
    val touchTarget: Dp = 48.dp,
    val progressHeight: Dp = 3.dp,
    val hairline: Dp = 1.dp
)
