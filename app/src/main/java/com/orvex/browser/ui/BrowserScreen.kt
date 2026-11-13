package com.orvex.browser.ui

import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orvex.browser.R
import com.orvex.browser.browser.BrowserEventListener
import com.orvex.browser.browser.OrvexChromeClient
import com.orvex.browser.browser.OrvexWebViewClient
import com.orvex.browser.browser.UrlUtils
import com.orvex.browser.browser.applySecureDefaults

@Composable
fun BrowserScreen(viewModel: BrowserViewModel = viewModel()) {
    val state = viewModel.uiState
    val focusManager = LocalFocusManager.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    val initialUrl = remember { viewModel.uiState.url }

    BackHandler(enabled = state.canGoBack) {
        webView?.goBack()
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AddressBar(
                text = viewModel.addressInput,
                isSecure = state.isSecure,
                isLoading = state.isLoading,
                progress = state.progress,
                onTextChange = viewModel::onAddressChange,
                onFocusChange = viewModel::onEditingChanged,
                onGo = {
                    UrlUtils.resolve(viewModel.addressInput)?.let { webView?.loadUrl(it) }
                    focusManager.clearFocus()
                }
            )
        },
        bottomBar = {
            BrowserBottomBar(
                canGoBack = state.canGoBack,
                canGoForward = state.canGoForward,
                isLoading = state.isLoading,
                onBack = { webView?.goBack() },
                onForward = { webView?.goForward() },
                onReloadOrStop = {
                    if (state.isLoading) webView?.stopLoading() else webView?.reload()
                },
                onHome = { webView?.loadUrl(UrlUtils.HOME_URL) }
            )
        }
    ) { innerPadding ->
        BrowserWebView(
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

@Composable
fun AddressBar(
    text: String,
    isSecure: Boolean,
    isLoading: Boolean,
    progress: Int,
    onTextChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onGo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .onFocusChanged { onFocusChange(it.isFocused) },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            placeholder = { Text(stringResource(R.string.address_hint)) },
            leadingIcon = {
                if (isSecure) {
                    Icon(Icons.Filled.Lock, contentDescription = stringResource(R.string.icon_secure))
                } else {
                    Icon(Icons.Filled.Warning, contentDescription = stringResource(R.string.icon_not_secure))
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(onGo = { onGo() })
        )
        Box(modifier = Modifier.fillMaxWidth().height(3.dp)) {
            if (isLoading) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun BrowserBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    isLoading: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onReloadOrStop: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier, tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, enabled = canGoBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
            }
            IconButton(onClick = onForward, enabled = canGoForward) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.action_forward))
            }
            IconButton(onClick = onReloadOrStop) {
                if (isLoading) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.action_stop))
                } else {
                    Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.action_reload))
                }
            }
            IconButton(onClick = onHome) {
                Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.action_home))
            }
        }
    }
}

@Composable
fun BrowserWebView(
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
