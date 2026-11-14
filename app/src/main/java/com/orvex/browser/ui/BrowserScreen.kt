package com.orvex.browser.ui

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orvex.browser.browser.UrlUtils
import com.orvex.browser.designsystem.adaptive.OrvexWindowSize
import com.orvex.browser.designsystem.adaptive.rememberOrvexWindowSize
import com.orvex.browser.designsystem.theme.OrvexTheme

private val MediumContentMaxWidth = 640.dp
private val ExpandedContentMaxWidth = 720.dp

@Composable
fun BrowserScreen(viewModel: BrowserViewModel = viewModel()) {
    val state = viewModel.uiState
    val focusManager = LocalFocusManager.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    val initialUrl = remember { viewModel.uiState.url }

    val maxContentWidth: Dp = when (rememberOrvexWindowSize()) {
        OrvexWindowSize.Compact -> Dp.Unspecified
        OrvexWindowSize.Medium -> MediumContentMaxWidth
        OrvexWindowSize.Expanded -> ExpandedContentMaxWidth
    }

    BackHandler(enabled = state.canGoBack) {
        webView?.goBack()
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
                    UrlUtils.resolve(viewModel.addressInput)?.let { webView?.loadUrl(it) }
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
                onBack = { webView?.goBack() },
                onForward = { webView?.goForward() },
                onReloadOrStop = {
                    if (state.isLoading) webView?.stopLoading() else webView?.reload()
                },
                onHome = { webView?.loadUrl(UrlUtils.HOME_URL) },
                maxContentWidth = maxContentWidth
            )
        }
    ) { innerPadding ->
        OrvexWebView(
            initialUrl = initialUrl,
            listener = viewModel,
            onWebViewCreated = { webView = it },
            onWebViewReleased = { webView = null },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}
