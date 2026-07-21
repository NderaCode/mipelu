package com.cocido.mipelu.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cocido.mipelu.core.theme.NudeClaro
import com.cocido.mipelu.core.theme.Marron
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluButtonStyle

@Composable
fun OnboardingScreen(
    onCrearCuenta: () -> Unit,
    onIniciarSesion: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(NudeClaro, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = Marron,
                modifier = Modifier.padding(48.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "El historial profesional de cada clienta, siempre a mano.",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Guardá fórmulas, fotos, productos, notas y resultados de cada trabajo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MiPeluButton(
                text = "Crear cuenta",
                onClick = onCrearCuenta,
                modifier = Modifier.fillMaxWidth(),
            )
            MiPeluButton(
                text = "Iniciar sesión",
                onClick = onIniciarSesion,
                modifier = Modifier.fillMaxWidth(),
                style = MiPeluButtonStyle.Secondary,
            )
        }
    }
}
