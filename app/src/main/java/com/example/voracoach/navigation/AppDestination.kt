package com.example.voracoach.navigation

import com.example.voracoach.model.MealType
import com.example.voracoach.viewmodel.WorkoutDetailViewModel.Companion.WORKOUT_ID_ARG

// Nombre de los argumentos con el id de ejercicio, comida y tipo de comida
const val EXERCISE_ID_ARG = "exerciseId"
const val MEAL_ID_ARG = "mealId"
const val MEAL_TYPE_ARG = "mealType"
const val FROM_ANALYSIS_ARG = "fromAnalysis"
const val EDIT_MEAL_ID_ARG = "editMealId"

// Ruteo
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

    data object Goals : AppDestination("profile_goals")

    data object VoiceSettings : AppDestination("voice_settings")

    data object NutritionPreferences : AppDestination("profile_nutrition")

    // Sub-pantallas de Progress
    data object TodayOverview : AppDestination("progress_today")

    data object WorkoutProgress : AppDestination("progress_workout")

    data object NutritionProgress : AppDestination("progress_nutrition")

    data object Settings : AppDestination("settings")

    data object HealthTrends : AppDestination("progress_health")

    data object WeeklyReview : AppDestination("progress_weekly_review")

    // Ruta del subgrafo que agrupa todas las pantallas de Nutrition (no es una pantalla)
    data object NutritionGraph : AppDestination("nutrition_graph")

    // Sub-pantallas de Nutrition
    data object TodayMeals : AppDestination("nutrition_today_meals")

    data object AddFood : AppDestination("nutrition_add_food")

    data object VoiceFoodLogging : AppDestination("nutrition_voice")

    data object FoodAnalysis : AppDestination("nutrition_analysis")

    data object PhotoAnalysis : AppDestination("nutrition_photo")

    data object SavedMeals : AppDestination("nutrition_saved_meals")

    // mealType: horario de la comida. Opcionales, por query:
    // - fromAnalysis: true si viene de voz o foto, para arrancar con los ingredientes "detectados".
    // - editMealId: id de la comida a editar ("Edit Meal"); -1 si es una comida nueva.
    data object FoodConfirmation : AppDestination(
        "food_confirmation/{$MEAL_TYPE_ARG}?$FROM_ANALYSIS_ARG={$FROM_ANALYSIS_ARG}&$EDIT_MEAL_ID_ARG={$EDIT_MEAL_ID_ARG}"
    ) {
        fun createRoute(
            mealType: String = MealType.forCurrentTime().name,
            fromAnalysis: Boolean = false,
            editMealId: Int = -1,
        ) = "food_confirmation/$mealType?$FROM_ANALYSIS_ARG=$fromAnalysis&$EDIT_MEAL_ID_ARG=$editMealId"
    }

    data object MealDetails : AppDestination("nutrition_detail/{$MEAL_ID_ARG}") {
        fun createRoute(mealId: String) = "nutrition_detail/$mealId"
    }
}
