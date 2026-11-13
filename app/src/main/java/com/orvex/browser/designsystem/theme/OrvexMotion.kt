package com.orvex.browser.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/** Animasyon süreleri ve eğrileri. Kısa ve hafif tutulur. */
object OrvexMotion {
    const val DurationShort = 120
    const val DurationMedium = 220
    const val DurationLong = 360

    val Standard = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    fun <T> short(): TweenSpec<T> = tween(durationMillis = DurationShort, easing = Standard)

    fun <T> medium(): TweenSpec<T> = tween(durationMillis = DurationMedium, easing = Standard)

    fun <T> long(): TweenSpec<T> = tween(durationMillis = DurationLong, easing = Standard)

    fun <T> press(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
}
