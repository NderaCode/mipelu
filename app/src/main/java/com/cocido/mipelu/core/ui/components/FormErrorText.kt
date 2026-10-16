package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign

@Composable
fun FormErrorText(
    message: String,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        textAlign = textAlign,
        modifier = modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
    )
}
