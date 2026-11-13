package com.orvex.browser.designsystem.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Uygulamadaki tüm ikonlar anlamsal isimlerle buradan alınır.
 * İleride özel ikonlar yalnızca bu dosyada değiştirilecek.
 */
object OrvexIcons {
    val Back: ImageVector get() = Icons.AutoMirrored.Filled.ArrowBack
    val Forward: ImageVector get() = Icons.AutoMirrored.Filled.ArrowForward
    val Reload: ImageVector get() = Icons.Filled.Refresh
    val Stop: ImageVector get() = Icons.Filled.Close
    val Home: ImageVector get() = Icons.Filled.Home
    val Secure: ImageVector get() = Icons.Filled.Lock
    val Insecure: ImageVector get() = Icons.Filled.Warning
}
