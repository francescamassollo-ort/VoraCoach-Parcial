package com.example.voracoach.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.TextMuted
import com.example.voracoach.ui.theme.VoraCoachTheme

/** Destinos de la barra inferior. Cuando exista la navegación, `route` se usa con el NavHost. */
enum class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    Home("home", "Home", Icons.Outlined.Home),
    Workout("workout", "Workout", Icons.Outlined.FitnessCenter),
    Nutrition("nutrition", "Nutrition", Icons.Outlined.Restaurant),
    Progress("progress", "Progress", Icons.AutoMirrored.Outlined.ShowChart),
    Profile("profile", "Profile", Icons.Outlined.Person),
}

/**
 * Barra de navegación inferior: fondo blanco, borde superior gris y 5 ítems de 64 dp.
 * Solo es el componente visual; recibe el destino seleccionado y avisa los clics.
 */
@Composable
fun AppBottomBar(
    selected: BottomNavDestination,
    onDestinationClick: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        // El padding de la barra de gestos va adentro para que el blanco se extienda debajo.
        Column(modifier = Modifier.navigationBarsPadding()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BottomNavDestination.entries.forEach { destination ->
                    BottomBarItem(
                        destination = destination,
                        selected = destination == selected,
                        onClick = { onDestinationClick(destination) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    destination: BottomNavDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else TextMuted
    Column(
        modifier = Modifier
            .width(64.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            )
            .padding(vertical = 8.dp), // agranda el área táctil sin mover los íconos
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = null, // el texto de abajo ya describe el ítem
            tint = color,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = destination.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            ),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5)
@Composable
private fun AppBottomBarPreview() {
    VoraCoachTheme {
        var selected by remember { mutableStateOf(BottomNavDestination.Home) }
        AppBottomBar(selected = selected, onDestinationClick = { selected = it })
    }
}
