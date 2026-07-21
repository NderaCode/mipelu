package com.cocido.mipelu.feature.workrecords

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.FondoBorrador
import com.cocido.mipelu.core.theme.miPeluColors
import com.cocido.mipelu.core.ui.components.BeforeAfterPhotos
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluButtonStyle
import com.cocido.mipelu.core.ui.components.MiPeluChip
import com.cocido.mipelu.core.ui.components.MiPeluTextField
import com.cocido.mipelu.core.ui.components.SectionLabel
import com.cocido.mipelu.core.ui.components.TopBarBack
import com.cocido.mipelu.core.util.toShortDateEs
import com.cocido.mipelu.domain.model.ServiceType
import com.cocido.mipelu.domain.model.WorkRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWorkScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: NewWorkViewModel = hiltViewModel(),
) {
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val clients by viewModel.clients.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    var clientMenuExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = draft.date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let(viewModel::setDate)
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            },
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarBack(title = if (draft.id.isBlank()) "Nuevo trabajo" else "Editar trabajo", onBack = onBack)
        LazyColumn(
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 110.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionLabel("Datos del trabajo")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Clienta", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (draft.clientName.isBlank()) {
                            Box {
                                ReadOnlyField(
                                    text = "Elegí una clienta",
                                    background = MaterialTheme.colorScheme.surface,
                                    textColor = MaterialTheme.miPeluColors.textoMuted,
                                    onClick = { clientMenuExpanded = true },
                                )
                                DropdownMenu(expanded = clientMenuExpanded, onDismissRequest = { clientMenuExpanded = false }) {
                                    clients.forEach { client ->
                                        DropdownMenuItem(
                                            text = { Text(client.name) },
                                            onClick = {
                                                viewModel.selectClient(client)
                                                clientMenuExpanded = false
                                            },
                                        )
                                    }
                                }
                            }
                        } else {
                            ReadOnlyField(
                                text = draft.clientName,
                                background = FondoBorrador,
                                textColor = MaterialTheme.colorScheme.onBackground,
                                onClick = null,
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Tipo de trabajo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ServiceType.entries.forEach { type ->
                                MiPeluChip(
                                    label = type.label,
                                    selected = draft.serviceType == type,
                                    onClick = { viewModel.selectServiceType(type) },
                                )
                            }
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Fecha", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        ReadOnlyField(
                            text = draft.date.toShortDateEs(),
                            background = MaterialTheme.colorScheme.surface,
                            textColor = MaterialTheme.colorScheme.onBackground,
                            onClick = { showDatePicker = true },
                        )
                    }
                }
            }

            item { WorkFieldSection(title = "Diagnóstico", fields = diagnosticoFields, draft = draft, onFieldChange = viewModel::updateField) }
            item { WorkFieldSection(title = "Fórmula y productos", fields = formulaFields, draft = draft, onFieldChange = viewModel::updateField) }
            item { WorkFieldSection(title = "Resultado", fields = resultadoFields, draft = draft, onFieldChange = viewModel::updateField) }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionLabel("Fotos")
                    BeforeAfterPhotos(
                        beforeUrl = draft.beforePhotoUrls.firstOrNull(),
                        afterUrl = draft.afterPhotoUrls.firstOrNull(),
                        onBeforeUrlPicked = viewModel::setBeforePhoto,
                        onAfterUrlPicked = viewModel::setAfterPhoto,
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiPeluButton(
                        text = "Guardar trabajo",
                        onClick = { viewModel.save(asDraft = false, onSaved = onSaved) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    MiPeluButton(
                        text = "Guardar borrador",
                        onClick = { viewModel.save(asDraft = true, onSaved = onSaved) },
                        modifier = Modifier.fillMaxWidth(),
                        style = MiPeluButtonStyle.Text,
                        height = 44.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyField(
    text: String,
    background: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)?,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = textColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(background, MaterialTheme.shapes.small)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .wrapContentWidth(Alignment.Start)
            .padding(horizontal = 14.dp),
    )
}

@Composable
private fun WorkFieldSection(
    title: String,
    fields: List<WorkFieldSpec>,
    draft: WorkRecord,
    onFieldChange: (WorkFieldSpec, String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel(title)
        fields.forEach { field ->
            MiPeluTextField(
                value = field.getter(draft),
                onValueChange = { onFieldChange(field, it) },
                label = field.label,
                placeholder = field.placeholder,
                multiline = field.multiline,
            )
        }
    }
}
