package com.cocido.mipelu.feature.clients

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Surface
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
import com.cocido.mipelu.core.ui.components.AvatarInitials
import com.cocido.mipelu.core.ui.components.BadgeTag
import com.cocido.mipelu.core.ui.components.EmptyState
import com.cocido.mipelu.core.ui.components.MiPeluChip
import com.cocido.mipelu.core.ui.components.MiPeluSearchField
import com.cocido.mipelu.core.ui.components.miPeluCardShadow
import com.cocido.mipelu.core.theme.ScreenTitleStyle
import com.cocido.mipelu.domain.model.Client

@Composable
fun ClientListScreen(
    onClientClick: (String) -> Unit,
    onAddClient: () -> Unit,
    viewModel: ClientListViewModel = hiltViewModel(),
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
            "Clientas",
            style = ScreenTitleStyle,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(16.dp))
        MiPeluSearchField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchChange,
            placeholder = "Buscar clienta",
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(modifier = Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ClientFilter.entries) { filter ->
                MiPeluChip(
                    label = filter.label,
                    selected = uiState.selectedFilter == filter,
                    onClick = { viewModel.onFilterSelected(filter) },
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            if (uiState.isLoading && uiState.clients.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.isEmpty) {
                EmptyState(
                    title = "Todavía no cargaste clientas",
                    subtitle = "Agregá tu primera clienta para empezar a guardar su historial.",
                    ctaLabel = "Agregar clienta",
                    onCtaClick = onAddClient,
                    icon = Icons.Filled.People,
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.clients, key = { it.id }) { client ->
                        ClientListItem(client = client, onClick = { onClientClick(client.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientListItem(client: Client, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .miPeluCardShadow(shape = MaterialTheme.shapes.medium)
            .semantics(mergeDescendants = true) {}
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AvatarInitials(initials = client.initials)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(client.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground)
                val lastLabel = client.hairType.ifBlank { "Sin trabajos registrados" }
                Text(lastLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val tags = client.tagLabels()
                if (tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.forEach { BadgeTag(label = it) }
                    }
                }
            }
        }
    }
}
