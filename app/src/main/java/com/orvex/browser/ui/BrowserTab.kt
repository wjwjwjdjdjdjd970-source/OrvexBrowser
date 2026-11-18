package com.orvex.browser.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import com.orvex.browser.browser.UrlUtils
import java.util.UUID

/**
 * Bir sekmenin arayüz verisi. WebView nesnesi burada tutulmaz.
 */
@Immutable
data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val url: String = UrlUtils.HOME_URL,
    val title: String = "",
    val favicon: ImageBitmap? = null,
    val progress: Int = 0,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false
) {
    /** Mevcut adres çubuğu ve alt çubuk bileşenlerinin okuduğu görünüm. */
    fun toUiState() = BrowserUiState(
        url = url,
        title = title,
        progress = progress,
        isLoading = isLoading,
        canGoBack = canGoBack,
        canGoForward = canGoForward
    )
}
