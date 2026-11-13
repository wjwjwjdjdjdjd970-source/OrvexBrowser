package com.orvex.browser.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Adres çubuğuna yazılan metni yüklenebilir bir https adresine
 * ya da arama sorgusuna çevirir.
 */
object UrlUtils {
    const val HOME_URL = "https://duckduckgo.com/"
    private const val SEARCH_URL = "https://duckduckgo.com/?q="

    private val HOST_PATTERN = Regex(
        "^(localhost|(?:[A-Za-z0-9-]+\\.)+[A-Za-z0-9-]{2,}|\\d{1,3}(?:\\.\\d{1,3}){3})" +
            "(?::\\d{1,5})?(?:[/?#].*)?$"
    )

    /** Boş girdi için null döner. */
    fun resolve(input: String): String? {
        val text = input.trim()
        if (text.isEmpty()) return null

        val hasWhitespace = text.any { it.isWhitespace() }
        val lower = text.lowercase()

        if (!hasWhitespace && (lower.startsWith("https://") || lower.startsWith("http://"))) {
            return text
        }
        if (!hasWhitespace && HOST_PATTERN.matches(text)) {
            return "https://$text"
        }
        // javascript:, file:, data: gibi şemalar ve diğer her şey arama olarak ele alınır.
        return SEARCH_URL + Uri.encode(text)
    }
}

/** WebView olaylarını arayüz katmanına (ViewModel) taşır. */
interface BrowserEventListener {
    fun onPageStarted(url: String)
    fun onPageFinished(url: String)
    fun onNavigationChanged(url: String, canGoBack: Boolean, canGoForward: Boolean)
    fun onProgressChanged(progress: Int)
    fun onTitleChanged(title: String)
}

class OrvexWebViewClient(
    private val listener: BrowserEventListener
) : WebViewClient() {

    /** Yalnızca http/https yüklenir; intent:, file:, javascript: vb. engellenir. */
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val scheme = request?.url?.scheme?.lowercase()
        return scheme != "http" && scheme != "https"
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        if (url != null) listener.onPageStarted(url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (url != null) listener.onPageFinished(url)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        if (view != null && url != null) {
            listener.onNavigationChanged(url, view.canGoBack(), view.canGoForward())
        }
    }

    /** Geçersiz SSL sertifikalarında bağlantı her zaman iptal edilir. */
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.cancel()
    }
}

class OrvexChromeClient(
    private val listener: BrowserEventListener
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        listener.onProgressChanged(newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        if (title != null) listener.onTitleChanged(title)
    }

    // Bu aşamada konum, kamera ve mikrofon izinleri tamamen reddedilir.
    override fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        callback?.invoke(origin, false, false)
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        request?.deny()
    }
}

/** WebView için temel güvenlik ayarlarını uygular. */
@SuppressLint("SetJavaScriptEnabled")
@Suppress("DEPRECATION")
fun WebView.applySecureDefaults() {
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true

        // Yerel dosya ve içerik erişimi kapalı
        allowFileAccess = false
        allowContentAccess = false
        allowFileAccessFromFileURLs = false
        allowUniversalAccessFromFileURLs = false

        // https sayfada http içerik yüklenmez
        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

        setGeolocationEnabled(false)
        setSupportMultipleWindows(false)
        javaScriptCanOpenWindowsAutomatically = false
        setSafeBrowsingEnabled(true)

        setSupportZoom(true)
        builtInZoomControls = true
        setDisplayZoomControls(false)
        useWideViewPort = true
        loadWithOverviewMode = true
    }

    CookieManager.getInstance().apply {
        setAcceptCookie(true)
        setAcceptThirdPartyCookies(this@applySecureDefaults, false)
    }
}
