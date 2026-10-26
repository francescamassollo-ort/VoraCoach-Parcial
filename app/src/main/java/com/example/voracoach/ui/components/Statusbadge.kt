package com.example.voracoach.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voracoach.ui.theme.VoraCoachTheme // ← cambialo si tu tema se llama distinto
import com.example.voracoach.ui.theme.White12

enum class StatusBadgeStyle {
    /** Píldora blanca translúcida al 12 % con texto blanco, para tarjetas verde oscuro ("MODERATE", "AI ONLINE"). */
    OnDark,

    /** Etiqueta menta suave con texto verde oscuro, para fondos claros ("READY", "CONSISTENT"). */
    Mint,

    /** Solo texto verde oscuro, sin fondo ("TODAY"). */
    Plain,
}

/**
 * Etiqueta corta de estado, en mayúsculas y en una sola línea.
 *
 * ⚠ La variante [StatusBadgeStyle.OnDark] y la [StatusBadgeStyle.Plain] salen de los valores
 * de Figma (relleno blanco al 12 %, padding 10×4, texto de 10 sp SemiBold; "TODAY" con padding
 * 8×2 y esquinas de 4 dp). La variante [StatusBadgeStyle.Mint] ("READY") es una estimación a
 * partir de la captura: revisá el color de fondo contra el diseño.
 *
 * @param fontSize 10 sp por defecto; "AI ONLINE" usa 11 sp en Figma.
 */
@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    style: StatusBadgeStyle = StatusBadgeStyle.OnDark,
    fontSize: TextUnit = 10.sp,
) {
    val scheme = MaterialTheme.colorScheme
    val shape = if (style == StatusBadgeStyle.OnDark) CircleShape else RoundedCornerShape(4.dp)
    val background: Color = when (style) {
        StatusBadgeStyle.OnDark -> White12
        StatusBadgeStyle.Mint -> scheme.secondaryContainer
        StatusBadgeStyle.Plain -> Color.Transparent
    }
    val textColor: Color = when (style) {
        StatusBadgeStyle.OnDark -> Color.White
        StatusBadgeStyle.Mint, StatusBadgeStyle.Plain -> scheme.primary
    }
    val horizontal = if (style == StatusBadgeStyle.OnDark) 10.dp else 8.dp
    val vertical = if (style == StatusBadgeStyle.OnDark) 4.dp else 2.dp

    Text(
        text = text,
        modifier = modifier
            .clip(shape)
            .background(background)
            .padding(horizontal = horizontal, vertical = vertical),
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
        ),
        color = textColor,
        maxLines = 1,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF123D35, name = "Badges sobre verde oscuro")
@Composable
private fun StatusBadgeOnDarkPreview() {
    VoraCoachTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusBadge("MODERATE")
            StatusBadge("AI ONLINE", fontSize = 11.sp)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, name = "Badges sobre fondo claro")
@Composable
private fun StatusBadgeLightPreview() {
    VoraCoachTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusBadge("READY", style = StatusBadgeStyle.Mint)
            StatusBadge("CONSISTENT", style = StatusBadgeStyle.Mint)
            StatusBadge("TODAY", style = StatusBadgeStyle.Plain)
        }
    }
}