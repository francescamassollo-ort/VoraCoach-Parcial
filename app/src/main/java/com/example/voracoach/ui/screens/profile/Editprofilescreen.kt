package com.example.voracoach.ui.screens.profile


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.Gender
import com.example.voracoach.ui.components.InsightBanner
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.EditProfileUiState
import com.example.voracoach.viewmodel.EditProfileViewModel

// Pantalla "Profile / 02 — Edit Profile". Sub-pantalla: no muestra la barra inferior.
@Composable
fun EditProfileRoute(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    EditProfileScreen(
        uiState = uiState,
        onBack = onBack,
        onChangePhotoClick = { /* demo: la app no sube fotos */ },
        onFirstNameChange = viewModel::onFirstNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onBirthDateChange = viewModel::onBirthDateChange,
        onHeightChange = viewModel::onHeightChange,
        onWeightChange = viewModel::onWeightChange,
        onGenderSelected = viewModel::onGenderSelected,
        // Guarda y vuelve al Dashboard, que ya muestra el nombre nuevo
        onSave = {
            viewModel.onSave()
            onBack()
        }
    )
}

@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    onBack: () -> Unit,
    onChangePhotoClick: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onGenderSelected: (Gender) -> Unit,
    onSave: () -> Unit
) {
    // imePadding: el teclado empuja el botón hacia arriba en vez de taparlo
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .imePadding()
    ) {
        // ScreenHeader ya trae su margen horizontal: no se le suma otro
        ScreenHeader(title = stringResource(R.string.edit_profile_title), onBackClick = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PhotoSection(onChangePhotoClick)

            LabeledField(stringResource(R.string.edit_profile_first_name)) {
                ProfileTextField(uiState.firstName, onFirstNameChange)
            }
            LabeledField(stringResource(R.string.edit_profile_last_name)) {
                ProfileTextField(uiState.lastName, onLastNameChange)
            }
            LabeledField(stringResource(R.string.edit_profile_birth_date)) {
                ProfileTextField(uiState.birthDate, onBirthDateChange)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LabeledField(stringResource(R.string.edit_profile_height), Modifier.weight(1f)) {
                    ProfileTextField(
                        value = uiState.heightCm,
                        onValueChange = onHeightChange,
                        keyboardType = KeyboardType.Number,
                        suffix = stringResource(R.string.unit_cm)
                    )
                }
                LabeledField(stringResource(R.string.edit_profile_weight), Modifier.weight(1f)) {
                    ProfileTextField(
                        value = uiState.weightKg,
                        onValueChange = onWeightChange,
                        keyboardType = KeyboardType.Number,
                        suffix = stringResource(R.string.unit_kg)
                    )
                }
            }
            LabeledField(stringResource(R.string.edit_profile_gender)) {
                GenderField(uiState.gender, onGenderSelected)
            }
            InsightBanner(message = stringResource(R.string.edit_profile_info))
        }

        // Fuera del scroll: el botón siempre queda visible abajo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SecondaryButton(
                text = stringResource(R.string.edit_profile_save),
                onClick = onSave,
                enabled = uiState.canSave,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.edit_profile_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PhotoSection(onChangePhotoClick: () -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = stringResource(R.string.profile_avatar_description),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        TextButton(onClick = onChangePhotoClick) {
            Text(
                text = stringResource(R.string.edit_profile_change_photo),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Etiqueta en mayúsculas arriba del campo, como en Figma
@Composable
private fun LabeledField(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 2.dp)
        )
        Spacer(Modifier.height(6.dp))
        content()
    }
}

// No reutiliza VoraTextField: ese lleva ícono obligatorio y etiqueta propia con otro estilo,
// y acá el campo es blanco, sin ícono y con la etiqueta en mayúsculas.
@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    suffix: String? = null,
    readOnly: Boolean = false,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val scheme = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        readOnly = readOnly,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        suffix = suffix?.let {
            { Text(it, style = MaterialTheme.typography.bodyLarge, color = scheme.onSurfaceVariant) }
        },
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = scheme.onSurface,
            unfocusedTextColor = scheme.onSurface,
            cursorColor = scheme.primary,
            focusedContainerColor = scheme.surface,
            unfocusedContainerColor = scheme.surface,
            focusedBorderColor = scheme.primary,
            unfocusedBorderColor = scheme.outline,
            focusedTrailingIconColor = scheme.onSurfaceVariant,
            unfocusedTrailingIconColor = scheme.onSurfaceVariant
        )
    )
}

// Campo de solo lectura que abre un menú con las opciones. La capa transparente de arriba
// recibe el toque, porque un campo readOnly no es clickeable por sí solo.
@Composable
private fun GenderField(selected: Gender, onSelected: (Gender) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        ProfileTextField(
            value = genderLabel(selected),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null) }
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Gender.entries.forEach { gender ->
                DropdownMenuItem(
                    text = { Text(genderLabel(gender)) },
                    onClick = {
                        onSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}

// El enum no lleva texto: la pantalla elige el string (strings.xml)
@Composable
private fun genderLabel(gender: Gender): String = stringResource(
    when (gender) {
        Gender.FEMALE -> R.string.gender_female
        Gender.MALE -> R.string.gender_male
        Gender.NON_BINARY -> R.string.gender_non_binary
        Gender.PREFER_NOT_TO_SAY -> R.string.gender_prefer_not_to_say
    }
)

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun EditProfilePreview() {
    VoraCoachTheme {
        EditProfileScreen(
            uiState = EditProfileUiState(
                firstName = "Alex",
                lastName = "Morgan",
                birthDate = "14 March 1995",
                heightCm = "178",
                weightKg = "76",
                gender = Gender.PREFER_NOT_TO_SAY
            ),
            onBack = {}, onChangePhotoClick = {}, onFirstNameChange = {}, onLastNameChange = {},
            onBirthDateChange = {}, onHeightChange = {}, onWeightChange = {},
            onGenderSelected = {}, onSave = {}
        )
    }
}