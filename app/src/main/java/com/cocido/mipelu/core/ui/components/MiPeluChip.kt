package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.cocido.mipelu.core.theme.Morado
import com.cocido.mipelu.core.theme.PillShape
import com.cocido.mipelu.core.theme.miPeluColors

/**
 * Chip de selección. [multiSelect] = false (default) usa semántica de radio button, para grupos
 * de selección única (filtros); [multiSelect] = true usa semántica de checkbox, para grupos donde
 * se puede marcar más de una opción (tipo de servicio en Nuevo trabajo).
 */
@Composable
fun MiPeluChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    multiSelect: Boolean = false,
) {
    val containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
    val shadowModifier = if (selected) Modifier.miPeluGlowShadow(color = Morado, shape = PillShape, elevation = 6.dp) else Modifier
    val selectionModifier = if (multiSelect) {
        Modifier.toggleable(value = selected, onValueChange = { onClick() }, role = Role.Checkbox)
    } else {
        Modifier.selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
    }
    androidx.compose.material3.Surface(
        modifier = modifier.then(shadowModifier).then(selectionModifier),
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
