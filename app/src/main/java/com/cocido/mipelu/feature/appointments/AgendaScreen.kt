package com.cocido.mipelu.feature.appointments

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.ScreenTitleStyle
import com.cocido.mipelu.core.ui.TestTags
import com.cocido.mipelu.core.ui.components.EmptyState
import com.cocido.mipelu.core.ui.components.LoadingPane
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluButtonStyle
import com.cocido.mipelu.core.ui.components.miPeluCardShadow
import com.cocido.mipelu.domain.model.Appointment
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AgendaScreen(
    onOpenSettings: () -> Unit,
    viewModel: AgendaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 20.dp),
    ) {
        Text(
            "Agenda",
            style = ScreenTitleStyle,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Compartí tu link. La clienta elige día y hora; el trabajo lo cargás vos después, en la ficha.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        MiPeluButton(
            text = "Compartir link de turnos",
            onClick = {
                val url = uiState.settings?.publicUrl ?: return@MiPeluButton
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Reservá tu turno: $url")
                }
                context.startActivity(Intent.createChooser(send, "Compartir turnero"))
            },
            enabled = uiState.settings != null,
            modifier = Modifier.fillMaxWidth().testTag(TestTags.AGENDA_SHARE_BUTTON),
        )
        Spacer(Modifier.height(8.dp))
        MiPeluButton(
            text = "Horarios del turnero",
            onClick = onOpenSettings,
            style = MiPeluButtonStyle.Secondary,
            modifier = Modifier.fillMaxWidth().testTag(TestTags.AGENDA_SETTINGS_BUTTON),
        )
        Spacer(Modifier.height(16.dp))

        PullToRefreshBox(isRefreshing = uiState.isLoading, onRefresh = viewModel::refresh, modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.appointments.isEmpty() -> LoadingPane()
                uiState.isEmpty -> EmptyState(
                    title = "Todavía no hay turnos",
                    subtitle = "Compartí el link para que tus clientas elijan día y hora.",
                    icon = Icons.Outlined.Event,
                    ctaLabel = "Compartir link",
                    onCtaClick = {
                        val url = uiState.settings?.publicUrl ?: return@EmptyState
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Reservá tu turno: $url")
                        }
                        context.startActivity(Intent.createChooser(send, "Compartir turnero"))
                    },
                )
                else -> LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(uiState.appointments, key = { it.id }) { appointment ->
                        AppointmentRow(appointment, onCancel = { viewModel.cancel(appointment.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentRow(appointment: Appointment, onCancel: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .miPeluCardShadow(shape = MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large)
            .padding(16.dp),
    ) {
        Text(formatInstant(appointment.startsAt), style = MaterialTheme.typography.titleMedium)
        Text(appointment.guestName, style = MaterialTheme.typography.bodyLarge)
        if (!appointment.notes.isNullOrBlank()) {
            Text(
                appointment.notes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                appointment.guestEmail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onCancel) { Text("Cancelar") }
        }
    }
}

private val agendaFormat = DateTimeFormatter.ofPattern("EEE d MMM · HH:mm", Locale("es", "AR"))

private fun formatInstant(iso: String): String = runCatching {
    Instant.parse(iso).atZone(ZoneId.of("America/Argentina/Buenos_Aires")).format(agendaFormat)
}.getOrDefault(iso)
