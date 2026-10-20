package com.example.voracoach.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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

private const val MIN_PASSWORD_LENGTH = 8

// Pantalla de registro (Sign Up)
@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onSignUp: () -> Unit,
    onGoToSignIn: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoOnlyMessage = stringResource(R.string.auth_demo_only)
    val showDemoOnly: () -> Unit = { scope.launch { snackbarHostState.showSnackbar(demoOnlyMessage) } }

    val requiredError = stringResource(R.string.auth_error_required)
    val shortPasswordError = stringResource(R.string.signup_error_password_short)
    // La regla "8+ caracteres" se marca como cumplida en cuanto la contraseña la alcanza
    val passwordLongEnough = password.length >= MIN_PASSWORD_LENGTH

    val passwordError = when {
        !showErrors -> null
        password.isBlank() -> requiredError
        !passwordLongEnough -> shortPasswordError
        else -> null
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthTopBar(onBack = onBack, onLanguageClick = showDemoOnly)
            Spacer(Modifier.height(8.dp))
            // En Sign Up el encabezado no lleva descripción, solo marca y lema
            AuthHeader()
            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(24.dp)) {
                    AuthTextField(
                        label = stringResource(R.string.signup_name_label),
                        value = name,
                        onValueChange = { name = it },
                        placeholder = stringResource(R.string.signup_name_hint),
                        leadingIcon = Icons.Filled.PersonOutline,
                        keyboardType = KeyboardType.Text,
                        errorMessage = if (showErrors && name.isBlank()) requiredError else null
                    )
                    Spacer(Modifier.height(16.dp))
                    AuthTextField(
                        label = stringResource(R.string.auth_email_label),
                        value = email,
                        onValueChange = { email = it },
                        placeholder = stringResource(R.string.auth_email_hint),
                        leadingIcon = Icons.Filled.MailOutline,
                        keyboardType = KeyboardType.Email,
                        errorMessage = if (showErrors && email.isBlank()) requiredError else null
                    )
                    Spacer(Modifier.height(16.dp))
                    AuthTextField(
                        label = stringResource(R.string.auth_password_label),
                        value = password,
                        onValueChange = { password = it },
                        placeholder = stringResource(R.string.signup_password_hint),
                        leadingIcon = Icons.Filled.Lock,
                        keyboardType = KeyboardType.Password,
                        errorMessage = passwordError,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePasswordVisible = { passwordVisible = !passwordVisible }
                    )

                    // Indicador de la regla de contraseña y enlace de ayuda
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (passwordLongEnough) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(16.dp)
                            )
                            Text(
                                text = stringResource(R.string.signup_password_rule),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        TextButton(onClick = showDemoOnly) {
                            Text(
                                text = stringResource(R.string.signup_password_help),
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            showErrors = true
                            if (name.isNotBlank() && email.isNotBlank() && passwordLongEnough) onSignUp()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(stringResource(R.string.signup_button), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.padding(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                    Spacer(Modifier.height(20.dp))

                    SocialSignInRow(
                        dividerText = stringResource(R.string.signup_continue_with),
                        onSocialClick = showDemoOnly
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Shield,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(16.dp)
                )
                Text(stringResource(R.string.signup_biometric), style = MaterialTheme.typography.bodyMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.signup_account_issues))
                TextButton(onClick = showDemoOnly) {
                    Text(
                        text = stringResource(R.string.signup_recover),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.signup_have_account),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onGoToSignIn) {
                    Text(
                        text = stringResource(R.string.signup_go_signin),
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun SignUpScreenPreview() {
    MaterialTheme {
        SignUpScreen(onBack = {}, onSignUp = {}, onGoToSignIn = {})
    }
}
