package com.example.voracoach.ui.screens.coach

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.StatusBadge
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.ui.theme.White12
import com.example.voracoach.ui.theme.White60

// "Voice / 02 — Listening", la transcripción aparece de a poco (simulada)
@Composable
internal fun CoachListening(transcript: String, actions: CoachActions) {
    DarkCoachLayout(
        title = stringResource(R.string.coach_listening_header),
        onBack = actions.onBack,
        headerAction = {
            // Punto menta + "LIVE AUDIO"
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    stringResource(R.string.coach_live_audio),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        },
        bottom = {
            // El mic termina la escucha: si la frase no terminó, se simula que no se entendió (Error)
            IconButton(
                onClick = actions.onStopListening,
                modifier = Modifier
                    .size(72.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
            ) {
                Icon(
                    Icons.Outlined.Mic,
                    contentDescription = stringResource(R.string.coach_stop_listening),
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
            CancelButton(actions.onCancel)
        }
    ) {
        CoachOrb()
        Spacer(Modifier.height(28.dp))
        DarkCard {
            Text(
                stringResource(R.string.coach_live_transcript),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.coach_quote, "$transcript…"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.coach_listening_hint), style = MaterialTheme.typography.bodySmall, color = White60)
    }
}

// "Voice / 03 — Processing": la respuesta se está "pensando"
@Composable
internal fun CoachProcessing(userText: String, actions: CoachActions) {
    DarkCoachLayout(
        title = stringResource(R.string.coach_processing_header),
        onBack = actions.onBack,
        bottom = { CancelButton(actions.onCancel) }
    ) {
        CoachOrb()
        Spacer(Modifier.height(24.dp))
        Text(
            stringResource(R.string.coach_thinking),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.height(14.dp))
        DarkCard {
            Text(
                text = stringResource(R.string.coach_quote, userText),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.coach_processing_hint), style = MaterialTheme.typography.bodySmall, color = White60)
        }
    }
}

// "Voice / 04 — Speaking": VORA "lee" la respuesta, con progreso y controles de reproducción simulados
@Composable
internal fun CoachSpeaking(
    reply: String,
    elapsedSeconds: Int,
    totalSeconds: Int,
    isPaused: Boolean,
    actions: CoachActions
) {
    DarkCoachLayout(
        title = stringResource(R.string.coach_speaking_header),
        onBack = actions.onBack,
        headerAction = {
            StatusBadge(text = stringResource(R.string.coach_speaking_badge), modifier = Modifier.padding(end = 16.dp))
        },
        bottom = {
            Button(
                onClick = actions.onInterrupt,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.coach_tap_to_interrupt), style = MaterialTheme.typography.titleSmall)
            }
        }
    ) {
        CoachOrb()
        Spacer(Modifier.height(28.dp))
        DarkCard {
            Text(
                text = stringResource(R.string.coach_quote, reply),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
        Spacer(Modifier.height(24.dp))

        LinearProgressIndicator(
            progress = { if (totalSeconds > 0) elapsedSeconds.toFloat() / totalSeconds else 0f },
            color = MaterialTheme.colorScheme.secondary,
            trackColor = White12,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        val remaining = (totalSeconds - elapsedSeconds).coerceAtLeast(0)
        Row(Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.coach_time_elapsed, elapsedSeconds / 60, elapsedSeconds % 60),
                style = MaterialTheme.typography.bodySmall,
                color = White60,
                modifier = Modifier.weight(1f)
            )
            Text(
                stringResource(R.string.coach_time_remaining, remaining / 60, remaining % 60),
                style = MaterialTheme.typography.bodySmall,
                color = White60
            )
        }
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            RoundControl(onClick = actions.onReplay) {
                Icon(Icons.Outlined.Repeat, contentDescription = stringResource(R.string.coach_replay), tint = MaterialTheme.colorScheme.inverseOnSurface)
            }
            IconButton(
                onClick = actions.onTogglePause,
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = stringResource(if (isPaused) R.string.coach_resume else R.string.coach_pause),
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
            // Detener tiene el mismo efecto que interrumpir: pasa a la conversación
            RoundControl(onClick = actions.onInterrupt) {
                Icon(Icons.Outlined.StopCircle, contentDescription = stringResource(R.string.coach_stop), tint = MaterialTheme.colorScheme.inverseOnSurface)
            }
        }
    }
}

// Estructura común de los estados oscuros: encabezado arriba, contenido centrado con scroll
// (por si no entra en pantallas chicas, UI03) y acciones fijas abajo
@Composable
private fun DarkCoachLayout(
    title: String,
    onBack: () -> Unit,
    headerAction: @Composable RowScope.() -> Unit = {},
    bottom: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseSurface)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ScreenHeader(
            title = title,
            onBackClick = onBack,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            actions = headerAction
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            content()
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            content = bottom
        )
    }
}

@Composable
private fun CoachOrb() {
    VoraOrb(diameter = 170.dp) {
        Icon(Icons.Outlined.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.inverseOnSurface)
    }
}

@Composable
private fun DarkCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = White12,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, White12),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp), content = content)
    }
}

@Composable
private fun RoundControl(onClick: () -> Unit, content: @Composable () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .background(White12, CircleShape)
    ) { content() }
}

@Composable
private fun CancelButton(onCancel: () -> Unit) {
    TextButton(onClick = onCancel) {
        Text(stringResource(R.string.coach_cancel), color = MaterialTheme.colorScheme.secondary)
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun CoachListeningPreview() {
    VoraCoachTheme { CoachListening(transcript = "I had chicken, rice and some", actions = CoachActions()) }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun CoachSpeakingPreview() {
    VoraCoachTheme {
        CoachSpeaking(
            reply = "That sounds like a balanced lunch.",
            elapsedSeconds = 8,
            totalSeconds = 22,
            isPaused = false,
            actions = CoachActions()
        )
    }
}
