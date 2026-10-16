package com.cocido.mipelu.feature.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.ui.TestTags
import com.cocido.mipelu.core.ui.components.MiPeluChip
import com.cocido.mipelu.core.ui.components.TopBarBack

private val weekdayLabels = listOf(
    0 to "Dom",
    1 to "Lun",
    2 to "Mar",
    3 to "Mié",
    4 to "Jue",
    5 to "Vie",
    6 to "Sáb",
)

private val slotOptions = listOf(
    30 to "Cada 30 min",
    45 to "Cada 45 min",
    60 to "Cada 1 hora",
    90 to "Cada 1 h 30",
    120 to "Cada 2 horas",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingSettingsScreen(
    onBack: () -> Unit,
    viewModel: BookingSettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val selectedDays = settings?.weekly?.map { it.weekday }?.toSet().orEmpty()
    val slotMinutes = settings?.slotMinutes ?: 60

    Column(modifier = Modifier.fillMaxSize()) {
        TopBarBack(title = "Turnero", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Text("Tu link", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                settings?.publicUrl ?: "…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag(TestTags.AGENDA_PUBLIC_URL),
            )
            Spacer(Modifier.height(24.dp))
            Text("Días en los que atendés", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                weekdayLabels.forEach { (day, label) ->
                    MiPeluChip(
                        label = label,
                        selected = day in selectedDays,
                        onClick = { viewModel.toggleWeekday(day, day !in selectedDays) },
                        multiSelect = true,
                    )
                }
            }
            Text(
                "Horario 9 a 18 (Argentina). El trabajo lo cargás después, en la ficha.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            Spacer(Modifier.height(24.dp))
            Text("¿Cada cuánto tomás una clienta?", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "Así aparecen los horarios en tu link, para no pisarte. No es cuánto dura el trabajo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                slotOptions.forEach { (minutes, label) ->
                    MiPeluChip(
                        label = label,
                        selected = slotMinutes == minutes,
                        onClick = { viewModel.setSlotMinutes(minutes) },
                    )
                }
            }
        }
    }
}
