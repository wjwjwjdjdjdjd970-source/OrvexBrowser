package com.orvex.browser.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.orvex.browser.browser.BrowserEventListener
import com.orvex.browser.browser.UrlUtils

data class BrowserUiState(
    val url: String = UrlUtils.HOME_URL,
    val title: String = "",
    val progress: Int = 0,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false
) {
    val isSecure: Boolean get() = url.startsWith("https://")
}

class BrowserViewModel : ViewModel(), BrowserEventListener {

    var uiState by mutableStateOf(BrowserUiState())
        private set

    var addressInput by mutableStateOf(UrlUtils.HOME_URL)
        private set

    private var isEditing = false

    fun onAddressChange(value: String) {
        addressInput = value
    }

    fun onEditingChanged(editing: Boolean) {
        isEditing = editing
        if (!editing) addressInput = uiState.url
    }

    override fun onPageStarted(url: String) {
        uiState = uiState.copy(url = url, isLoading = true, progress = 0)
        if (!isEditing) addressInput = url
    }

    override fun onPageFinished(url: String) {
        uiState = uiState.copy(url = url, isLoading = false, progress = 100)
        if (!isEditing) addressInput = url
    }

    override fun onNavigationChanged(url: String, canGoBack: Boolean, canGoForward: Boolean) {
        uiState = uiState.copy(url = url, canGoBack = canGoBack, canGoForward = canGoForward)
        if (!isEditing) addressInput = url
    }

    override fun onProgressChanged(progress: Int) {
        uiState = uiState.copy(progress = progress, isLoading = progress < 100)
    }

    override fun onTitleChanged(title: String) {
        uiState = uiState.copy(title = title)
    }
}
