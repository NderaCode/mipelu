package com.cocido.mipelu.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val MiPeluLightColorScheme = lightColorScheme(
    primary = Morado,
    onPrimary = Superficie,
    primaryContainer = NudeClaro,
    onPrimaryContainer = NudeTexto,
    secondary = CiruelaMedio,
    onSecondary = Superficie,
    secondaryContainer = NudeClaro,
    onSecondaryContainer = NudeTexto,
    background = FondoApp,
    onBackground = Charcoal,
    surface = Superficie,
    onSurface = Charcoal,
    surfaceVariant = FondoContenedor,
    onSurfaceVariant = TextoSecundario,
    outline = BordeCard,
    outlineVariant = DivisorSutil,
    error = ErrorLogout,
    onError = Superficie,
)

/**
 * Tokens que no tienen un slot 1:1 en [androidx.compose.material3.ColorScheme]
 * pero se repiten en el diseño (avatar, nota destacada, campo de solo lectura).
 */
data class MiPeluExtendedColors(
    // TextoMuted (#A79A8E) da ~2.7:1 sobre fondos claros, muy por debajo del 4.5:1 de WCAG AA.
    // Se usa TextoSecundario (que sí pasa AA, ~5.2:1) como único tono "muted" del sistema.
    val textoMuted: androidx.compose.ui.graphics.Color = TextoSecundario,
    val bordeCampo: androidx.compose.ui.graphics.Color = BordeCampo,
    val bordeSuave: androidx.compose.ui.graphics.Color = BordeSuave,
    val fondoBorrador: androidx.compose.ui.graphics.Color = FondoBorrador,
)

private val LocalMiPeluExtendedColors = staticCompositionLocalOf { MiPeluExtendedColors() }

val MaterialTheme.miPeluColors: MiPeluExtendedColors
    @Composable get() = LocalMiPeluExtendedColors.current

@Composable
fun MiPeluTheme(
    // El brief pide light mode primero; se ignora el tema oscuro del sistema por ahora.
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = MiPeluLightColorScheme
    CompositionLocalProvider(LocalMiPeluExtendedColors provides MiPeluExtendedColors()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MiPeluTypography,
            shapes = MiPeluShapes,
            content = content,
        )
    }
}
