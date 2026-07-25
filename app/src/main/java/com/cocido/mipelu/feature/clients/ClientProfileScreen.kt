package com.cocido.mipelu.feature.clients

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.NudeClaro
import com.cocido.mipelu.core.theme.NudeTexto
import com.cocido.mipelu.core.ui.components.AvatarInitials
import com.cocido.mipelu.core.ui.components.BadgeTag
import com.cocido.mipelu.core.ui.components.badgeContainerColor
import com.cocido.mipelu.core.ui.components.badgeContentColor
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluButtonStyle
import com.cocido.mipelu.core.ui.components.SectionLabel
import com.cocido.mipelu.core.ui.components.TopBarBack
import com.cocido.mipelu.core.util.toShortDateEs
import com.cocido.mipelu.domain.model.WorkRecord

@Composable
fun ClientProfileScreen(
    onBack: () -> Unit,
    onNuevoTrabajo: (clientId: String) -> Unit,
    onEditarFicha: (clientId: String) -> Unit,
    onWorkClick: (String) -> Unit,
    viewModel: ClientProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val client = uiState.client

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarBack(title = "Perfil de clienta", onBack = onBack)
        if (client == null) return@Column

        LazyColumn(
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AvatarInitials(initials = client.initials, size = 48.dp)
                    Column {
                        Text(client.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                        Text(client.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            val tags = client.tagLabels()
            if (tags.isNotEmpty()) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.forEach { BadgeTag(label = it) }
                    }
                }
            }
            if (client.importantNotes.isNotBlank()) {
                item {
                    Text(
                        text = client.importantNotes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NudeTexto,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NudeClaro, MaterialTheme.shapes.medium)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiPeluButton(
                        text = "Nuevo trabajo",
                        onClick = { onNuevoTrabajo(client.id) },
                        modifier = Modifier.weight(1f),
                        height = 48.dp,
                        shape = MaterialTheme.shapes.small,
                    )
                    MiPeluButton(
                        text = "Editar ficha",
                        onClick = { onEditarFicha(client.id) },
                        modifier = Modifier.weight(1f),
                        height = 48.dp,
                        shape = MaterialTheme.shapes.small,
                        style = MiPeluButtonStyle.Secondary,
                    )
                }
            }
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            InfoField("Tipo de cabello", client.hairType, Modifier.weight(1f))
                            InfoField("Color base", client.baseColor, Modifier.weight(1f))
                        }
                        InfoField("Estado actual", uiState.lastHairCondition, Modifier.fillMaxWidth())
                        InfoField("Preferencias", client.preferences, Modifier.fillMaxWidth())
                    }
                }
            }
            item { SectionLabel("Historial") }
            if (uiState.works.isEmpty()) {
                item {
                    Text(
                        "Todavía no hay trabajos registrados para esta clienta.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    )
                }
            } else {
                items(uiState.works, key = { it.id }) { work ->
                    ClientTimelineItem(work = work, onClick = { onWorkClick(work.id) })
                }
            }
        }
    }
}

@Composable
private fun InfoField(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(3.dp))
        Text(
            value.ifBlank { "—" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun ClientTimelineItem(work: WorkRecord, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BadgeTag(
                    label = work.serviceType.label,
                    containerColor = work.serviceType.badgeContainerColor(),
                    contentColor = work.serviceType.badgeContentColor(),
                )
                Text(work.date.toShortDateEs(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (work.formula.isNotBlank()) {
                Text(work.formula, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    work.productsUsed.ifBlank { "" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(work.price, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}
