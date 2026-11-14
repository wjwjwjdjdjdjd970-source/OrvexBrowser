package com.orvex.browser.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import com.orvex.browser.R
import com.orvex.browser.designsystem.components.OrvexGlassSurface
import com.orvex.browser.designsystem.components.OrvexProgressLine
import com.orvex.browser.designsystem.components.OrvexTextField
import com.orvex.browser.designsystem.icons.OrvexIcons
import com.orvex.browser.designsystem.theme.OrvexTheme

@Composable
fun OrvexAddressBar(
    text: String,
    isSecure: Boolean,
    isLoading: Boolean,
    progress: Int,
    onTextChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onGo: () -> Unit,
    modifier: Modifier = Modifier,
    maxContentWidth: Dp = Dp.Unspecified
) {
    val dimens = OrvexTheme.dimens
    OrvexGlassSurface(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OrvexTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = stringResource(R.string.address_hint),
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .fillMaxWidth()
                    .padding(horizontal = dimens.spaceMd, vertical = dimens.spaceSm),
                leadingIcon = {
                    Icon(
                        imageVector = if (isSecure) OrvexIcons.Secure else OrvexIcons.Insecure,
                        contentDescription = stringResource(
                            if (isSecure) R.string.icon_secure else R.string.icon_not_secure
                        ),
                        tint = if (isSecure) LocalContentColor.current else OrvexTheme.colors.danger
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = { onGo() }),
                onFocusChanged = onFocusChange
            )
            OrvexProgressLine(visible = isLoading, progress = progress)
        }
    }
}
