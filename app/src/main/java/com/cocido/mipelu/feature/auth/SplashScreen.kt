package com.cocido.mipelu.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cocido.mipelu.R
import com.cocido.mipelu.core.theme.CiruelaProfundo
import com.cocido.mipelu.core.theme.TextoSecundarioSobreOscuro
import com.cocido.mipelu.core.theme.TextoSobreOscuro
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateNext: (loggedIn: Boolean) -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        delay(900)
        onNavigateNext(viewModel.currentUser.value != null)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CiruelaProfundo)
            .clickable { onNavigateNext(viewModel.currentUser.value != null) },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(24.dp).widthIn(max = 260.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_mi_pelu_logo),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
            )
            Text(
                text = "Mi Pelu",
                style = MaterialTheme.typography.displaySmall,
                color = TextoSobreOscuro,
            )
            Text(
                text = "Tu memoria profesional, siempre a mano",
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundarioSobreOscuro,
                textAlign = TextAlign.Center,
            )
        }
    }
}
