package com.example.voracoach.ui.screens.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppChip
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.SecondaryButtonStyle
import com.example.voracoach.ui.components.StatusBadge
import com.example.voracoach.ui.components.StatusBadgeStyle
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.CoachSuggestion

// Textos de las preguntas sugeridas. El ViewModel recibe el enum (para elegir la respuesta)
// y el texto ya traducido (para mostrarlo en la conversación).
private fun CoachSuggestion.labelRes(): Int = when (this) {
    CoachSuggestion.START_WORKOUT -> R.string.coach_suggestion_start_workout
    CoachSuggestion.LOG_BREAKFAST -> R.string.coach_suggestion_log_breakfast
    CoachSuggestion.EAT_TONIGHT -> R.string.coach_suggestion_eat_tonight
    CoachSuggestion.HOW_AM_I_DOING -> R.string.coach_suggestion_how_am_i_doing
}

// "Voice / 01 — Idle": listo para conversar
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CoachIdle(lastUserMessage: String?, actions: CoachActions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ScreenHeader(
            title = stringResource(R.string.coach_title),
            onBackClick = actions.onBack,
            actions = {
                StatusBadge(
                    text = stringResource(R.string.coach_online),
                    style = StatusBadgeStyle.Mint,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            VoraOrb(diameter = 140.dp) {
                Icon(Icons.Outlined.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.coach_idle_title), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.coach_idle_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.coach_suggested_questions),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CoachSuggestion.entries.forEach { suggestion ->
                    val text = stringResource(suggestion.labelRes())
                    AppChip(text = text, onClick = { actions.onSuggestionClick(suggestion, text) })
                }
            }
            Spacer(Modifier.height(20.dp))

            // Última pregunta hecha en esta sesión; si todavía no hay, una de ejemplo
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row {
                    Text(
                        text = stringResource(R.string.coach_last_conversation),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    if (lastUserMessage == null) {
                        Text(
                            text = stringResource(R.string.coach_last_conversation_time),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(
                        R.string.coach_quote,
                        lastUserMessage ?: stringResource(R.string.coach_last_conversation_demo)
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        // Micrófono grande: inicia la escucha simulada (no pide permiso de micrófono, NAV05)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = actions.onTapToTalk,
                modifier = Modifier
                    .size(72.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    Icons.Outlined.Mic,
                    contentDescription = stringResource(R.string.coach_tap_to_talk),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.coach_tap_to_talk), style = MaterialTheme.typography.labelLarge)
            TypeInsteadButton(actions.onTypeInstead)
        }
    }
}

// "Voice / 06 — Error": no se entendió lo que se dijo
@Composable
internal fun CoachError(showPermissionCard: Boolean, actions: CoachActions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ScreenHeader(title = stringResource(R.string.coach_error_header), onBackClick = actions.onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            // Orbe apagado (gris) con el ícono de reintentar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
            ) {
                Icon(
                    Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.coach_error_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.coach_error_body),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            if (showPermissionCard) {
                Spacer(Modifier.height(24.dp))
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                        ) {
                            Icon(Icons.Outlined.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.coach_mic_access_title), style = MaterialTheme.typography.titleSmall)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.coach_mic_access_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // No se pide el permiso de verdad (NAV05): se informa que es demostración
                        SecondaryButton(
                            text = stringResource(R.string.coach_allow_access),
                            onClick = actions.onDemoOnly,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(R.string.coach_not_now),
                            onClick = actions.onDismissPermissionCard,
                            style = SecondaryButtonStyle.Outlined,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = actions.onTryAgain,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.coach_try_again), style = MaterialTheme.typography.titleSmall)
            }
            TypeInsteadButton(actions.onTypeInstead)
        }
    }
}

@Composable
private fun TypeInsteadButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = stringResource(R.string.coach_type_instead),
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun CoachIdlePreview() {
    VoraCoachTheme { CoachIdle(lastUserMessage = null, actions = CoachActions()) }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun CoachErrorPreview() {
    VoraCoachTheme { CoachError(showPermissionCard = true, actions = CoachActions()) }
}
