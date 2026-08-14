package com.cocido.mipelu.core.theme

import androidx.compose.ui.graphics.Color

// Paleta extraída 1:1 del rediseño + guía de marca (Mi Pelu.dc.html / Mi Pelu - Branding.dc.html)
val CiruelaProfundo = Color(0xFF3D1638) // fondos oscuros (splash, ícono de la app)
val Morado = Color(0xFF5C2350) // primario / acento (botones, acciones)
val CiruelaMedio = Color(0xFF6B2B58) // avatares / badges
val Mauve = Color(0xFF8C4A63) // overline / labels de marca
val RosaDorado = Color(0xFFE8B594) // acento claro / hover
val Charcoal = Color(0xFF2B1B26) // texto principal
val TextoSecundario = Color(0xFF79695F)
val TextoMuted = Color(0xFFA79A8E)
val NudeClaro = Color(0xFFF0DCCB) // fondo avatar/tag/nota destacada (champagne)
val NudeTexto = Color(0xFF5C3A22) // texto sobre nude (nota destacada)
val FondoApp = Color(0xFFF7F1EA)
val FondoContenedor = Color(0xFFEDE6D9)
val Superficie = Color(0xFFFFFFFF)
val ErrorLogout = Color(0xFF8C2F45)

// Texto sobre fondo oscuro (splash + hero de Inicio)
val TextoSobreOscuro = Color(0xFFF3E4D8)
val TextoSecundarioSobreOscuro = Color(0xFFD8B8C9)
val SaludoSobreHero = Color(0xFFE8CFE0)

// Bordes de card: negro sobre blanco a distintas opacidades
val BordeCard = Charcoal.copy(alpha = 0.051f) // #2B1B260D
val BordeCampo = Charcoal.copy(alpha = 0.102f) // #2B1B261A
val BordeSuave = Charcoal.copy(alpha = 0.122f) // #2B1B261F
val DivisorSutil = Charcoal.copy(alpha = 0.059f) // #2B1B260F

val FondoBorrador = Color(0xFFF1E8DE) // campo de solo lectura (clienta en Nuevo trabajo)

// Colores de etiqueta por tipo de servicio (guía de marca: Color, Balayage, Keratina, Tratamiento).
// El resto de los tipos no tiene color propio especificado en la guía y usa el estilo genérico (NudeClaro/NudeTexto).
// BadgeColor y BadgeTratamiento están oscurecidos respecto del valor original de la guía de marca
// (#C97F55 / #5F7A63) porque a esa luminosidad el texto sobre su propio fondo tintado al 12% no
// llegaba a 4.5:1 (WCAG AA) - con este tono sí (~4.4-5.5:1 según el tipo).
val BadgeColor = Color(0xFF9C5730)
val BadgeBalayage = CiruelaMedio
val BadgeKeratina = Mauve
val BadgeTratamiento = Color(0xFF4F6753)
