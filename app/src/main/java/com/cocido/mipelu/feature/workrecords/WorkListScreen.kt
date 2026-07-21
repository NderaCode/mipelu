package com.cocido.mipelu.feature.workrecords

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.ui.components.EmptyState
import com.cocido.mipelu.core.ui.components.MiPeluChip
import com.cocido.mipelu.core.ui.components.MiPeluSearchField
import com.cocido.mipelu.core.ui.components.WorkListItem

@Composable
fun WorkListScreen(
    onWorkClick: (String) -> Unit,
    onAddWork: () -> Unit,
    viewModel: WorkListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp, vertical = 20.dp)) {
        Text("Trabajos", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))
        MiPeluSearchField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchChange,
            placeholder = "Buscar por clienta, fórmula o trabajo",
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                MiPeluChip(label = "Todas", selected = uiState.selectedType == null, onClick = { viewModel.onTypeSelected(null) })
            }
            items(uiState.availableTypes) { type ->
                MiPeluChip(label = type.label, selected = uiState.selectedType == type, onClick = { viewModel.onTypeSelected(type) })
            }
        }
        Spacer(Modifier.height(8.dp))

        if (uiState.isEmpty) {
            EmptyState(
                title = "Todavía no hay trabajos registrados",
                subtitle = "Guardá el primer trabajo para empezar a construir el historial.",
                ctaLabel = "Nuevo trabajo",
                onCtaClick = onAddWork,
                icon = Icons.Filled.Work,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(uiState.works, key = { it.id }) { work ->
                    WorkListItem(
                        work = work,
                        onClick = { onWorkClick(work.id) },
                        showFormulaSummary = true,
                        photoSize = 40.dp,
                    )
                }
            }
        }
    }
}
