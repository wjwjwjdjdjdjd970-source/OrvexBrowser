package com.orvex.browser.designsystem.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/** Ek bağımlılık gerektirmeyen basit ekran genişliği sınıfı. */
enum class OrvexWindowSize { Compact, Medium, Expanded }

@Composable
fun rememberOrvexWindowSize(): OrvexWindowSize {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
        widthDp < 600 -> OrvexWindowSize.Compact
        widthDp < 840 -> OrvexWindowSize.Medium
        else -> OrvexWindowSize.Expanded
    }
}
