package com.example.voracoach.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voracoach.ui.theme.MintTintBorder
import com.example.voracoach.ui.theme.VoraCoachTheme // ← cambialo si tu tema se llama distinto

/**
 * Banner de mensajes de VORA: fondo menta al 12 %, borde menta al 25 %, esquinas de 12 dp,
 * padding de 12 dp y 10 dp entre ícono y texto (valores de "You're on track…" en Figma).
 *
 * Tiene dos formas:
 * - Sin [title]: ícono y mensaje en una fila (aviso de la pantalla Home).
 * - Con [title]: título en mayúsculas arriba, con el ícono u orbe al lado, y el mensaje debajo
 *   (nota de VORA Coach). ⚠ Esta segunda forma no la pude leer en Figma; la armé a partir
 *   de la captura, así que revisá tamaños y espaciados.
 *
 * @param leading ícono u orbe a la izquierda. Por defecto, el ícono de destellos de 16 dp.
 * Pasá `null` para no mostrar nada, o por ejemplo `{ VoraOrb(diameter = 16.dp) }`.
 */
@Composable
fun InsightBanner(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    leading: (@Composable () -> Unit)? = { SparklesIcon() },
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        border = BorderStroke(1.dp, MintTintBorder),
    ) {
        val messageStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)

        if (title == null) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leading?.invoke()
                Text(text = message, style = messageStyle, modifier = Modifier.weight(1f))
            }
        } else {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    leading?.invoke()
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
                Text(text = message, style = messageStyle)
            }
        }
    }
}

@Composable
private fun SparklesIcon() {
    Icon(
        imageVector = Icons.Outlined.AutoAwesome,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Banner de aviso")
@Composable
private fun InsightBannerPreview() {
    VoraCoachTheme {
        InsightBanner(
            message = "You're on track. A protein-rich dinner would help you reach your daily target.",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Banner con título y orbe")
@Composable
private fun InsightBannerWithTitlePreview() {
    VoraCoachTheme {
        InsightBanner(
            title = "VORA COACH NOTE",
            message = "Based on your last session, VORA suggests keeping the same weights and focusing on controlled repetitions today.",
            leading = { VoraOrb(diameter = 16.dp) },
            modifier = Modifier.padding(16.dp),
        )
    }
}