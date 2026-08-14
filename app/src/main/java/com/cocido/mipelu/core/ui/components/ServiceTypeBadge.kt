package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cocido.mipelu.core.theme.BadgeBalayage
import com.cocido.mipelu.core.theme.BadgeColor
import com.cocido.mipelu.core.theme.BadgeKeratina
import com.cocido.mipelu.core.theme.BadgeTratamiento
import com.cocido.mipelu.core.theme.NudeClaro
import com.cocido.mipelu.core.theme.NudeTexto
import com.cocido.mipelu.domain.model.ServiceType

/**
 * Colores de etiqueta por tipo de servicio, según la guía de marca (Color, Balayage, Keratina,
 * Tratamiento). El resto de los tipos no tiene color propio especificado y usa el estilo genérico.
 */
fun ServiceType.badgeContentColor(): Color = when (this) {
    ServiceType.COLOR -> BadgeColor
    ServiceType.BALAYAGE -> BadgeBalayage
    ServiceType.KERATINA -> BadgeKeratina
    ServiceType.TRATAMIENTO -> BadgeTratamiento
    else -> NudeTexto
}

fun ServiceType.badgeContainerColor(): Color = when (this) {
    ServiceType.COLOR, ServiceType.BALAYAGE, ServiceType.KERATINA, ServiceType.TRATAMIENTO ->
        badgeContentColor().copy(alpha = 0.12f)
    else -> NudeClaro
}

/** Un badge por tipo de servicio del trabajo, envueltos si no entran en una sola fila. */
@Composable
fun ServiceTypeBadgeRow(types: List<ServiceType>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        types.forEach { type ->
            BadgeTag(
                label = type.label,
                containerColor = type.badgeContainerColor(),
                contentColor = type.badgeContentColor(),
            )
        }
    }
}
