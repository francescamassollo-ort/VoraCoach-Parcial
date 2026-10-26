package com.example.voracoach.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.VoraCoachTheme // ← cambialo si tu tema se llama distinto
import com.example.voracoach.ui.theme.White12

enum class CircleIconButtonStyle {
    /** Para fondos claros: círculo blanco con borde gris fino (campana, calendario, engranaje). */
    Light,

    /** Para fondos verde oscuro: círculo blanco translúcido al 12 % con ícono blanco (cerrar). */
    OnDark,
}

/**
 * Botón circular de 40 dp con un ícono de 20 dp, como la campana de Home o el calendario de
 * Workout. Su área táctil es de 48 dp, el mínimo recomendado por Android, aunque se vea de 40.
 *
 * @param contentDescription descripción para lectores de pantalla (por ejemplo "Notificaciones").
 * Es obligatoria porque el botón no tiene texto.
 * @param showBadge muestra un punto de aviso arriba a la derecha, como la campana con
 * notificaciones sin leer.
 *
 * ⚠ En Figma el botón de la campana es un SVG completo y no pude leer su color de punto ni el
 * del borde; acá el punto es verde oscuro y el borde usa el gris del tema. Revisalo contra el
 * diseño.
 */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CircleIconButtonStyle = CircleIconButtonStyle.Light,
    showBadge: Boolean = false,
    badgeColor: Color = MaterialTheme.colorScheme.primary,
) {
    Box(modifier = modifier) {
        val iconContent: @Composable () -> Unit = {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp),
            )
        }

        when (style) {
            CircleIconButtonStyle.Light -> OutlinedIconButton(
                onClick = onClick,
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                content = iconContent,
            )

            CircleIconButtonStyle.OnDark -> IconButton(
                onClick = onClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = White12,
                    contentColor = Color.White,
                ),
                content = iconContent,
            )
        }

        if (showBadge) {
            // Punto de 8 dp. El botón visible (40 dp) queda centrado en el área táctil (48 dp),
            // así que el punto, que en Figma está a (20, 12) dentro de los 40 dp, va a (24, 16).
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.dp, top = 16.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Botones sobre fondo claro")
@Composable
private fun CircleIconButtonLightPreview() {
    VoraCoachTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(
                icon = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                onClick = {},
                showBadge = true,
            )
            CircleIconButton(
                icon = Icons.Outlined.CalendarMonth,
                contentDescription = "Calendario",
                onClick = {},
            )
            CircleIconButton(
                icon = Icons.Outlined.Settings,
                contentDescription = "Ajustes",
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF123D35, name = "Botón sobre verde oscuro")
@Composable
private fun CircleIconButtonOnDarkPreview() {
    VoraCoachTheme {
        CircleIconButton(
            icon = Icons.Outlined.Close,
            contentDescription = "Cerrar",
            onClick = {},
            style = CircleIconButtonStyle.OnDark,
            modifier = Modifier.padding(16.dp),
        )
    }
}