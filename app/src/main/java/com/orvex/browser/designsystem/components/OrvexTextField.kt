package com.orvex.browser.designsystem.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import com.orvex.browser.designsystem.theme.OrvexTheme

/**
 * Orvex stilinde tek satırlık metin alanı. Adres çubuğunun temelidir.
 */
@Composable
fun OrvexTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    val colors = OrvexTheme.colors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.onFocusChanged { onFocusChanged(it.isFocused) },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            cursorColor = colors.accent,
            focusedBorderColor = colors.accent,
            unfocusedBorderColor = colors.outline,
            focusedContainerColor = colors.surfaceHigh,
            unfocusedContainerColor = colors.surface,
            focusedPlaceholderColor = colors.textSecondary,
            unfocusedPlaceholderColor = colors.textSecondary,
            focusedLeadingIconColor = colors.accent,
            unfocusedLeadingIconColor = colors.textSecondary
        )
    )
}
