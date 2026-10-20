package com.example.voracoach.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.voracoach.R
import com.example.voracoach.ui.screens.auth.SignInScreen
import com.example.voracoach.ui.screens.auth.SignUpScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // se usa para cerrar la app desde la flecha atrás de Sign In, que es la primera pantalla.
    val activity = LocalActivity.current

    NavHost(
        navController = navController,
        startDestination = AppDestination.SignIn.route,
        modifier = modifier
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
            PlaceholderScreen(title = stringResource(R.string.screen_workout))
        }

        composable(AppDestination.Nutrition.route) {
            PlaceholderScreen(title = stringResource(R.string.screen_nutrition))
        }
    }
}

private fun NavHostController.goToHomeClearingAuth() {
    navigate(AppDestination.Home.route) {
        popUpTo(AppDestination.SignIn.route) { inclusive = true }// Al entrar a Home se borran Sign In y Sign Up del historial para que el botón atrás desde Home cierre la app en vez de volver al login
    }
}
