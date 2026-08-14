package com.cocido.mipelu.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cocido.mipelu.core.theme.miPeluColors
import com.cocido.mipelu.core.util.toShortDateEs
import com.cocido.mipelu.domain.model.WorkRecord

@Composable
private fun ThumbSlot(url: String?, size: androidx.compose.ui.unit.Dp) {
    var loadFailed by remember(url) { mutableStateOf(false) }
    if (!url.isNullOrBlank() && !loadFailed) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
            onError = { loadFailed = true },
        )
    } else {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.size(size).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
        )
    }
}

/**
 * Fila de trabajo reusada en Inicio (últimos trabajos) y Trabajos (lista completa).
 */
@Composable
fun WorkListItem(
    work: WorkRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFormulaSummary: Boolean = false,
    photoSize: androidx.compose.ui.unit.Dp = 32.dp,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .miPeluCardShadow(shape = MaterialTheme.shapes.medium)
            .semantics(mergeDescendants = true) {}
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                ThumbSlot(work.beforePhotoUrls.firstOrNull(), photoSize)
                ThumbSlot(work.afterPhotoUrls.firstOrNull(), photoSize)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = work.clientName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    ServiceTypeBadgeRow(types = work.serviceTypes)
                }
                Text(
                    text = work.date.toShortDateEs(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.miPeluColors.textoMuted,
                )
                if (showFormulaSummary && work.formula.isNotBlank()) {
                    Text(
                        text = work.formula,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
