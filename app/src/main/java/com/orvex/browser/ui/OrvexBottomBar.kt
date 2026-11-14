package com.orvex.browser.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.orvex.browser.R
import com.orvex.browser.designsystem.components.OrvexGlassSurface
import com.orvex.browser.designsystem.components.OrvexIconButton
import com.orvex.browser.designsystem.icons.OrvexIcons
import com.orvex.browser.designsystem.theme.OrvexTheme

@Composable
fun OrvexBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    isLoading: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onReloadOrStop: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier,
    maxContentWidth: Dp = Dp.Unspecified
) {
    val dimens = OrvexTheme.dimens
    OrvexGlassSurface(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = maxContentWidth)
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(dimens.barHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrvexIconButton(
                icon = OrvexIcons.Back,
                contentDescription = stringResource(R.string.action_back),
                onClick = onBack,
                enabled = canGoBack
            )
            OrvexIconButton(
                icon = OrvexIcons.Forward,
                contentDescription = stringResource(R.string.action_forward),
                onClick = onForward,
                enabled = canGoForward
            )
            OrvexIconButton(
                icon = if (isLoading) OrvexIcons.Stop else OrvexIcons.Reload,
                contentDescription = stringResource(
                    if (isLoading) R.string.action_stop else R.string.action_reload
                ),
                onClick = onReloadOrStop
            )
            OrvexIconButton(
                icon = OrvexIcons.Home,
                contentDescription = stringResource(R.string.action_home),
                onClick = onHome
            )
        }
    }
}
