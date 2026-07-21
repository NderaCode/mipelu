package com.cocido.mipelu.core.theme

import androidx.compose.ui.graphics.Color

// Paleta extraída 1:1 del diseño (Mi Pelu.dc.html)
val Terracota = Color(0xFFBF3327) // primario / acento
val Charcoal = Color(0xFF221715) // texto principal
val TextoSecundario = Color(0xFF79695F)
val TextoMuted = Color(0xFFA79A8E)
val Marron = Color(0xFF8C6A46) // avatar / chip oscuro
val NudeClaro = Color(0xFFF0DCCB) // fondo avatar/tag/nota destacada
val NudeTexto = Color(0xFF5C3A22) // texto sobre nude
val FondoApp = Color(0xFFF7F1EA)
val FondoContenedor = Color(0xFFEDE6D9)
val Superficie = Color(0xFFFFFFFF)
val ErrorLogout = Color(0xFF9C2B20)

// Bordes de card: negro sobre blanco a distintas opacidades
val BordeCard = Charcoal.copy(alpha = 0.078f) // #22171514
val BordeCampo = Charcoal.copy(alpha = 0.102f) // #2217151A
val BordeSuave = Charcoal.copy(alpha = 0.122f) // #2217151F
val DivisorSutil = Charcoal.copy(alpha = 0.059f) // #2217150F

val FondoBorrador = Color(0xFFF1E8DE) // campo de solo lectura (clienta en Nuevo trabajo)
