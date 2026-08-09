package com.cocido.mipelu.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.FondoApp
import com.cocido.mipelu.core.theme.Superficie
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluTextField
import com.cocido.mipelu.core.ui.components.TopBarBack
import com.cocido.mipelu.core.ui.components.miPeluCardShadow

private val AuthCardShape = RoundedCornerShape(24.dp)

@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var professionalName by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarBack(title = "", onBack = onBack)
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
                    Text(
                        "Crear cuenta",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(32.dp))
                    MiPeluTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre",
                        placeholder = "Tu nombre",
                        containerColor = FondoApp,
                    )
                    Spacer(Modifier.height(18.dp))
                    MiPeluTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        placeholder = "tu@email.com",
                        keyboardType = KeyboardType.Email,
                        containerColor = FondoApp,
                    )
                    Spacer(Modifier.height(18.dp))
                    MiPeluTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        placeholder = "••••••••",
                        isPassword = true,
                        containerColor = FondoApp,
                    )
                    Spacer(Modifier.height(18.dp))
                    MiPeluTextField(
                        value = professionalName,
                        onValueChange = { professionalName = it },
                        label = "Nombre profesional",
                        placeholder = "Ej: Romina Gómez — Estilista",
                        containerColor = FondoApp,
                        imeAction = ImeAction.Done,
                        onDone = {
                            if (!isLoading) viewModel.signUp(name, email, password, professionalName, onSignUpSuccess)
                        },
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
                        text = if (isLoading) "Creando cuenta..." else "Crear cuenta",
                        onClick = {
                            viewModel.signUp(name, email, password, professionalName, onSignUpSuccess)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && name.isNotBlank() && email.isNotBlank() &&
                            password.isNotBlank() && professionalName.isNotBlank(),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Tu información se guarda de forma segura en la nube.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
