package com.cocido.mipelu.feature.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.ErrorLogout
import com.cocido.mipelu.core.theme.CiruelaMedio
import com.cocido.mipelu.core.theme.Superficie
import com.cocido.mipelu.core.ui.components.AvatarInitials
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluTextField
import com.cocido.mipelu.core.ui.components.miPeluCardShadow
import com.cocido.mipelu.core.theme.ScreenTitleStyle
import com.cocido.mipelu.core.util.toHumanReadableSize
import com.cocido.mipelu.domain.model.UserProfile

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
    val deleteError by viewModel.deleteError.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val comingSoon = { Toast.makeText(context, "Disponible próximamente", Toast.LENGTH_SHORT).show() }
    var showEditProfile by remember { mutableStateOf(false) }
    var showDeleteAccountConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(deleteError) {
        deleteError?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    if (showEditProfile && profile != null) {
        EditProfileDialog(
            profile = profile!!,
            viewModel = viewModel,
            onDismiss = { showEditProfile = false },
        )
    }

    if (showDeleteAccountConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountConfirm = false },
            title = { Text("¿Eliminar tu cuenta?") },
            text = { Text("Se van a borrar todas tus clientas, trabajos y fotos de forma permanente. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAccountConfirm = false
                    viewModel.deleteAccount(onLoggedOut)
                }) { Text("Eliminar cuenta", color = ErrorLogout) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirm = false }) { Text("Cancelar") }
            },
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(20.dp, 28.dp, 20.dp, 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                "Perfil",
                style = ScreenTitleStyle,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() },
            )
        }
        item {
            profile?.let { p ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth().miPeluCardShadow(shape = MaterialTheme.shapes.medium),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        AvatarInitials(initials = p.initials, size = 52.dp, containerColor = CiruelaMedio, contentColor = Superficie)
                        Column {
                            Text(p.professionalName.ifBlank { p.name }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                            Text(p.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    modifier = Modifier.fillMaxWidth().miPeluCardShadow(shape = MaterialTheme.shapes.medium),
                ) {
                    Column {
                        SettingsRow(
                            "Nombre profesional",
                            p.professionalName.ifBlank { p.name },
                            onClick = { showEditProfile = true },
                        )
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
        item {
            TextButton(
                onClick = { showDeleteAccountConfirm = true },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Eliminar cuenta", color = ErrorLogout) }
        }
    }
}

@Composable
private fun EditProfileDialog(
    profile: UserProfile,
    viewModel: SettingsViewModel,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(profile.name) }
    var professionalName by remember { mutableStateOf(profile.professionalName) }
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar perfil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MiPeluTextField(value = name, onValueChange = { name = it }, label = "Nombre")
                MiPeluTextField(
                    value = professionalName,
                    onValueChange = { professionalName = it },
                    label = "Nombre profesional",
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                )
                if (errorMessage != null) {
                    Text(errorMessage.orEmpty(), style = MaterialTheme.typography.bodySmall, color = ErrorLogout)
                }
            }
        },
        confirmButton = {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.height(24.dp), color = MaterialTheme.colorScheme.primary)
            } else {
                MiPeluButton(
                    text = "Guardar",
                    onClick = { viewModel.updateProfile(name, professionalName, onDismiss) },
                    height = 40.dp,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Cancelar") }
        },
    )
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
