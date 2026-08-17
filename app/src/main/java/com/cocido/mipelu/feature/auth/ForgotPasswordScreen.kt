package com.cocido.mipelu.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.CiruelaMedio
import com.cocido.mipelu.core.theme.FondoApp
import com.cocido.mipelu.core.theme.NudeClaro
import com.cocido.mipelu.core.theme.Superficie
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluTextField
import com.cocido.mipelu.core.ui.components.TopBarBack
import com.cocido.mipelu.core.ui.components.miPeluCardShadow

private val AuthCardShape = RoundedCornerShape(24.dp)

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var email by remember { mutableStateOf("") }
    val isLoading by viewModel.forgotPasswordLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.forgotPasswordError.collectAsStateWithLifecycle()
    val sent by viewModel.forgotPasswordSent.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarBack(title = "", onBack = onBack)
        // Mismo patrón que Login/SignUp (card centrada con sombra tintada) - esta pantalla vivía
        // suelta a la izquierda sin card, por eso se veía vacía apenas se llegaba al estado "sent".
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth()
                    .miPeluCardShadow(shape = AuthCardShape, elevation = 14.dp),
                shape = AuthCardShape,
                color = Superficie,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RoundIconBadge(icon = if (sent) Icons.Filled.MarkEmailRead else Icons.Outlined.LockReset)
                    Spacer(Modifier.height(20.dp))
                    if (sent) {
                        Text(
                            "Revisá tu email",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Si $email está registrado, te enviamos instrucciones para restablecer tu contraseña.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(32.dp))
                        MiPeluButton(
                            text = "Volver a iniciar sesión",
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        Text(
                            "Olvidé mi contraseña",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Ingresá el email de tu cuenta y te enviamos instrucciones para restablecer tu contraseña.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(32.dp))
                        MiPeluTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            placeholder = "tu@email.com",
                            keyboardType = KeyboardType.Email,
                            containerColor = FondoApp,
                            imeAction = ImeAction.Done,
                            onDone = { if (!isLoading && email.isNotBlank()) viewModel.forgotPassword(email) },
                        )
                        errorMessage?.let {
                            Spacer(Modifier.height(14.dp))
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Spacer(Modifier.height(28.dp))
                        MiPeluButton(
                            text = if (isLoading) "Enviando..." else "Enviar instrucciones",
                            onClick = { viewModel.forgotPassword(email) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading && email.isNotBlank(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundIconBadge(icon: ImageVector) {
    Box(
        modifier = Modifier.size(64.dp).background(NudeClaro, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = CiruelaMedio, modifier = Modifier.size(30.dp))
    }
}
