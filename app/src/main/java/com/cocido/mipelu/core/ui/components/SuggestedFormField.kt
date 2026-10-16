package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SuggestedFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    multiline: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    suggestions: List<String> = emptyList(),
    imeAction: ImeAction = if (multiline) ImeAction.Default else ImeAction.Next,
    onDone: (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (suggestions.isNotEmpty()) {
            ChoiceChipRow(
                options = suggestions,
                selected = value,
                onSelect = onValueChange,
            )
        }
        MiPeluTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = modifier,
            placeholder = placeholder,
            multiline = multiline,
            keyboardType = keyboardType,
            imeAction = imeAction,
            onDone = onDone,
            isError = isError,
            supportingText = supportingText,
        )
    }
}
