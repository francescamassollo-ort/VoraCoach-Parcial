package com.example.voracoach.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.DarkGreen
import com.example.voracoach.ui.theme.VoraCoachTheme // ← cambialo si tu tema se llama distinto

/**
 * Encabezado de pantalla: flecha atrás a la izquierda, título alineado a la izquierda
 * (pegado a la flecha) y acciones opcionales a la derecha, por ejemplo el menú "más".
 *
 * Los colores se adaptan solos al fondo donde se coloque:
 * - [contentColor] (flecha, título y acciones) toma por defecto el color de contenido del
 *   contenedor padre (Scaffold, Surface, etc.), que es oscuro sobre fondo claro y blanco sobre
 *   un fondo verde oscuro.
 * - El círculo de la flecha también se adapta: sobre fondo claro es blanco con borde gris fino
 *   ([backButtonContainerColor] y [backButtonBorderColor]); sobre fondo oscuro (cuando
 *   [contentColor] es claro) es ese color al 12 % y sin borde, así siempre contrasta.
 * Si una pantalla necesita colores distintos, se pasan por parámetro.
 *
 * Ocupa todo el ancho y deja 8 dp de margen, de modo que el ícono de 24 dp queda a 20 dp del
 * borde, igual que en Figma. No le sumes padding horizontal desde la pantalla.
 */
@Composable
fun ScreenHeader(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = LocalContentColor.current,
    backButtonContainerColor: Color =
        if (contentColor.luminance() > 0.5f) contentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
    backButtonBorderColor: Color? =
        if (contentColor.luminance() > 0.5f) null else MaterialTheme.colorScheme.outline,
    backContentDescription: String = "Volver",
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // OutlinedIconButton dibuja el borde sobre el círculo de 40 dp (un Modifier.border
        // afuera lo dibujaría alrededor del área táctil de 48 dp). Sin borde (null) queda liso.
        OutlinedIconButton(
            onClick = onBackClick,
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = backButtonContainerColor, // círculo detrás de la flecha
                contentColor = contentColor,               // color de la flecha
            ),
            border = backButtonBorderColor?.let { BorderStroke(1.dp, it) },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = backContentDescription,
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = contentColor,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            // weight(1f): ocupa el espacio entre la flecha y las acciones, sin pisarlas
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp),
        )

        // Los íconos de las acciones también toman el color adaptado.
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            actions()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Sobre fondo claro")
@Composable
private fun ScreenHeaderLightPreview() {
    VoraCoachTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ScreenHeader(
                title = "Workout Overview",
                onBackClick = {},
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.MoreHoriz, contentDescription = "Más opciones")
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF123D35, name = "Sobre fondo verde oscuro")
@Composable
private fun ScreenHeaderGreenPreview() {
    VoraCoachTheme {
        // Surface define el color de contenido (blanco) que el encabezado hereda solo.
        Surface(color = DarkGreen, contentColor = Color.White) {
            ScreenHeader(title = "Listening", onBackClick = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Título largo")
@Composable
private fun ScreenHeaderLongTitlePreview() {
    VoraCoachTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ScreenHeader(
                title = "Un título muy largo que no entra en una sola línea",
                onBackClick = {},
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.MoreHoriz, contentDescription = "Más opciones")
                    }
                },
            )
        }
    }
}