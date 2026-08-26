package com.cocido.mipelu.feature.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.TextoMuted
import com.cocido.mipelu.core.ui.TestTags
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluTextField
import com.cocido.mipelu.core.ui.components.TopBarBack
import com.cocido.mipelu.domain.model.PlanLimits

@Composable
fun NewClientScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: NewClientViewModel = hiltViewModel(),
) {
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isBlockedByLimit by viewModel.isBlockedByLimit.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarBack(title = "Nueva clienta", onBack = onBack)
        if (isBlockedByLimit) {
            ClientLimitReachedNotice()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(nuevaClientaFields) { field ->
                    MiPeluTextField(
                        value = field.getter(draft),
                        onValueChange = { viewModel.updateField(field, it) },
                        label = field.label,
                        placeholder = field.placeholder,
                        multiline = field.multiline,
                        modifier = if (field === nuevaClientaFields.first()) {
                            Modifier.testTag(TestTags.NEW_CLIENT_NAME_FIELD)
                        } else {
                            Modifier
                        },
                    )
                }
                errorMessage?.let { message ->
                    item {
                        Text(
                            message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                item {
                    MiPeluButton(
                        text = if (isSaving) "Guardando..." else "Guardar clienta",
                        onClick = { viewModel.save(onSaved) },
                        modifier = Modifier.fillMaxWidth().testTag(TestTags.NEW_CLIENT_SAVE_BUTTON),
                        enabled = !isSaving,
                    )
                }
            }
        }
    }
}

/**
 * Estado bloqueado del plan gratuito: solo texto informativo, sin botón ni link a ningún medio de
 * pago. La exención de consumption-only de Google Play permite mencionar que existe un plan Pro,
 * pero no invitar a comprarlo desde dentro de la app (ver Payments policy).
 */
@Composable
private fun ClientLimitReachedNotice() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 40.dp)
            .testTag(TestTags.NEW_CLIENT_LIMIT_NOTICE),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Outlined.Lock, contentDescription = null, tint = TextoMuted)
        Text(
            text = "Llegaste al límite del plan gratuito",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 14.dp),
        )
        Text(
            text = "Alcanzaste el límite de ${PlanLimits.FREE_CLIENT_LIMIT} clientas del plan " +
                "gratuito. Activá el plan Pro para cargar clientas ilimitadas.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
