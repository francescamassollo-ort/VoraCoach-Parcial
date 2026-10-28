package com.example.voracoach.ui.screens.profile

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.CoachingStyle
import com.example.voracoach.model.ResponseStyle
import com.example.voracoach.model.VoiceSettings
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.VoiceSettingsViewModel

// Pantalla "Profile / 05 — Voice & AI Settings".
// El estado vive en VoiceSettingsRepository (a través del ViewModel): la pantalla recibe el
// VoiceSettings completo y devuelve una copia modificada con onSettingsChange.
@Composable
fun VoiceSettingsRoute(
    onBack: () -> Unit,
    onPrivacyClick: () -> Unit,
    viewModel: VoiceSettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    VoiceSettingsScreen(
        settings = settings,
        onSettingsChange = viewModel::onSettingsChange,
        onBack = onBack,
        onPrivacyClick = onPrivacyClick
    )
}

@Composable
fun VoiceSettingsScreen(
    settings: VoiceSettings,
    onSettingsChange: (VoiceSettings) -> Unit,
    onBack: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Scaffold(
        topBar = {
            ScreenHeader(
                title = stringResource(R.string.screen_coach),
                onBackClick = onBack,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.voice_settings_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SectionLabel(R.string.voice_settings_coaching_style)
            CoachingStyleSelector(
                selected = settings.coachingStyle,
                onSelect = { onSettingsChange(settings.copy(coachingStyle = it)) }
            )

            ActiveVoiceCard()

            SectionLabel(R.string.voice_settings_speaking)
            AppCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 12.dp, top = 6.dp, end = 12.dp, bottom = 10.dp)
            ) {
                SwitchRow(R.string.voice_settings_speak_aloud, settings.speakResponses) {
                    onSettingsChange(settings.copy(speakResponses = it))
                }
                SwitchRow(R.string.voice_settings_auto_play, settings.autoPlayCoaching) {
                    onSettingsChange(settings.copy(autoPlayCoaching = it))
                }
                SwitchRow(R.string.voice_settings_interruptions, settings.allowInterruptions) {
                    onSettingsChange(settings.copy(allowInterruptions = it))
                }
                Text(
                    text = stringResource(R.string.voice_settings_interruptions_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            SectionLabel(R.string.voice_settings_response_style)
            ResponseOption(R.string.voice_settings_concise, R.string.voice_settings_concise_desc,
                settings.responseStyle == ResponseStyle.CONCISE
            ) { onSettingsChange(settings.copy(responseStyle = ResponseStyle.CONCISE)) }
            ResponseOption(R.string.voice_settings_balanced, R.string.voice_settings_balanced_desc,
                settings.responseStyle == ResponseStyle.BALANCED
            ) { onSettingsChange(settings.copy(responseStyle = ResponseStyle.BALANCED)) }
            ResponseOption(R.string.voice_settings_detailed, R.string.voice_settings_detailed_desc,
                settings.responseStyle == ResponseStyle.DETAILED
            ) { onSettingsChange(settings.copy(responseStyle = ResponseStyle.DETAILED)) }

            AppCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp)
            ) {
                SwitchRow(R.string.voice_settings_voice_feedback, settings.voiceFeedback) {
                    onSettingsChange(settings.copy(voiceFeedback = it))
                }
                SwitchRow(R.string.voice_settings_haptic_feedback, settings.hapticFeedback) {
                    onSettingsChange(settings.copy(hapticFeedback = it))
                }
            }

            ControlCard(onPrivacyClick)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(@StringRes text: Int) {
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

// Tres botones iguales; el elegido va en verde oscuro. selectable con Role.RadioButton
// para que TalkBack los lea como opciones de un mismo grupo.
@Composable
private fun CoachingStyleSelector(selected: CoachingStyle, onSelect: (CoachingStyle) -> Unit) {
    val options = listOf(
        CoachingStyle.SUPPORTIVE to R.string.voice_settings_supportive,
        CoachingStyle.BALANCED to R.string.voice_settings_balanced,
        CoachingStyle.DIRECT to R.string.voice_settings_direct
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (style, label) ->
            val isSelected = style == selected
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(style) }),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 9.dp)
                )
            }
        }
    }
}

// La voz es fija (VORA Voice 01) y "Preview" es demostración: la app no reproduce audio real.
@Composable
private fun ActiveVoiceCard() {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.voice_settings_active_voice),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.VolumeUp, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.voice_settings_voice_name),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.voice_settings_preview), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// Fila con interruptor. toggleable en toda la fila (Role.Switch) y el Switch sin onClick propio,
// así el toque funciona en toda la fila y el lector de pantalla la anuncia como un solo control.
@Composable
private fun SwitchRow(@StringRes label: Int, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(label), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        // El Switch de Material mide 52x32 dp; en Figma es más chico (~42x26). scale(0.8f) lo
        // achica visualmente y offset compensa el corrimiento hacia adentro para que quede alineado a la derecha.
        Switch(
            checked = checked,
            onCheckedChange = null,
            modifier = Modifier.offset(x = 5.dp).scale(0.8f),
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.surface,
                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                checkedBorderColor = MaterialTheme.colorScheme.secondary,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

// Opción de estilo de respuesta: la elegida lleva fondo y borde menta y un check verde oscuro.
@Composable
private fun ResponseOption(
    @StringRes title: Int,
    @StringRes description: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(title), style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        CircleShape
                    )
                    .then(
                        if (selected) Modifier else Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(Icons.Filled.Check, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
private fun ControlCard(onPrivacyClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.voice_settings_control_title), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.voice_settings_control_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.voice_settings_privacy_link),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onPrivacyClick)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun VoiceSettingsPreview() {
    VoraCoachTheme {
        VoiceSettingsScreen(
            settings = VoiceSettings(),
            onSettingsChange = {},
            onBack = {},
            onPrivacyClick = {}
        )
    }
}