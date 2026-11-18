package com.orvex.browser.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orvex.browser.browser.TabWebViewManager
import com.orvex.browser.browser.UrlUtils
import com.orvex.browser.browser.findComponentActivity
import com.orvex.browser.designsystem.adaptive.OrvexWindowSize
import com.orvex.browser.designsystem.adaptive.rememberOrvexWindowSize
import com.orvex.browser.designsystem.theme.OrvexTheme

private val MediumContentMaxWidth = 640.dp
private val ExpandedContentMaxWidth = 720.dp

@Composable
fun BrowserScreen(viewModel: BrowserViewModel = viewModel()) {
    val state = viewModel.uiState
    val focusManager = LocalFocusManager.current
    val activity = checkNotNull(LocalContext.current.findComponentActivity()) {
        "BrowserScreen bir ComponentActivity içinde çalışmalı"
    }
    val manager = remember(activity, viewModel) {
        TabWebViewManager(activity, viewModel, viewModel)
    }

    // Listeden kalkan sekmelerin canlı WebView'larını kapat.
    val tabIds = viewModel.tabs.map { it.id }
    LaunchedEffect(tabIds) { manager.retainOnly(tabIds.toSet()) }

    val activeTabId = viewModel.activeTabId
    val fallbackUrl = remember(activeTabId) { viewModel.activeTab.url }

    val maxContentWidth: Dp = when (rememberOrvexWindowSize()) {
        OrvexWindowSize.Compact -> Dp.Unspecified
        OrvexWindowSize.Medium -> MediumContentMaxWidth
        OrvexWindowSize.Expanded -> ExpandedContentMaxWidth
    }

    BackHandler(enabled = state.canGoBack) {
        manager.activeWebView()?.goBack()
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = OrvexTheme.colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            OrvexAddressBar(
                text = viewModel.addressInput,
                isSecure = state.isSecure,
                isLoading = state.isLoading,
                progress = state.progress,
                onTextChange = viewModel::onAddressChange,
                onFocusChange = viewModel::onEditingChanged,
                onGo = {
                    UrlUtils.resolve(viewModel.addressInput)?.let {
                        manager.activeWebView()?.loadUrl(it)
                    }
                    focusManager.clearFocus()
                },
                maxContentWidth = maxContentWidth
            )
        },
        bottomBar = {
            OrvexBottomBar(
                canGoBack = state.canGoBack,
                canGoForward = state.canGoForward,
                isLoading = state.isLoading,
                onBack = { manager.activeWebView()?.goBack() },
                onForward = { manager.activeWebView()?.goForward() },
                onReloadOrStop = {
                    val webView = manager.activeWebView()
                    if (state.isLoading) webView?.stopLoading() else webView?.reload()
                },
                onHome = { manager.activeWebView()?.loadUrl(UrlUtils.HOME_URL) },
                maxContentWidth = maxContentWidth
            )
        }
    ) { innerPadding ->
        OrvexWebView(
            manager = manager,
            tabId = activeTabId,
            fallbackUrl = fallbackUrl,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}
