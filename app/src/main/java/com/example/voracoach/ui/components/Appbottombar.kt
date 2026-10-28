package com.example.voracoach.ui.components

/*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.navigation.BottomBarItem
import com.example.voracoach.ui.theme.TextMuted
import com.example.voracoach.ui.theme.VoraCoachTheme


/**
 * Barra de navegación inferior: fondo blanco, borde superior gris y 5 ítems de 64 dp.
 */
@Composable
fun AppBottomBar(
    selected: BottomBarItem,
    onDestinationClick: (BottomBarItem) -> Unit,
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
                BottomBarItem.entries.forEach { destination ->
                    BottomItem(
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
private fun BottomItem(
    destination: BottomBarItem,
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
            /*text = destination.labelRes,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            ),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        */
            text = stringResource(destination.labelRes),   // ← antes: destination.labelRes
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
        var selected by remember { mutableStateOf(BottomBarItem.Home) }
        AppBottomBar(selected = selected, onDestinationClick = { selected = it })
    }
}
*/

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.voracoach.navigation.BottomBarItem
import com.example.voracoach.ui.theme.TextMuted
import com.example.voracoach.ui.theme.VoraCoachTheme

/**
 * Barra de navegación inferior: fondo blanco, borde superior gris y 5 ítems de 64 dp.
 *
 * Recibe la pantalla actual para saber qué pestaña marcar, y avisa el clic con [onItemSelected].
 * No navega por sí sola: quien la use decide qué hacer (normalmente `navController.navigateToTab(item)`).
 *
 * @param currentDestination destino actual del NavController, o null si todavía no hay ninguno
 * (en ese caso no se marca ninguna pestaña).
 */
@Composable
fun AppBottomBar(
    currentDestination: NavDestination?,
    onItemSelected: (BottomBarItem) -> Unit,
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
                BottomBarItem.entries.forEach { item ->
                    // hierarchy incluye la pantalla actual y sus "padres", cuando un área tenga sub-pantallas, la pestaña sigue marcada aunque estés en una de ellas
                    val selected = currentDestination?.hierarchy?.any { it.route == item.destination.route } == true
                    BottomItem(
                        item = item,
                        selected = selected,
                        onClick = { onItemSelected(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomItem(
    item: BottomBarItem,
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
            imageVector = item.icon,
            contentDescription = null, // el texto de abajo ya describe el ítem
            tint = color,
            modifier = Modifier.size(20.dp),
        )

        Text(
            text = stringResource(item.labelRes), // labelRes es un ID de recurso (Int), no un String
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            ),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

/** Crea un destino falso solo para los previews, con la ruta de la pestaña indicada. */
private fun previewDestination(item: BottomBarItem): NavDestination =
    NavDestination("preview").apply { route = item.destination.route }

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Interactiva")
@Composable
private fun AppBottomBarPreview() {
    VoraCoachTheme {
        // Tocá las pestañas en el preview interactivo para ver cómo cambia la seleccionada.
        var selectedItem by remember { mutableStateOf(BottomBarItem.Home) }
        AppBottomBar(
            currentDestination = previewDestination(selectedItem),
            onItemSelected = { selectedItem = it },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Sin pestaña seleccionada")
@Composable
private fun AppBottomBarNoSelectionPreview() {
    VoraCoachTheme {
        AppBottomBar(
            currentDestination = null,
            onItemSelected = {},
        )
    }
}