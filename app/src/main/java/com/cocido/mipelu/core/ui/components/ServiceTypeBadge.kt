package com.cocido.mipelu.core.ui.components

import androidx.compose.ui.graphics.Color
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
