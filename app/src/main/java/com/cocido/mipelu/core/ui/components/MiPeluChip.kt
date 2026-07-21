package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cocido.mipelu.core.theme.PillShape
import com.cocido.mipelu.core.theme.miPeluColors

@Composable
fun MiPeluChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
    androidx.compose.material3.Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = PillShape,
        color = containerColor,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.miPeluColors.bordeSuave),
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}
