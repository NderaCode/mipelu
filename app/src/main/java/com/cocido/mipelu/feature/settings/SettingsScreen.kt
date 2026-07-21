package com.cocido.mipelu.feature.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.ErrorLogout
import com.cocido.mipelu.core.theme.Marron
import com.cocido.mipelu.core.theme.Superficie
import com.cocido.mipelu.core.ui.components.AvatarInitials
import com.cocido.mipelu.core.util.toHumanReadableSize

private fun String.toDisplayPlan(): String = when (lowercase()) {
    "free" -> "Gratuito"
    "pro" -> "Pro"
    else -> replaceFirstChar { it.uppercase() }
}

@Composable
fun SettingsScreen(
    onLoggedOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val comingSoon = { Toast.makeText(context, "Disponible próximamente", Toast.LENGTH_SHORT).show() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Text("Más", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground) }
        item {
            profile?.let { p ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        AvatarInitials(initials = p.initials, size = 52.dp, containerColor = Marron, contentColor = Superficie)
                        Column {
                            Text(p.professionalName.ifBlank { p.name }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                            Text("Estilista independiente", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        item {
            profile?.let { p ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column {
                        SettingsRow("Nombre profesional", p.professionalName.ifBlank { p.name })
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        SettingsRow(
                            "Uso de almacenamiento",
                            "${p.storageUsedBytes.toHumanReadableSize()} de ${p.storageLimitBytes.toHumanReadableSize()}",
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        SettingsRow("Plan actual", p.plan.toDisplayPlan())
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        SettingsRow("Exportar datos", onClick = comingSoon)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        SettingsRow("Ayuda", onClick = comingSoon)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        SettingsRow("Privacidad", onClick = comingSoon)
                    }
                }
            }
        }
        item {
            androidx.compose.material3.OutlinedButton(
                onClick = {
                    viewModel.logout()
                    onLoggedOut()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.small,
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = ErrorLogout),
            ) { Text("Cerrar sesión") }
        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String? = null, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
