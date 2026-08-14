package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cocido.mipelu.core.theme.Charcoal

/**
 * Sombra tintada de superficie (cards, search field, filas de "Más"). El shadow de Material
 * por defecto usa un gris neutro fijo; esto lo reemplaza por el tono tinte del diseño
 * (Charcoal a baja opacidad) para lograr el mismo efecto que el box-shadow de dos capas del
 * prototipo. Modifier.shadow solo admite una capa, así que se aproxima con una sola sombra difusa.
 */
fun Modifier.miPeluCardShadow(
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 8.dp,
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = Charcoal.copy(alpha = 0.05f),
    spotColor = Charcoal.copy(alpha = 0.09f),
)

/** Sombra de "brillo" tintada con el color de marca, para botones primarios y chips seleccionados. */
fun Modifier.miPeluGlowShadow(
    color: Color,
    shape: Shape = RoundedCornerShape(100.dp),
    elevation: Dp = 10.dp,
): Modifier = shadow(
    elevation = elevation,
    shape = shape,
    ambientColor = color.copy(alpha = 0.22f),
    spotColor = color.copy(alpha = 0.3f),
)
