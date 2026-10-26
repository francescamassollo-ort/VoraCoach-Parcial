package com.example.voracoach.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.voracoach.R
import com.example.voracoach.ui.components.StatusBarIconsFor
import com.example.voracoach.ui.screens.auth.SignInScreen
import com.example.voracoach.ui.screens.auth.SignUpScreen
import com.example.voracoach.ui.screens.workout.WorkoutCatalogRoute
import com.example.voracoach.ui.screens.workout.ActiveWorkoutRoute
import com.example.voracoach.ui.screens.workout.PreWorkoutRoute
import com.example.voracoach.ui.screens.workout.WorkoutCompleteRoute
import com.example.voracoach.ui.screens.workout.WorkoutDetailRoute
import com.example.voracoach.viewmodel.WorkoutDetailViewModel.Companion.WORKOUT_ID_ARG

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // se usa para cerrar la app desde la flecha atrás de Sign In, que es la primera pantalla.
    val activity = LocalActivity.current

    // Pantalla actual, observada como estado: cada vez que se navega, esto cambia
    // y la barra se vuelve a dibujar marcando la pestaña correcta.
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentEntry?.destination

    // La barra solo aparece en las pantallas que son pestañas. En Sign In, Sign Up,
    // Notifications y las futuras sub-pantallas (detalle, agregar...) se oculta.
    val showBottomBar = BottomBarItem.entries.any { it.destination.route == currentDestination?.route }

    // Pantallas con fondo verde oscuro: ahí la hora y la batería se muestran en blanco.
    // Al sumar otra pantalla oscura (por ejemplo, estados de Coach), agregar su ruta acá.
    val darkBackgroundRoutes = setOf(
        AppDestination.PreWorkout.route,
        AppDestination.ActiveWorkout.route
    )
    StatusBarIconsFor(darkBackground = currentDestination?.route in darkBackgroundRoutes)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentDestination = currentDestination,
                    onItemSelected = { navController.navigateToTab(it) }
                )
            }
        },
        // Sin márgenes automáticos por la barra de estado: cada pantalla ya tiene su propio
        // Scaffold que los aplica. Si los aplicaran los dos, el contenido quedaría el doble de bajo.
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.SignIn.route,
            // innerPadding deja lugar para la barra inferior, así no tapa el contenido
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestination.SignIn.route) {
                SignInScreen(
                    onBack = { activity?.finish() },
                    onSignIn = { navController.goToHomeClearingAuth() },
                    onGoToSignUp = { navController.navigate(AppDestination.SignUp.route) }
                )
            }

            composable(AppDestination.SignUp.route) {
                SignUpScreen(
                    onBack = { navController.popBackStack() },
                    onSignUp = { navController.goToHomeClearingAuth() },
                    // Sign Up siempre se abre desde Sign In, así que "Sign In" es volver atrás y no apilar otra copia de Sign In.
                    onGoToSignIn = { navController.popBackStack() }
                )
            }

            composable(AppDestination.Home.route) {
                PlaceholderScreen(title = stringResource(R.string.screen_home))
            }

            // Las tres rutas quedan registradas aunque todavía no haya pantalla real,
            // así ningún botón que navegue hacia ellas hace crashear la app.
            composable(AppDestination.Notifications.route) {
                PlaceholderScreen(title = stringResource(R.string.screen_notifications))
            }

            composable(AppDestination.Workout.route) {
                WorkoutCatalogRoute(
                    onWorkoutClick = { workoutId ->
                        navController.navigate(AppDestination.WorkoutDetail.createRoute(workoutId))
                    }
                )
            }

            // navArgument declara el parámetro de la ruta. El ViewModel del detalle lo lee
            // con SavedStateHandle, así que la pantalla no necesita recibir el id.
            composable(
                route = AppDestination.WorkoutDetail.route,
                arguments = listOf(navArgument(WORKOUT_ID_ARG) { type = NavType.StringType })
            ) {
                WorkoutDetailRoute(
                    onBack = { navController.popBackStack() },
                    onStartWorkout = { workoutId ->
                        navController.navigate(AppDestination.PreWorkout.createRoute(workoutId))
                    }
                )
            }

            composable(
                route = AppDestination.PreWorkout.route,
                arguments = listOf(navArgument(WORKOUT_ID_ARG) { type = NavType.StringType })
            ) {
                PreWorkoutRoute(
                    // La X vuelve al detalle, igual que el botón atrás del sistema
                    onClose = { navController.popBackStack() },
                    onStartWorkout = { workoutId ->
                        navController.navigate(AppDestination.ActiveWorkout.createRoute(workoutId))
                    }
                )
            }

            composable(
                route = AppDestination.ActiveWorkout.route,
                arguments = listOf(navArgument(WORKOUT_ID_ARG) { type = NavType.StringType })
            ) {
                ActiveWorkoutRoute(
                    onClose = { navController.popBackStack() },
                    onFinishWorkout = { workoutId ->
                        navController.navigate(AppDestination.WorkoutComplete.createRoute(workoutId))
                    }
                )
            }

            composable(
                route = AppDestination.WorkoutComplete.route,
                arguments = listOf(navArgument(WORKOUT_ID_ARG) { type = NavType.StringType })
            ) {
                WorkoutCompleteRoute(
                    // Vuelve al catálogo sacando del historial detalle, Pre y Active:
                    // así atrás desde el catálogo no reabre un entrenamiento ya terminado
                    onDone = { navController.popBackStack(AppDestination.Workout.route, inclusive = false) }
                )
            }

            composable(AppDestination.Nutrition.route) {
                PlaceholderScreen(title = stringResource(R.string.screen_nutrition))
            }

            composable(AppDestination.Progress.route) {
                PlaceholderScreen(title = stringResource(R.string.screen_progress))
            }

            composable(AppDestination.Profile.route) {
                PlaceholderScreen(title = stringResource(R.string.screen_profile))
            }

            composable(AppDestination.Coach.route) {
                PlaceholderScreen(title = stringResource(R.string.screen_coach))
            }
        }
    }
}

private fun NavHostController.goToHomeClearingAuth() {
    navigate(AppDestination.Home.route) {
        popUpTo(AppDestination.SignIn.route) { inclusive = true }// Al entrar a Home se borran Sign In y Sign Up del historial para que el botón atrás desde Home cierre la app en vez de volver al login
    }
}
