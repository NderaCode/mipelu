package com.cocido.mipelu.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.CiruelaProfundo
import com.cocido.mipelu.core.theme.Morado
import com.cocido.mipelu.core.theme.RosaDorado
import com.cocido.mipelu.core.ui.components.AvatarInitials
import com.cocido.mipelu.core.ui.components.SectionLabel
import com.cocido.mipelu.core.ui.components.WorkListItem
import com.cocido.mipelu.core.ui.components.miPeluCardShadow
import com.cocido.mipelu.core.theme.Superficie

private val HeroShape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)

@Composable
fun HomeScreen(
    onNuevaClienta: () -> Unit,
    onNuevoTrabajo: () -> Unit,
    onBuscarHistorial: () -> Unit,
    onWorkClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = viewModel::refresh,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item { HeroHeader(greetingName = uiState.greetingName) }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-10).dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard("Clientas registradas", uiState.clientCount, Modifier.weight(1f).fillMaxHeight())
                StatCard("Trabajos guardados", uiState.workCount, Modifier.weight(1f).fillMaxHeight())
                StatCard("Fotos guardadas", uiState.photoCount, Modifier.weight(1f).fillMaxHeight())
            }
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SectionLabel("Accesos rápidos")
                QuickAccessRow(
                    icon = Icons.Filled.PersonAdd,
                    label = "Nueva clienta",
                    onClick = onNuevaClienta,
                )
                QuickAccessRow(
                    icon = Icons.AutoMirrored.Outlined.NoteAdd,
                    label = "Nuevo trabajo",
                    onClick = onNuevoTrabajo,
                )
                QuickAccessRow(
                    icon = Icons.Filled.Search,
                    label = "Buscar historial",
                    onClick = onBuscarHistorial,
                )
            }
        }
        item {
            Box(Modifier.padding(horizontal = 20.dp)) {
                SectionLabel("Últimos trabajos")
            }
        }
        items(uiState.recentWorks, key = { it.id }) { work ->
            WorkListItem(
                work = work,
                onClick = { onWorkClick(work.id) },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
    }
    }
}

@Composable
private fun HeroHeader(greetingName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .miPeluCardShadow(shape = HeroShape, elevation = 16.dp)
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(0f to CiruelaProfundo, 0.65f to Morado),
                ),
                shape = HeroShape,
            )
            // El degradé pinta detrás de la barra de estado (fillMaxWidth mide antes de este
            // padding, así que el fondo cubre esa zona); el contenido en sí arranca debajo,
            // respetando el inset real del dispositivo en vez de un valor fijo.
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(20.dp, 16.dp, 20.dp, 52.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AvatarInitials(
                initials = greetingName.firstOrNull()?.uppercase() ?: "?",
                containerColor = RosaDorado,
                contentColor = CiruelaProfundo,
            )
            Text(
                text = "Hola, $greetingName",
                style = MaterialTheme.typography.titleMedium,
                color = Superficie,
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.miPeluCardShadow(shape = MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp).fillMaxWidth().fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = value.toString(), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun QuickAccessRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().miPeluCardShadow(shape = MaterialTheme.shapes.small),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
