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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.cocido.mipelu.core.ui.components.FormErrorText
import com.cocido.mipelu.core.ui.components.MiPeluButton
import com.cocido.mipelu.core.ui.components.MiPeluTextField
import com.cocido.mipelu.core.ui.components.TopBarBack
import com.cocido.mipelu.core.ui.components.miPeluCardShadow
import com.cocido.mipelu.core.util.looksLikeEmail

private val AuthCardShape = RoundedCornerShape(24.dp)
private const val MIN_PASSWORD_LENGTH = 8

@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var professionalName by rememberSaveable { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val passwordTooShort = password.isNotEmpty() && password.length < MIN_PASSWORD_LENGTH
    val emailLooksInvalid = email.isNotBlank() && !email.looksLikeEmail()
    val isFormValid =
        nombre.isNotBlank() &&
            apellido.isNotBlank() &&
            email.looksLikeEmail() &&
            password.length >= MIN_PASSWORD_LENGTH &&
            professionalName.isNotBlank()
    val submit = {
        if (!isLoading && isFormValid) {
            viewModel.signUp("$nombre $apellido".trim(), email, password, professionalName, onSignUpSuccess)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBarBack(title = "", onBack = onBack)
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier =
                    Modifier
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
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = "Nombre",
                        placeholder = "Tu nombre",
                        containerColor = FondoApp,
                    )
                    Spacer(Modifier.height(18.dp))
                    MiPeluTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = "Apellido",
                        placeholder = "Tu apellido",
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
                        isError = emailLooksInvalid,
                        supportingText = if (emailLooksInvalid) "Revisá el email: parece incompleto." else null,
                    )
                    Spacer(Modifier.height(18.dp))
                    MiPeluTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        placeholder = "••••••••",
                        isPassword = true,
                        containerColor = FondoApp,
                        supportingText =
                            if (passwordTooShort) {
                                "La contraseña debe tener al menos 8 caracteres."
                            } else {
                                "Mínimo 8 caracteres."
                            },
                        isError = passwordTooShort,
                    )
                    Spacer(Modifier.height(18.dp))
                    MiPeluTextField(
                        value = professionalName,
                        onValueChange = { professionalName = it },
                        label = "Nombre profesional",
                        placeholder = "Ej: Romina Gómez — Estilista",
                        containerColor = FondoApp,
                        imeAction = ImeAction.Done,
                        onDone = submit,
                    )
                    errorMessage?.let {
                        Spacer(Modifier.height(14.dp))
                        FormErrorText(message = it, textAlign = TextAlign.Center)
                    }
                    Spacer(Modifier.height(28.dp))
                    MiPeluButton(
                        text = if (isLoading) "Creando cuenta..." else "Crear cuenta",
                        onClick = submit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && isFormValid,
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
