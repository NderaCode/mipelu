package com.cocido.mipelu.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.cocido.mipelu.domain.repository.SessionState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/** Floor, not a deadline: the routing decision waits for the session to actually resolve, this
 * only stops a fast restore from making the brand screen flash by. */
private const val MIN_BRAND_DISPLAY_MS = 900L

@Composable
fun SplashScreen(
    onNavigateNext: (loggedIn: Boolean) -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        // Restoring a session means validating the stored token against the backend, which on a
        // slow connection takes longer than any fixed delay we could pick. Waiting for the state
        // to settle - rather than sleeping and then sampling it - is what stops a returning user
        // from being bounced to onboarding just because the network was slow.
        val settled = async { viewModel.sessionState.first { it != SessionState.Restoring } }
        delay(MIN_BRAND_DISPLAY_MS)
        onNavigateNext(settled.await() == SessionState.Authenticated)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CiruelaProfundo),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 260.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_mi_pelu_logo),
                contentDescription = "Logo de Mi Pelu",
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
