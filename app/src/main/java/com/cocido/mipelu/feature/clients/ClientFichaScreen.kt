package com.cocido.mipelu.feature.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.ui.components.EmptyState
import com.cocido.mipelu.core.ui.components.FormErrorText
import com.cocido.mipelu.core.ui.components.LoadingPane
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.SuggestedFormField
import com.cocido.mipelu.core.ui.components.TopBarBack

@Composable
fun ClientFichaScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ClientFichaViewModel = hiltViewModel(),
) {
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val notFound by viewModel.notFound.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarBack(title = "Ficha técnica", onBack = onBack)
        when {
            isLoading -> LoadingPane(contentDescription = "Cargando ficha técnica")
            notFound || draft == null -> EmptyState(
                title = "No encontramos esta ficha",
                subtitle = "Puede que la clienta ya no esté en tu agenda.",
                ctaLabel = "Volver",
                onCtaClick = onBack,
                icon = Icons.Outlined.PersonOff,
            )
            else -> {
                val current = draft!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    itemsIndexed(fichaTecnicaFields) { index, field ->
                        val isLast = index == fichaTecnicaFields.lastIndex
                        SuggestedFormField(
                            value = field.getter(current),
                            onValueChange = { viewModel.updateField(field, it) },
                            label = field.label,
                            placeholder = field.placeholder,
                            multiline = field.multiline,
                            keyboardType = field.keyboardType,
                            suggestions = field.suggestions,
                            imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
                            onDone = if (isLast) {
                                { if (!isSaving) viewModel.save(onSaved) }
                            } else {
                                null
                            },
                        )
                    }
                    errorMessage?.let { message ->
                        item { FormErrorText(message) }
                    }
                    item {
                        MiPeluButton(
                            text = if (isSaving) "Guardando..." else "Guardar cambios",
                            onClick = { viewModel.save(onSaved) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving,
                        )
                    }
                }
            }
        }
    }
}
