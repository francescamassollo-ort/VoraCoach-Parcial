package com.example.voracoach.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import kotlinx.coroutines.launch
import com.example.voracoach.ui.components.VoraTextField

// Pantalla "Login - 01 — SignIn" (sin firebase por ahora )

@Composable
fun SignInScreen(
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    onGoToSignUp: () -> Unit
) {
    // rememberSaveable para que lo escrito sobreviva a la rotación de pantalla
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    // Los errores solo se muestran después de tocar "Sign In", no mientras el usuario escribe
    var showErrors by rememberSaveable { mutableStateOf(false) }

    // El Snackbar avisa cuando se toca algo que solo funcionaría con Firebase
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoOnlyMessage = stringResource(R.string.auth_demo_only)
    val showDemoOnly: () -> Unit = { scope.launch { snackbarHostState.showSnackbar(demoOnlyMessage) } }

    val requiredError = stringResource(R.string.auth_error_required)

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())// verticalScroll: en pantallas chicas o con el teclado abierto el contenido se desplaza
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthTopBar(onBack = onBack, onLanguageClick = showDemoOnly)
            Spacer(Modifier.height(16.dp))
            AuthHeader(description = stringResource(R.string.signin_description))
            Spacer(Modifier.height(24.dp))

            // Tarjeta blanca para agrupar el form
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    VoraTextField(
                        label = stringResource(R.string.auth_email_label),
                        value = email,
                        onValueChange = { email = it },
                        placeholder = stringResource(R.string.auth_email_hint),
                        leadingIcon = Icons.Filled.MailOutline,
                        keyboardType = KeyboardType.Email,
                        errorMessage = if (showErrors && email.isBlank()) requiredError else null
                    )
                    Spacer(Modifier.height(16.dp))
                    VoraTextField(
                        label = stringResource(R.string.auth_password_label),
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "",
                        leadingIcon = Icons.Filled.Lock,
                        keyboardType = KeyboardType.Password,
                        errorMessage = if (showErrors && password.isBlank()) requiredError else null,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePasswordVisible = { passwordVisible = !passwordVisible },
                        labelAction = {
                            // No hay pantalla de recuperación x ahora
                            TextButton(onClick = showDemoOnly) {
                                Text(stringResource(R.string.signin_forgot_password))
                            }
                        }
                    )
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showErrors = true
                            if (email.isNotBlank() && password.isNotBlank()) onSignIn()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(stringResource(R.string.signin_button), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.padding(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                    Spacer(Modifier.height(20.dp))

                    SocialSignInRow(
                        dividerText = stringResource(R.string.signin_continue_with),
                        onSocialClick = showDemoOnly
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = stringResource(R.string.signin_encrypted),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.signin_no_account),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onGoToSignUp) {
                    Text(
                        text = stringResource(R.string.signin_go_signup),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun SignInScreenPreview() {
    MaterialTheme {
        SignInScreen(onBack = {}, onSignIn = {}, onGoToSignUp = {})
    }
}
