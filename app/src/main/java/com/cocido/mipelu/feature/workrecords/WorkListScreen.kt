package com.cocido.mipelu.feature.workrecords

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.ui.components.EmptyState
import com.cocido.mipelu.core.ui.components.MiPeluChip
import com.cocido.mipelu.core.ui.components.MiPeluSearchField
import com.cocido.mipelu.core.ui.components.WorkListItem
import com.cocido.mipelu.core.theme.ScreenTitleStyle

@Composable
fun WorkListScreen(
    onWorkClick: (String) -> Unit,
    onAddWork: () -> Unit,
    viewModel: WorkListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 20.dp),
    ) {
        Text(
            "Trabajos",
            style = ScreenTitleStyle,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(16.dp))
        MiPeluSearchField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchChange,
            placeholder = "Buscar por clienta, fórmula o trabajo",
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(modifier = Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                MiPeluChip(label = "Todas", selected = uiState.selectedType == null, onClick = { viewModel.onTypeSelected(null) })
            }
            items(uiState.availableTypes) { type ->
                MiPeluChip(label = type.label, selected = uiState.selectedType == type, onClick = { viewModel.onTypeSelected(type) })
            }
        }
        Spacer(Modifier.height(8.dp))

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            if (uiState.isLoading && uiState.works.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.error != null && uiState.works.isEmpty()) {
                EmptyState(
                    title = "No se pudieron cargar los trabajos",
                    subtitle = uiState.error.orEmpty(),
                    ctaLabel = "Reintentar",
                    onCtaClick = viewModel::refresh,
                    icon = Icons.Filled.CloudOff,
                )
            } else if (uiState.isEmpty) {
                EmptyState(
                    title = "Todavía no hay trabajos registrados",
                    subtitle = "Guardá el primer trabajo para empezar a construir el historial.",
                    ctaLabel = "Nuevo trabajo",
                    onCtaClick = onAddWork,
                    icon = Icons.Filled.Work,
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 20.dp),
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
}
