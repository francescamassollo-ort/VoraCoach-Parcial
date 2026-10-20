package com.example.voracoach.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.voracoach.R

enum class BottomBarItem(
    val destination: AppDestination,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Home(AppDestination.Home, R.string.nav_home, Icons.Outlined.Home),
    Workout(AppDestination.Workout, R.string.nav_workout, Icons.Outlined.FitnessCenter),
    Nutrition(AppDestination.Nutrition, R.string.nav_nutrition, Icons.Outlined.Restaurant)
}
