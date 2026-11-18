package com.orvex.browser.browser

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/** Sekme kimliğiyle gelen WebView olaylarını alan taraf (ViewModel). */
interface TabEventSink {
    fun onPageStarted(tabId: String, url: String)
    fun onPageFinished(tabId: String, url: String)
    fun onNavigationChanged(tabId: String, url: String, canGoBack: Boolean, canGoForward: Boolean)
    fun onProgressChanged(tabId: String, progress: Int)
    fun onTitleChanged(tabId: String, title: String)

    /** Sekmenin WebView'ı uyutuldu ya da yok edildi; yükleme durumu sıfırlanmalı. */
    fun onTabSuspended(tabId: String)
}

/** Uyutulan sekmelerin WebView geçmiş kayıtlarının tutulduğu yer. */
interface TabStateStore {
    fun saveWebState(tabId: String, state: Bundle)

    /** Kaydı döndürür ve siler. */
    fun takeWebState(tabId: String): Bundle?

    fun discardWebState(tabId: String)
}

/** Context zincirinden ComponentActivity'yi bulur. */
fun Context.findComponentActivity(): ComponentActivity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is ComponentActivity) return current
        current = current.baseContext
    }
    return current as? ComponentActivity
}

/**
 * Tek bir WebView örneğinin olaylarını sekme kimliğiyle ViewModel'e iletir.
 * Örnek kapatılınca ya da uyutulunca geçersiz yapılır; sonradan gelen olaylar düşer.
 */
internal class TabEventRelay(
    private val tabId: String,
    private val sink: TabEventSink
) : BrowserEventListener {

    @Volatile
    private var valid = true

    fun invalidate() {
        valid = false
    }

    override fun onPageStarted(url: String) {
        if (valid) sink.onPageStarted(tabId, url)
    }

    override fun onPageFinished(url: String) {
        if (valid) sink.onPageFinished(tabId, url)
    }

    override fun onNavigationChanged(url: String, canGoBack: Boolean, canGoForward: Boolean) {
        if (valid) sink.onNavigationChanged(tabId, url, canGoBack, canGoForward)
    }

    override fun onProgressChanged(progress: Int) {
        if (valid) sink.onProgressChanged(tabId, progress)
    }

    override fun onTitleChanged(title: String) {
        if (valid) sink.onTitleChanged(tabId, title)
    }
}

/**
 * Sekmelerin WebView'larını oluşturur, saklar, uyutur ve yok eder.
 *
 * - WebView'lar yalnızca burada oluşturulur; güvenlik ayarları her seferinde uygulanır.
 * - Canlı WebView sayısı [maxLiveWebViews] ile sınırlıdır; en az kullanılan uyutulur.
 * - Temizlik Activity gerçekten yok olurken yapılır, Compose yeniden çizimlerinde değil.
 */
class TabWebViewManager(
    private val activity: ComponentActivity,
    private val sink: TabEventSink,
    private val store: TabStateStore,
    private val maxLiveWebViews: Int = DEFAULT_MAX_LIVE_WEBVIEWS
) {

    private class LiveTab(val webView: WebView, val relay: TabEventRelay)

    private val lifecycle = activity.lifecycle

    /** Sondaki en son kullanılan sekmedir. */
    private val live = LinkedHashMap<String, LiveTab>()

    private var activeId: String? = null
    private var destroyed = false
    private var isResumed = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    private val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                isResumed = true
                activeWebView()?.onResume()
            }
            Lifecycle.Event.ON_PAUSE -> {
                isResumed = false
                live.values.forEach { it.webView.onPause() }
            }
            Lifecycle.Event.ON_DESTROY -> destroyAll(saveStates = !activity.isFinishing)
            else -> Unit
        }
    }

    init {
        lifecycle.addObserver(observer)
    }

    /**
     * Sekmeyi aktif yapar. WebView yoksa oluşturur: önce kayıtlı geçmişi geri yüklemeyi,
     * olmazsa [fallbackUrl]'yi yüklemeyi dener. Canlı sınırı aşılırsa en eski sekmeyi uyutur.
     */
    fun activate(tabId: String, fallbackUrl: String): WebView? {
        if (destroyed) return null

        val previousId = activeId
        if (previousId != null && previousId != tabId) {
            live[previousId]?.webView?.onPause()
        }

        val entry = live.remove(tabId) ?: createLiveTab(tabId, fallbackUrl)
        live[tabId] = entry
        activeId = tabId

        if (isResumed) entry.webView.onResume() else entry.webView.onPause()

        hibernateExtraTabs()
        return entry.webView
    }

    fun activeWebView(): WebView? = activeId?.let { live[it]?.webView }

    /** Sekmeyi tamamen kapatır: WebView yok edilir, kayıtlı durum silinir. */
    fun close(tabId: String) {
        store.discardWebState(tabId)
        val entry = live.remove(tabId) ?: return
        entry.relay.invalidate()
        disposeWebView(entry.webView)
        if (activeId == tabId) activeId = null
    }

    /** Listede olmayan sekmelerin canlı WebView'larını kapatır. */
    fun retainOnly(ids: Set<String>) {
        if (destroyed) return
        live.keys.filter { it !in ids }.forEach { close(it) }
    }

    /** Tüm WebView'ları yok eder. Activity yok olurken bir kez çağrılır. */
    fun destroyAll(saveStates: Boolean) {
        if (destroyed) return
        destroyed = true
        lifecycle.removeObserver(observer)

        live.entries.toList().forEach { (id, entry) ->
            entry.relay.invalidate()
            if (saveStates) saveState(id, entry.webView)
            disposeWebView(entry.webView)
            sink.onTabSuspended(id)
        }
        live.clear()
        activeId = null
    }

    private fun createLiveTab(tabId: String, fallbackUrl: String): LiveTab {
        val relay = TabEventRelay(tabId, sink)
        val webView = WebView(activity).apply {
            applySecureDefaults()
            webViewClient = OrvexWebViewClient(relay)
            webChromeClient = OrvexChromeClient(relay)
        }
        val saved = store.takeWebState(tabId)
        val restored = saved != null && webView.restoreState(saved) != null
        if (!restored) webView.loadUrl(fallbackUrl)
        return LiveTab(webView, relay)
    }

    private fun hibernateExtraTabs() {
        while (live.size > maxLiveWebViews) {
            val victimId = live.keys.firstOrNull { it != activeId } ?: return
            hibernate(victimId)
        }
    }

    private fun hibernate(tabId: String) {
        val entry = live.remove(tabId) ?: return
        entry.relay.invalidate()
        saveState(tabId, entry.webView)
        disposeWebView(entry.webView)
        sink.onTabSuspended(tabId)
    }

    private fun saveState(tabId: String, webView: WebView) {
        val state = Bundle()
        if (webView.saveState(state) != null) {
            store.saveWebState(tabId, state)
        } else {
            store.discardWebState(tabId)
        }
    }

    private fun disposeWebView(webView: WebView) {
        webView.stopLoading()
        (webView.parent as? ViewGroup)?.removeView(webView)
        webView.destroy()
    }

    companion object {
        const val DEFAULT_MAX_LIVE_WEBVIEWS = 2
    }
}
