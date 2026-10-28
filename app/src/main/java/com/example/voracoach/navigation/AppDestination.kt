package com.example.voracoach.navigation

import com.example.voracoach.viewmodel.WorkoutDetailViewModel.Companion.WORKOUT_ID_ARG

// Nombre del argumento con el id del ejercicio en la ruta de la guía
const val EXERCISE_ID_ARG = "exerciseId"

//Ruteo
sealed class AppDestination(val route: String) {
    data object SignIn : AppDestination("sign_in")
    data object SignUp : AppDestination("sign_up")
    data object Home : AppDestination("home")
    data object Notifications : AppDestination("notifications")
    data object Workout : AppDestination("workout")
    data object Nutrition : AppDestination("nutrition")
    data object Progress : AppDestination("progress")
    data object Profile : AppDestination("profile")
    data object Coach : AppDestination("coach")

    // Rutas con parámetro: "route" es el patrón que registra el NavHost ({workoutId} es un
    // lugar a completar) y createRoute arma la ruta concreta para navegar ("workout/w01").
    // Así cada entrenamiento del catálogo abre su propio detalle (NAV04).
    data object WorkoutDetail : AppDestination("workout/{$WORKOUT_ID_ARG}") {
        fun createRoute(workoutId: String) = "workout/$workoutId"
    }

    data object PreWorkout : AppDestination("workout/{$WORKOUT_ID_ARG}/pre") {
        fun createRoute(workoutId: String) = "workout/$workoutId/pre"
    }

    data object ActiveWorkout : AppDestination("workout/{$WORKOUT_ID_ARG}/active") {
        fun createRoute(workoutId: String) = "workout/$workoutId/active"
    }

    data object WorkoutComplete : AppDestination("workout/{$WORKOUT_ID_ARG}/complete") {
        fun createRoute(workoutId: String) = "workout/$workoutId/complete"
    }

    data object ExerciseGuide : AppDestination("workout/{$WORKOUT_ID_ARG}/exercise/{$EXERCISE_ID_ARG}") {
        fun createRoute(workoutId: String, exerciseId: String) = "workout/$workoutId/exercise/$exerciseId"
    }

    data object WeeklyPlan : AppDestination("workout_weekly")

    data object EditProfile : AppDestination("profile_edit")
}
