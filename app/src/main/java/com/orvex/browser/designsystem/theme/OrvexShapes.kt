package com.orvex.browser.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Köşe yarıçapı değerleri. */
object OrvexRadius {
    val small = 8.dp
    val medium = 14.dp
    val large = 20.dp
    val pill = 28.dp
}

val OrvexShapes = Shapes(
    small = RoundedCornerShape(OrvexRadius.small),
    medium = RoundedCornerShape(OrvexRadius.medium),
    large = RoundedCornerShape(OrvexRadius.large),
    extraLarge = RoundedCornerShape(OrvexRadius.pill)
)
