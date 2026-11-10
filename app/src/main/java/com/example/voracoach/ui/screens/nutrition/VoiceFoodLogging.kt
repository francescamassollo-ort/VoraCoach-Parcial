package com.example.voracoach.ui.screens.nutrition

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.ui.theme.DarkGreen
import com.example.voracoach.ui.theme.Mint
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.ui.theme.White60

// "04 — Voice Food Logging" según Figma. La escucha es simulada: la app no pide permiso de
// micrófono (NAV05), muestra una transcripción de ejemplo y "Done Speaking" pasa al análisis.
@Composable
fun VoiceFoodLogging(
    onNavigateBack: () -> Unit = {},
    onNavigateToDoneSpeaking: () -> Unit = {},
    onTypeInstead: () -> Unit = {},
) {
    // El orbe "late" suavemente mientras escucha
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    NutritionDarkLayout(
        title = stringResource(R.string.voice_title),
        onClose = onNavigateBack,
        showLiveDot = true,
    ) {
        Spacer(Modifier.height(24.dp))
        ListeningOrb(scale = pulseScale)
        Spacer(Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.voice_listening),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Mint,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.voice_keep_talking),
            style = MaterialTheme.typography.bodyMedium,
            color = White60,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        DarkInfoCard {
            Text(
                text = stringResource(R.string.voice_transcript_example),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onNavigateToDoneSpeaking,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = DarkGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(
                text = stringResource(R.string.voice_done),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            )
        }
        Spacer(Modifier.height(8.dp))
        // "Type instead": carga la comida a mano (Review Meal vacía)
        TextButton(onClick = onTypeInstead) {
            Text(
                text = stringResource(R.string.voice_type_instead),
                style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                color = White60,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun VoiceFoodLoggingPreview() {
    VoraCoachTheme {
        VoiceFoodLogging()
    }
}
