package com.orvex.browser.ui

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.orvex.browser.browser.TabEventSink
import com.orvex.browser.browser.TabStateStore

data class BrowserUiState(
    val url: String = "",
    val title: String = "",
    val progress: Int = 0,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false
) {
    val isSecure: Boolean get() = url.startsWith("https://")
}

class BrowserViewModel : ViewModel(), TabEventSink, TabStateStore {

    private val firstTab = BrowserTab()

    var tabs by mutableStateOf(listOf(firstTab))
        private set

    var activeTabId by mutableStateOf(firstTab.id)
        private set

    var addressInput by mutableStateOf(firstTab.url)
        private set

    private var isEditing = false

    /** Uyutulan sekmelerin WebView geçmiş kayıtları. WebView nesnesi tutulmaz. */
    private val webStates = HashMap<String, Bundle>()

    val activeTab: BrowserTab
        get() = tabs.firstOrNull { it.id == activeTabId } ?: tabs.first()

    /** Aktif sekmeden türetilen görünüm; çubuk bileşenleri bunu okur. */
    val uiState: BrowserUiState
        get() = activeTab.toUiState()

    // --- Adres çubuğu ---

    fun onAddressChange(value: String) {
        addressInput = value
    }

    fun onEditingChanged(editing: Boolean) {
        isEditing = editing
        if (!editing) addressInput = activeTab.url
    }

    // --- Sekme işlemleri ---

    fun newTab() {
        if (tabs.size >= MAX_TABS) return
        val tab = BrowserTab()
        tabs = tabs + tab
        selectTab(tab.id)
    }

    fun selectTab(tabId: String) {
        if (tabs.none { it.id == tabId }) return
        activeTabId = tabId
        isEditing = false
        addressInput = activeTab.url
    }

    fun closeTab(tabId: String) {
        val index = tabs.indexOfFirst { it.id == tabId }
        if (index < 0) return
        discardWebState(tabId)

        val remaining = tabs.filterNot { it.id == tabId }
        if (remaining.isEmpty()) {
            val fresh = BrowserTab()
            tabs = listOf(fresh)
            activeTabId = fresh.id
        } else {
            tabs = remaining
            if (activeTabId == tabId) {
                activeTabId = remaining[index.coerceAtMost(remaining.lastIndex)].id
            }
        }
        isEditing = false
        addressInput = activeTab.url
    }

    // --- WebView olayları (sekme kimliğiyle gelir) ---

    override fun onPageStarted(tabId: String, url: String) {
        updateTab(tabId) { it.copy(url = url, isLoading = true, progress = 0) }
    }

    override fun onPageFinished(tabId: String, url: String) {
        updateTab(tabId) { it.copy(url = url, isLoading = false, progress = 100) }
    }

    override fun onNavigationChanged(
        tabId: String,
        url: String,
        canGoBack: Boolean,
        canGoForward: Boolean
    ) {
        updateTab(tabId) { it.copy(url = url, canGoBack = canGoBack, canGoForward = canGoForward) }
    }

    override fun onProgressChanged(tabId: String, progress: Int) {
        updateTab(tabId) { it.copy(progress = progress, isLoading = progress < 100) }
    }

    override fun onTitleChanged(tabId: String, title: String) {
        updateTab(tabId) { it.copy(title = title) }
    }

    override fun onTabSuspended(tabId: String) {
        updateTab(tabId) { it.copy(isLoading = false, progress = 0) }
    }

    private fun updateTab(tabId: String, transform: (BrowserTab) -> BrowserTab) {
        val index = tabs.indexOfFirst { it.id == tabId }
        if (index < 0) return // kapatılmış sekmeden gelen geç olay
        val old = tabs[index]
        val updated = transform(old)
        if (updated == old) return
        tabs = tabs.toMutableList().also { it[index] = updated }
        if (tabId == activeTabId && !isEditing) addressInput = updated.url
    }

    // --- TabStateStore ---

    override fun saveWebState(tabId: String, state: Bundle) {
        webStates[tabId] = state
    }

    override fun takeWebState(tabId: String): Bundle? = webStates.remove(tabId)

    override fun discardWebState(tabId: String) {
        webStates.remove(tabId)
    }

    override fun onCleared() {
        webStates.clear()
    }

    companion object {
        const val MAX_TABS = 20
    }
}
