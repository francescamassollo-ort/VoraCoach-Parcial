package com.example.voracoach.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.voracoach.ui.components.AppBottomBar
import com.example.voracoach.ui.components.StatusBarIconsFor
import com.example.voracoach.ui.screens.auth.SignInScreen
import com.example.voracoach.ui.screens.auth.SignUpScreen
import com.example.voracoach.ui.screens.coach.CoachRoute
import com.example.voracoach.ui.screens.home.HomeRoute
import com.example.voracoach.ui.screens.home.NotificationsRoute
import com.example.voracoach.ui.screens.nutrition.AddFood
import com.example.voracoach.ui.screens.nutrition.DashboardRoute
import com.example.voracoach.ui.screens.nutrition.FoodAnalysis
import com.example.voracoach.ui.screens.nutrition.FoodConfirmation
import com.example.voracoach.ui.screens.nutrition.MealDetails
import com.example.voracoach.ui.screens.nutrition.PhotoAnalysis
import com.example.voracoach.ui.screens.nutrition.TodayMeals
import com.example.voracoach.ui.screens.nutrition.VoiceFoodLogging
import com.example.voracoach.ui.screens.profile.EditProfileRoute
import com.example.voracoach.ui.screens.profile.GoalsRoute
import com.example.voracoach.ui.screens.profile.NutritionPreferencesRoute
import com.example.voracoach.ui.screens.profile.ProfileRoute
import com.example.voracoach.ui.screens.profile.SettingsRoute
import com.example.voracoach.ui.screens.profile.VoiceSettingsRoute
import com.example.voracoach.ui.screens.progress.HealthTrendsRoute
import com.example.voracoach.ui.screens.progress.NutritionProgressRoute
import com.example.voracoach.ui.screens.progress.ProgressDashboardRoute
import com.example.voracoach.ui.screens.progress.TodayOverviewRoute
import com.example.voracoach.ui.screens.progress.WeeklyReviewScreen
import com.example.voracoach.ui.screens.progress.WorkoutProgressRoute
import com.example.voracoach.ui.screens.workout.ActiveWorkoutRoute
import com.example.voracoach.ui.screens.workout.ExerciseGuideRoute
import com.example.voracoach.ui.screens.workout.PreWorkoutRoute
import com.example.voracoach.ui.screens.workout.WeeklyPlanRoute
import com.example.voracoach.ui.screens.workout.WorkoutCatalogRoute
import com.example.voracoach.ui.screens.workout.WorkoutCompleteRoute
import com.example.voracoach.ui.screens.workout.WorkoutDetailRoute
import com.example.voracoach.viewmodel.NutritionViewModel
import com.example.voracoach.viewmodel.WorkoutDetailViewModel.Companion.WORKOUT_ID_ARG

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val activity = LocalActivity.current
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentEntry?.destination

    val showBottomBar = BottomBarItem.entries.any { it.destination.route == currentDestination?.route }

    val darkBackgroundRoutes = setOf(
        AppDestination.PreWorkout.route,
        AppDestination.ActiveWorkout.route,
        AppDestination.WeeklyReview.route,
    )

    if (currentDestination?.route != AppDestination.Coach.route) {
        StatusBarIconsFor(darkBackground = currentDestination?.route in darkBackgroundRoutes)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentDestination = currentDestination,
                    onItemSelected = { navController.navigateToTab(it) },
                )
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.SignIn.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AppDestination.SignIn.route) {
                SignInScreen(
                    onBack = { activity?.finish() },
                    onSignIn = { navController.goToHomeClearingAuth() },
                    onGoToSignUp = { navController.navigate(AppDestination.SignUp.route) },
                )
            }

            composable(AppDestination.SignUp.route) {
                SignUpScreen(
                    onBack = { navController.popBackStack() },
                    onSignUp = { navController.goToHomeClearingAuth() },
                    onGoToSignIn = { navController.popBackStack() },
                )
            }

            composable(AppDestination.Home.route) {
                HomeRoute(
                    onNotificationsClick = { navController.navigate(AppDestination.Notifications.route) },
                    onStartVoiceSession = { navController.navigate(AppDestination.Coach.route) },
                    onStartMyWorkout = { navController.navigateToTab(BottomBarItem.Workout) },
                    onLogBreakfast = { navController.navigateToTab(BottomBarItem.Nutrition) },
                    onWorkoutClick = { workoutId ->
                        navController.navigate(AppDestination.WorkoutDetail.createRoute(workoutId))
                    },
                    onTodayOverviewClick = { navController.navigate(AppDestination.TodayOverview.route) },
                )
            }

            composable(AppDestination.Notifications.route) {
                NotificationsRoute(onBack = { navController.popBackStack() })
            }

            composable(AppDestination.Workout.route) {
                WorkoutCatalogRoute(
                    onWorkoutClick = { workoutId ->
                        navController.navigate(AppDestination.WorkoutDetail.createRoute(workoutId))
                    },
                    onWeeklyPlanClick = { navController.navigate(AppDestination.WeeklyPlan.route) },
                )
            }

            composable(AppDestination.WeeklyPlan.route) {
                WeeklyPlanRoute(
                    onBack = { navController.popBackStack() },
                    onWorkoutClick = { workoutId ->
                        navController.navigate(AppDestination.WorkoutDetail.createRoute(workoutId))
                    },
                )
            }

            composable(
                route = AppDestination.WorkoutDetail.route,
                arguments = listOf(navArgument(WORKOUT_ID_ARG) { type = NavType.StringType }),
            ) {
                WorkoutDetailRoute(
                    onBack = { navController.popBackStack() },
                    onStartWorkout = { workoutId ->
                        navController.navigate(AppDestination.PreWorkout.createRoute(workoutId))
                    },
                    onExerciseClick = { workoutId, exerciseId ->
                        navController.navigate(AppDestination.ExerciseGuide.createRoute(workoutId, exerciseId))
                    },
                )
            }

            composable(
                route = AppDestination.ExerciseGuide.route,
                arguments = listOf(
                    navArgument(WORKOUT_ID_ARG) { type = NavType.StringType },
                    navArgument(EXERCISE_ID_ARG) { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                ExerciseGuideRoute(
                    exerciseId = backStackEntry.arguments?.getString(EXERCISE_ID_ARG).orEmpty(),
                    onBack = { navController.popBackStack() },
                )
            }

            composable(
                route = AppDestination.PreWorkout.route,
                arguments = listOf(navArgument(WORKOUT_ID_ARG) { type = NavType.StringType }),
            ) {
                PreWorkoutRoute(
                    onClose = { navController.popBackStack() },
                    onStartWorkout = { workoutId ->
                        navController.navigate(AppDestination.ActiveWorkout.createRoute(workoutId))
                    },
                )
            }

            composable(
                route = AppDestination.ActiveWorkout.route,
                arguments = listOf(navArgument(WORKOUT_ID_ARG) { type = NavType.StringType }),
            ) {
                ActiveWorkoutRoute(
                    onClose = { navController.popBackStack() },
                    onFinishWorkout = { workoutId ->
                        navController.navigate(AppDestination.WorkoutComplete.createRoute(workoutId))
                    },
                    onExerciseInfoClick = { workoutId, exerciseId ->
                        navController.navigate(AppDestination.ExerciseGuide.createRoute(workoutId, exerciseId))
                    },
                )
            }

            composable(
                route = AppDestination.WorkoutComplete.route,
                arguments = listOf(navArgument(WORKOUT_ID_ARG) { type = NavType.StringType }),
            ) {
                WorkoutCompleteRoute(
                    onDone = { navController.popBackStack(AppDestination.WorkoutDetail.route, inclusive = true) },
                )
            }

            // ── NUTRITION ─────────────────────────────────────────────────────
            // Subgrafo: agrupa las pantallas de Nutrition bajo una ruta propia. Así el
            // NutritionViewModel se ata a este grupo: todas sus pantallas comparten la misma
            // instancia (lo que se agrega en FoodConfirmation se ve en el Dashboard) y se
            // descarta cuando el usuario sale de Nutrition.
            navigation(
                route = AppDestination.NutritionGraph.route,
                startDestination = AppDestination.Nutrition.route,
            ) {
                composable(AppDestination.Nutrition.route) { entry ->
                    DashboardRoute(
                        viewModel = entry.sharedNutritionViewModel(navController),
                        onNavigateToTodayMeals = { navController.navigate(AppDestination.TodayMeals.route) },
                        onNavigateToAddFood = { navController.navigate(AppDestination.AddFood.route) },
                        onNavigateToVoice = { navController.navigate(AppDestination.VoiceFoodLogging.route) },
                        onNavigateToPhoto = { navController.navigate(AppDestination.PhotoAnalysis.route) },
                    )
                }

                composable(AppDestination.TodayMeals.route) { entry ->
                    TodayMeals(
                        viewModel = entry.sharedNutritionViewModel(navController),
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToAddFood = { navController.navigate(AppDestination.AddFood.route) },
                    )
                }

                composable(AppDestination.AddFood.route) {
                    AddFood(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToVoice = { navController.navigate(AppDestination.VoiceFoodLogging.route) },
                        onNavigateToPhoto = { navController.navigate(AppDestination.PhotoAnalysis.route) },
                        onNavigateToManual = { navController.navigate(AppDestination.FoodConfirmation.route) },
                    )
                }

                composable(AppDestination.VoiceFoodLogging.route) {
                    VoiceFoodLogging(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToDoneSpeaking = { navController.navigate(AppDestination.FoodAnalysis.route) },
                    )
                }

                composable(AppDestination.FoodAnalysis.route) {
                    FoodAnalysis(
                        onAnalysisComplete = { navController.navigate(AppDestination.FoodConfirmation.route) },
                        onNavigateBack = { navController.popBackStack() },
                    )
                }

                composable(AppDestination.PhotoAnalysis.route) {
                    PhotoAnalysis(
                        onNavigateBack = { navController.popBackStack() },
                        onReviewAndAdd = { navController.navigate(AppDestination.FoodConfirmation.route) },
                    )
                }

                composable(AppDestination.FoodConfirmation.route) { entry ->
                    FoodConfirmation(
                        viewModel = entry.sharedNutritionViewModel(navController),
                        onNavigateBack = { navController.popBackStack() },
                        onSaveSuccess = {
                            navController.popBackStack(AppDestination.Nutrition.route, inclusive = false)
                        },
                    )
                }

                composable(
                    route = AppDestination.MealDetails.route,
                    arguments = listOf(navArgument(MEAL_ID_ARG) { type = NavType.StringType }),
                ) { backStackEntry ->
                    val mealId = backStackEntry.arguments?.getString(MEAL_ID_ARG).orEmpty()
                    val nutritionViewModel = backStackEntry.sharedNutritionViewModel(navController)
                    val meals by nutritionViewModel.meals.collectAsStateWithLifecycle()
                    // Si la comida no existe (por ejemplo, se borró), queda null y la pantalla
                    // muestra un estado vacío en vez de datos inventados
                    val meal = meals.find { it.id.toString() == mealId }
                    MealDetails(
                        meal = meal,
                        viewModel = nutritionViewModel,
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
            }

            // ── PROGRESS ──────────────────────────────────────────────────────
            composable(AppDestination.Progress.route) {
                ProgressDashboardRoute(
                    onWorkoutClick = { navController.navigate(AppDestination.WorkoutProgress.route) },
                    onNutritionClick = { navController.navigate(AppDestination.NutritionProgress.route) },
                    onActivityClick = { navController.navigate(AppDestination.HealthTrends.route) },
                    onWeeklyReviewClick = { navController.navigate(AppDestination.WeeklyReview.route) },
                )
            }

            composable(AppDestination.TodayOverview.route) {
                TodayOverviewRoute(
                    onBack = { navController.popBackStack() },
                    onWorkoutClick = { workoutId ->
                        navController.navigate(AppDestination.WorkoutDetail.createRoute(workoutId))
                    },
                )
            }

            composable(AppDestination.WorkoutProgress.route) {
                WorkoutProgressRoute(onBack = { navController.popBackStack() })
            }

            composable(AppDestination.NutritionProgress.route) {
                NutritionProgressRoute(onBack = { navController.popBackStack() })
            }

            composable(AppDestination.HealthTrends.route) {
                HealthTrendsRoute(onBack = { navController.popBackStack() })
            }

            composable(AppDestination.WeeklyReview.route) {
                WeeklyReviewScreen(
                    onBack = { navController.popBackStack() },
                    onDinnerIdeasClick = { navController.navigateToTab(BottomBarItem.Nutrition) },
                    onNextWeekPlanClick = { navController.navigate(AppDestination.WeeklyPlan.route) },
                    onAskVoraClick = { navController.navigate(AppDestination.Coach.route) },
                )
            }

            composable(AppDestination.Profile.route) {
                ProfileRoute(
                    onSettingsClick = { /* Settings (pantalla 06) */ },
                    onEditProfileClick = { navController.navigate(AppDestination.EditProfile.route) },
                    onGoalsClick = { navController.navigate(AppDestination.Goals.route) },
                    onPreferencesClick = { navController.navigate(AppDestination.NutritionPreferences.route) },
                    onConnectedDataClick = { /* Settings (pantalla 06) */ },
                    onAccountClick = { navController.navigate(AppDestination.Settings.route) },
                    onReviewPreferencesClick = { navController.navigate(AppDestination.VoiceSettings.route) },
                )
            }

            composable(AppDestination.EditProfile.route) {
                EditProfileRoute(onBack = { navController.popBackStack() })
            }

            composable(AppDestination.Goals.route) {
                GoalsRoute(onBack = { navController.popBackStack() })
            }

            composable(AppDestination.VoiceSettings.route) {
                VoiceSettingsRoute(
                    onBack = { navController.popBackStack() },
                    onPrivacyClick = {},
                )
            }

            composable(AppDestination.Settings.route) {
                SettingsRoute(
                    onBack = { navController.popBackStack() },
                    onItemClick = { /* destino de cada fila: a definir */ },
                    onLogout = {
                        navController.navigate(AppDestination.SignIn.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                )
            }

            composable(AppDestination.NutritionPreferences.route) {
                NutritionPreferencesRoute(onBack = { navController.popBackStack() })
            }

            composable(AppDestination.Coach.route) {
                CoachRoute(onExit = { navController.popBackStack() })
            }
        }
    }
}

private fun NavHostController.goToHomeClearingAuth() {
    navigate(AppDestination.Home.route) {
        popUpTo(AppDestination.SignIn.route) { inclusive = true }
    }
}

fun NavHostController.navigateToTab(item: BottomBarItem) {
    navigate(item.destination.route) {
        popUpTo(AppDestination.Home.route) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// Devuelve el NutritionViewModel del subgrafo de Nutrition (no el de esta pantalla sola):
// se pide a la entrada del grafo padre, que vive mientras haya alguna pantalla de Nutrition abierta.
@Composable
private fun NavBackStackEntry.sharedNutritionViewModel(navController: NavHostController): NutritionViewModel {
    val graphEntry = remember(this) {
        navController.getBackStackEntry(AppDestination.NutritionGraph.route)
    }
    return hiltViewModel(graphEntry)
}
