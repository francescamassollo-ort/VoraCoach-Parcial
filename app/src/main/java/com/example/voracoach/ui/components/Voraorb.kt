package com.example.voracoach.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.DarkGreen
import com.example.voracoach.ui.theme.Mint
import com.example.voracoach.ui.theme.VoraCoachTheme // ← cambialo si tu tema se llama distinto

/**
 * Orbe de la IA de VORA. Se dibuja con tres capas, como en Figma: brillo exterior (glow),
 * anillo (ring) y núcleo (core). Las proporciones son las del diseño: el anillo mide el 90 %
 * del diámetro y el núcleo el 75 %. Aparece en casi todas las pantallas, de 16 dp a 170 dp.
 *
 * ⚠ En Figma las capas son SVG con degradados. No pude descargarlos, así que los degradados
 * y la opacidad del brillo y del anillo son una aproximación a partir de capturas. Si querés
 * el aspecto exacto, exportá las tres capas como Vector Drawable y reemplazá el `Canvas`.
 *
 * @param diameter tamaño total del orbe.
 * @param content contenido opcional centrado (por ejemplo un ícono de micrófono o de onda).
 */
@Composable
fun VoraOrb(
    diameter: Dp,
    modifier: Modifier = Modifier,
    content: (@Composable BoxScope.() -> Unit)? = null,
) {
    Box(
        modifier = modifier.size(diameter),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val d = size.minDimension
            val c = center

            // 1) Brillo exterior: un halo menta que se desvanece hacia afuera.
            drawCircle(
                brush = Brush.radialGradient(
                    0.55f to Color.Transparent,
                    0.80f to Mint.copy(alpha = 0.35f),
                    1f to Color.Transparent,
                    center = c,
                    radius = d / 2f,
                ),
                radius = d / 2f,
                center = c,
            )

            // 2) Anillo: línea fina clara al 90 % del diámetro.
            val strokeWidth = maxOf(1.dp.toPx(), d * 0.015f)
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = d * 0.45f - strokeWidth / 2f,
                center = c,
                style = Stroke(width = strokeWidth),
            )

            // 3) Núcleo: degradado diagonal de menta a verde oscuro, al 75 % del diámetro.
            val r = d * 0.375f
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(Mint, DarkGreen),
                    start = Offset(c.x - r, c.y - r),
                    end = Offset(c.x + r, c.y + r),
                ),
                radius = r,
                center = c,
            )
        }
        content?.invoke(this)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF123D35, name = "Orbe sobre verde oscuro")
@Composable
private fun VoraOrbDarkPreview() {
    VoraCoachTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VoraOrb(diameter = 24.dp)
            VoraOrb(diameter = 36.dp)
            VoraOrb(diameter = 72.dp)
            VoraOrb(diameter = 140.dp) {
                Icon(
                    imageVector = Icons.Outlined.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Orbe sobre fondo claro")
@Composable
private fun VoraOrbLightPreview() {
    VoraCoachTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VoraOrb(diameter = 28.dp)
            VoraOrb(diameter = 56.dp)
        }
    }
}