package com.orvex.browser.ui

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.orvex.browser.browser.BrowserEventListener
import com.orvex.browser.browser.OrvexChromeClient
import com.orvex.browser.browser.OrvexWebViewClient
import com.orvex.browser.browser.applySecureDefaults

@Composable
fun OrvexWebView(
    initialUrl: String,
    listener: BrowserEventListener,
    onWebViewCreated: (WebView) -> Unit,
    onWebViewReleased: () -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                applySecureDefaults()
                webViewClient = OrvexWebViewClient(listener)
                webChromeClient = OrvexChromeClient(listener)
                onWebViewCreated(this)
                loadUrl(initialUrl)
            }
        },
        onRelease = { webView ->
            onWebViewReleased()
            webView.stopLoading()
            webView.destroy()
        }
    )
}
