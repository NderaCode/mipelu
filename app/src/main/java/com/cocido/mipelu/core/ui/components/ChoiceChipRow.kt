package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Suggested values for a text field (hair type, porosity, oxidant…). Tapping a chip writes that
 * value into the field; the field stays editable so a professional can still type "ondulado, grueso".
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChoiceChipRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (options.isEmpty()) return
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            val isSelected = selected.equals(option, ignoreCase = true) ||
                selected.startsWith("$option,", ignoreCase = true) ||
                selected.startsWith("$option ", ignoreCase = true)
            MiPeluChip(
                label = option,
                selected = isSelected,
                onClick = { onSelect(option) },
            )
        }
    }
}
