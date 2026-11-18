package com.orvex.browser.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.orvex.browser.browser.TabWebViewManager

/**
 * Aktif sekmenin WebView'ını gösteren kapsayıcı. WebView'ı bu bileşen oluşturmaz
 * ve yok etmez; sahibi [TabWebViewManager]'dır. Bileşen kalkarsa WebView yalnızca ayrılır.
 */
@Composable
fun OrvexWebView(
    manager: TabWebViewManager,
    tabId: String,
    fallbackUrl: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context -> FrameLayout(context) },
        update = { container ->
            val webView = manager.activate(tabId, fallbackUrl)
            if (webView != null && container.getChildAt(0) !== webView) {
                container.removeAllViews()
                (webView.parent as? ViewGroup)?.removeView(webView)
                container.addView(
                    webView,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            }
        },
        onRelease = { container -> container.removeAllViews() }
    )
}
