package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cocido.mipelu.core.theme.Morado
import com.cocido.mipelu.core.theme.PillShape

enum class MiPeluButtonStyle { Primary, Secondary, Text }

@Composable
fun MiPeluButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MiPeluButtonStyle = MiPeluButtonStyle.Primary,
    height: androidx.compose.ui.unit.Dp = 52.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(16.dp),
    enabled: Boolean = true,
) {
    val heightModifier = modifier.height(height)
    when (style) {
        MiPeluButtonStyle.Primary -> Button(
            onClick = onClick,
            modifier = if (enabled) heightModifier.miPeluGlowShadow(color = Morado, shape = shape) else heightModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) { Text(text, style = MaterialTheme.typography.labelLarge) }

        MiPeluButtonStyle.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = heightModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground,
            ),
        ) { Text(text, style = MaterialTheme.typography.labelLarge) }

        MiPeluButtonStyle.Text -> TextButton(
            onClick = onClick,
            modifier = heightModifier,
            enabled = enabled,
            shape = shape,
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) { Text(text, style = MaterialTheme.typography.bodyLarge) }
    }
}

@Composable
fun MiPeluPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp).miPeluGlowShadow(color = Morado, shape = PillShape),
        shape = PillShape,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) { Text(text, style = MaterialTheme.typography.bodyMedium) }
}
