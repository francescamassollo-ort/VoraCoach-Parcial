package com.example.voracoach.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController

// Barra inferior.
@Composable
fun AppBottomBar(
    currentDestination: NavDestination?,
    onItemSelected: (BottomBarItem) -> Unit
) {
    NavigationBar {
        BottomBarItem.entries.forEach { item ->
            // hierarchy incluye la pantalla actual y sus "padres", cuando un área tenga sub-pantallas, la pestaña sigue marcada aunque estés en una de ellas
            val selected = currentDestination?.hierarchy?.any { it.route == item.destination.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item) },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.labelRes)) }
            )
        }
    }
}

fun NavHostController.navigateToTab(item: BottomBarItem) {
    navigate(item.destination.route) {
        popUpTo(AppDestination.Home.route) { saveState = true }// - saveState / restoreState: cada pestaña recuerda dónde quedaste al volver a ella.
        launchSingleTop = true // - launchSingleTop: tocar la pestaña en la que ya estás no crea otra copia.
        restoreState = true
    }
}
