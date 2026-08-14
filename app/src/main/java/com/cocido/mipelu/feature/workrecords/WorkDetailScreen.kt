package com.cocido.mipelu.feature.workrecords

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.ErrorLogout
import com.cocido.mipelu.core.ui.components.BeforeAfterPhotos
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluButtonStyle
import com.cocido.mipelu.core.ui.components.ServiceTypeBadgeRow
import com.cocido.mipelu.core.ui.components.TopBarBack
import com.cocido.mipelu.core.util.toShortDateEs
import com.cocido.mipelu.domain.model.WorkRecord

@Composable
fun WorkDetailScreen(
    onBack: () -> Unit,
    onEdit: (workId: String) -> Unit,
    onDuplicate: (workId: String) -> Unit,
    viewModel: WorkDetailViewModel = hiltViewModel(),
) {
    val work by viewModel.work.collectAsStateWithLifecycle()
    val deleteError by viewModel.deleteError.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(deleteError) {
        deleteError?.let { android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show() }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("¿Eliminar este trabajo?") },
            text = { Text("Se van a borrar también sus fotos antes/después. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.deleteWork(onBack)
                }) { Text("Eliminar", color = ErrorLogout) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            },
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarBack(title = work?.clientName.orEmpty(), onBack = onBack) {
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Filled.DeleteOutline,
                    contentDescription = "Eliminar trabajo",
                    tint = ErrorLogout,
                )
            }
        }
        val current = work ?: return@Column

        LazyColumn(
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(current.date.toShortDateEs(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ServiceTypeBadgeRow(types = current.serviceTypes)
                }
            }
            item {
                BeforeAfterPhotos(
                    beforeUrl = current.beforePhotoUrls.firstOrNull(),
                    afterUrl = current.afterPhotoUrls.firstOrNull(),
                    onBeforeUrlPicked = {},
                    onAfterUrlPicked = {},
                    photoHeight = 160.dp,
                )
            }
            item {
                DetailCard(title = "Fórmula") {
                    InfoRow("Fórmula", current.formula)
                    InfoRow("Productos", current.productsUsed)
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        InfoRow("Oxidante / volumen", current.oxidantVolume, Modifier.weight(1f))
                        InfoRow("Tiempo", current.exposureTime, Modifier.weight(1f))
                    }
                    InfoRow("Técnica aplicada", current.technique)
                }
            }
            item {
                DetailCard(title = "Diagnóstico") {
                    InfoRow("Estado del cabello", current.hairCondition)
                    InfoRow("Objetivo del trabajo", current.objective)
                }
            }
            item {
                DetailCard(title = "Resultado") {
                    InfoRow("Resultado final", current.finalResult)
                    InfoRow("Precio", current.price)
                    InfoRow("Recomendaciones", current.recommendations)
                    InfoRow("Próximo seguimiento", current.nextFollowUpNote)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiPeluButton(
                        text = "Editar",
                        onClick = { onEdit(current.id) },
                        modifier = Modifier.weight(1f),
                        height = 48.dp,
                        shape = MaterialTheme.shapes.small,
                        style = MiPeluButtonStyle.Secondary,
                    )
                    MiPeluButton(
                        text = "Duplicar",
                        onClick = { onDuplicate(current.id) },
                        modifier = Modifier.weight(1f),
                        height = 48.dp,
                        shape = MaterialTheme.shapes.small,
                        style = MiPeluButtonStyle.Secondary,
                    )
                    MiPeluButton(
                        text = "Compartir",
                        onClick = { shareWorkSummary(context, current) },
                        modifier = Modifier.weight(1f),
                        height = 48.dp,
                        shape = MaterialTheme.shapes.small,
                        style = MiPeluButtonStyle.Secondary,
                    )
                }
            }
        }
    }
}

private fun shareWorkSummary(context: android.content.Context, work: WorkRecord) {
    val summary = buildString {
        val types = work.serviceTypes.joinToString(" + ") { it.label }
        append("${work.clientName} — $types (${work.date.toShortDateEs()})\n")
        if (work.formula.isNotBlank()) append("Fórmula: ${work.formula}\n")
        if (work.productsUsed.isNotBlank()) append("Productos: ${work.productsUsed}\n")
        if (work.finalResult.isNotBlank()) append("Resultado: ${work.finalResult}\n")
        if (work.price.isNotBlank()) append("Precio: ${work.price}")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, summary.trim())
    }
    context.startActivity(Intent.createChooser(intent, "Compartir resumen"))
}

@Composable
private fun DetailCard(title: String, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}
