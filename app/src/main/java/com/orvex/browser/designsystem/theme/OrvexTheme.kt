package com.orvex.browser.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalOrvexColors = staticCompositionLocalOf { OrvexDarkColors }
val LocalOrvexDimens = staticCompositionLocalOf { OrvexDimens() }

/** Bileşenlerde `OrvexTheme.colors` ve `OrvexTheme.dimens` olarak okunur. */
object OrvexTheme {
    val colors: OrvexColors
        @Composable
        @ReadOnlyComposable
        get() = LocalOrvexColors.current

    val dimens: OrvexDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalOrvexDimens.current
}

/**
 * Orvex teması. Yalnızca koyu tema vardır ve sistemin dinamik rengi kullanılmaz.
 */
@Composable
fun OrvexTheme(content: @Composable () -> Unit) {
    val colors = OrvexDarkColors
    CompositionLocalProvider(
        LocalOrvexColors provides colors,
        LocalOrvexDimens provides OrvexDimens()
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            typography = OrvexTypography,
            shapes = OrvexShapes,
            content = content
        )
    }
}
