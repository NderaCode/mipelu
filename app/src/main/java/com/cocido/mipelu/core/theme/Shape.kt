package com.cocido.mipelu.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val MiPeluShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// Radios puntuales del diseño que no calzan en la escala M3 (botón/search pill, FAB).
val PillShape = RoundedCornerShape(100.dp)
val FabShape = RoundedCornerShape(16.dp)
