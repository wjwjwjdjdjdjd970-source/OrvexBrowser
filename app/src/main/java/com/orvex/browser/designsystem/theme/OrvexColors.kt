package com.orvex.browser.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Orvex renk token'ları. Değerler yalnızca burada tanımlanır.
 */
@Immutable
data class OrvexColors(
    val background: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val outline: Color,
    val accent: Color,
    val accentContainer: Color,
    val onAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val success: Color,
    val danger: Color,
    /** Cam yüzeyin yarı saydam dolgusu */
    val glassFill: Color,
    /** Cam yüzeyin üst kenarındaki ince ışık çizgisi */
    val glassHighlight: Color
)

val OrvexDarkColors = OrvexColors(
    background = Color(0xFF05070D),
    surface = Color(0xFF0B1020),
    surfaceHigh = Color(0xFF121A30),
    outline = Color(0xFF1E2A4A),
    accent = Color(0xFF3D8BFF),
    accentContainer = Color(0xFF14305E),
    onAccent = Color(0xFF04101F),
    textPrimary = Color(0xFFE8EEFF),
    textSecondary = Color(0xFF8FA0C4),
    success = Color(0xFF3DDC97),
    danger = Color(0xFFFF5C6C),
    glassFill = Color(0xCC0B1020),
    glassHighlight = Color(0x26FFFFFF)
)

/** Material bileşenlerinin de Orvex paletini kullanması için eşleme. */
fun OrvexColors.toMaterialColorScheme(): ColorScheme = darkColorScheme(
    primary = accent,
    onPrimary = onAccent,
    primaryContainer = accentContainer,
    onPrimaryContainer = textPrimary,
    secondary = accent,
    onSecondary = onAccent,
    background = background,
    onBackground = textPrimary,
    surface = surface,
    onSurface = textPrimary,
    surfaceVariant = surfaceHigh,
    onSurfaceVariant = textSecondary,
    outline = outline,
    outlineVariant = outline,
    error = danger
)
