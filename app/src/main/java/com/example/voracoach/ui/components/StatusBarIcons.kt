package com.example.voracoach.ui.components

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat

// Color de los íconos de la barra de estado (hora, batería, señal) según el fondo de la pantalla:
// negros sobre fondo claro y blancos sobre fondo oscuro (Pre Workout, Active Workout, estados de Coach).
// Se llama una sola vez, desde AppNavHost, con la pantalla actual: si cada pantalla lo cambiara
// por su cuenta, al pasar de una pantalla oscura a otra la que sale podría pisar a la que entra.
@Composable
fun StatusBarIconsFor(darkBackground: Boolean) {
    val window = LocalActivity.current?.window ?: return
    // SideEffect se ejecuta después de cada recomposición con el valor actual
    SideEffect {
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = !darkBackground
    }
}
