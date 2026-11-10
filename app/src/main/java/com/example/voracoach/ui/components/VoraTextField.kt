package com.example.voracoach.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.ui.theme.TextMuted
import com.example.voracoach.ui.theme.VoraCoachTheme // ← cambialo si tu tema se llama distinto

/**
 * Campo de texto con la etiqueta arriba, para Login y Sign Up.
 * Si [isPassword] es true, oculta el texto y muestra el ojito para verlo.
 *
 * Todo el aspecto sale del tema (Color.kt, Type.kt y Theme.kt), así que no hay colores ni
 * tamaños de texto escritos a mano acá:
 * - Etiqueta: `labelMedium` (Manrope 14 sp SemiBold) en `onSurface`.
 * - Texto escrito: `bodyLarge` (14 sp) en `onSurface`. Placeholder: `bodyLarge` en `TextMuted`.
 * - Borde: `outline` (gris `#E4E9E6`); al enfocar pasa a `primary` (verde oscuro); con error, a `error`.
 * - Fondo del campo: `background` (crema `#F7F8F5`), para que se distinga sobre la tarjeta blanca.
 * - Íconos: `onSurfaceVariant` (gris `#68736F`); el de la izquierda pasa a `primary` al enfocar.
 * - Esquinas de 16 dp, como en tu versión original.
 *
 * Medidas de Figma que ya están contempladas: la etiqueta está 4 dp hacia adentro y hay 6 dp
 * entre la etiqueta y el campo.
 *
 * ⚠ En Figma no pude leer el color de fondo ni el radio exacto del campo (el límite de llamadas
 * del plan Starter me cortó), y el campo de Material mide 56 dp de alto contra los 52–55 dp del
 * diseño. Revisalo contra el mockup.
 *
 * Los textos del ojito usan `R.string.auth_show_password` y `R.string.auth_hide_password`:
 * tienen que existir en tu `strings.xml`.
 *
 * @param labelAction contenido opcional a la derecha de la etiqueta, por ejemplo el enlace
 * "Forgot your password?".
 */
@Composable
fun VoraTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisible: () -> Unit = {},
    labelAction: (@Composable () -> Unit)? = null,
) {
    val scheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp), // la etiqueta de Figma está 4 dp hacia adentro
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = scheme.onSurface,
            )
            // Espacio opcional a la derecha de la etiqueta, por ejemplo "Forgot your password?"
            labelAction?.invoke()
        }

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(text = placeholder, style = MaterialTheme.typography.bodyLarge)
            },
            leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onTogglePasswordVisible) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = stringResource(
                                if (passwordVisible) R.string.auth_hide_password else R.string.auth_show_password,
                            ),
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = errorMessage != null,
            supportingText = errorMessage?.let {
                { Text(text = it, style = MaterialTheme.typography.bodySmall) }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                // Texto
                focusedTextColor = scheme.onSurface,
                unfocusedTextColor = scheme.onSurface,
                errorTextColor = scheme.onSurface,
                cursorColor = scheme.primary,
                // Placeholder
                focusedPlaceholderColor = TextMuted,
                unfocusedPlaceholderColor = TextMuted,
                errorPlaceholderColor = TextMuted,
                // Fondo
                focusedContainerColor = scheme.background,
                unfocusedContainerColor = scheme.background,
                errorContainerColor = scheme.background,
                // Borde
                focusedBorderColor = scheme.primary,
                unfocusedBorderColor = scheme.outline,
                errorBorderColor = scheme.error,
                // Íconos
                focusedLeadingIconColor = scheme.primary,
                unfocusedLeadingIconColor = scheme.onSurfaceVariant,
                errorLeadingIconColor = scheme.error,
                focusedTrailingIconColor = scheme.onSurfaceVariant,
                unfocusedTrailingIconColor = scheme.onSurfaceVariant,
                errorTrailingIconColor = scheme.error,
                // Mensaje de apoyo / error
                errorSupportingTextColor = scheme.error,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, name = "Campos de login")
@Composable
private fun VoraTextFieldPreview() {
    VoraCoachTheme {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VoraTextField(
                label = "Email Address",
                value = email,
                onValueChange = { email = it },
                placeholder = "alex.morgan@email.com",
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
            )
            VoraTextField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                placeholder = "At least 8 characters",
                leadingIcon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePasswordVisible = { passwordVisible = !passwordVisible },
                labelAction = {
                    Text(
                        text = "Forgot your password?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, name = "Campo con error")
@Composable
private fun VoraTextFieldErrorPreview() {
    VoraCoachTheme {
        VoraTextField(
            label = "Email Address",
            value = "alex.morgan",
            onValueChange = {},
            placeholder = "alex.morgan@email.com",
            leadingIcon = Icons.Outlined.Email,
            keyboardType = KeyboardType.Email,
            errorMessage = "Ingresá un correo válido",
            modifier = Modifier.padding(24.dp),
        )
    }
}
/*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.voracoach.R

// Campo de texto con la etiqueta arriba
// Si isPassword es true, oculta el texto y muestra el ojito para verlo.
@Composable
fun VoraTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePasswordVisible: () -> Unit = {},
    labelAction: (@Composable () -> Unit)? = null
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            // Espacio opcional a la derecha de la etiqueta, por ejemplo "Forgot your password?"
            labelAction?.invoke()
        }
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = onTogglePasswordVisible) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = stringResource(
                                if (passwordVisible) R.string.auth_hide_password else R.string.auth_show_password
                            )
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = errorMessage != null,
            supportingText = errorMessage?.let { { Text(it) } },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
*/