package com.example.voracoach.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.VoraCoachTheme // ← cambialo si tu tema se llama distinto

/**
 * Encabezado de sección: título a la izquierda y, opcionalmente, un enlace de texto a la derecha
 * ("View progress", "View history", "Mark all as read").
 *
 * Valores de Figma:
 * - Título grande, como "Today's Overview": 16 sp SemiBold → `titleSmall` (es el valor por defecto).
 * - Título dentro de una tarjeta, como "Your Progress": 14 sp SemiBold → pasá
 *   `titleStyle = MaterialTheme.typography.labelMedium`.
 * - Enlace: 12 sp SemiBold en verde oscuro.
 *
 * El enlace solo aparece si pasás [actionText] y [onActionClick] a la vez.
 *
 * ⚠ En "Mark all as read" (pantalla de notificaciones) no pude leer el tamaño exacto del
 * enlace; acá usa el mismo estilo que los demás.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    actionColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = titleStyle,
            color = titleColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )

        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = actionColor,
                maxLines = 1,
                modifier = Modifier
                    .clickable(role = Role.Button, onClick = onActionClick)
                    // Agranda el área táctil del enlace sin cambiar cómo se ve.
                    .padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Encabezados de sección")
@Composable
private fun SectionHeaderPreview() {
    VoraCoachTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionHeader(title = "Today's Overview")
            SectionHeader(
                title = "Your Progress",
                titleStyle = MaterialTheme.typography.labelMedium,
                actionText = "View progress",
                onActionClick = {},
            )
            SectionHeader(
                title = "Un título de sección muy largo que no entra en una sola línea",
                actionText = "View history",
                onActionClick = {},
            )
        }
    }
}