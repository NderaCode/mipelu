package com.cocido.mipelu.feature.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluTextField
import com.cocido.mipelu.core.ui.components.TopBarBack

@Composable
fun ClientFichaScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ClientFichaViewModel = hiltViewModel(),
) {
    val draft by viewModel.draft.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarBack(title = "Ficha técnica", onBack = onBack)
        val current = draft ?: return@Column
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp, 20.dp, 20.dp, 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(fichaTecnicaFields) { field ->
                MiPeluTextField(
                    value = field.getter(current),
                    onValueChange = { viewModel.updateField(field, it) },
                    label = field.label,
                    multiline = field.multiline,
                )
            }
            item {
                MiPeluButton(
                    text = "Guardar cambios",
                    onClick = { viewModel.save(onSaved) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
