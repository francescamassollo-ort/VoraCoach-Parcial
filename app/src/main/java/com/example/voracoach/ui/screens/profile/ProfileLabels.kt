package com.example.voracoach.ui.screens.profile

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.voracoach.R
import com.example.voracoach.model.CoachingStyle
import com.example.voracoach.model.DietType
import com.example.voracoach.model.FavoriteFood
import com.example.voracoach.model.FitnessGoal
import com.example.voracoach.model.NutritionPriority
import com.example.voracoach.model.TrainingFrequency
import com.example.voracoach.model.WorkoutType

// Los enums no llevan texto: acá se elige el string de cada uno (strings.xml).
// Lo usan el Dashboard, Goals & Preferences y, más adelante, Voice & AI Settings.

@StringRes
fun FitnessGoal.labelRes(): Int = when (this) {
    FitnessGoal.BUILD_STRENGTH -> R.string.goal_build_strength
    FitnessGoal.IMPROVE_FITNESS -> R.string.goal_improve_fitness
    FitnessGoal.LOSE_BODY_FAT -> R.string.goal_lose_body_fat
    FitnessGoal.BUILD_HEALTHY_HABITS -> R.string.goal_build_healthy_habits
}

// En Figma solo los dos primeros objetivos llevan descripción
@StringRes
fun FitnessGoal.descriptionRes(): Int? = when (this) {
    FitnessGoal.BUILD_STRENGTH -> R.string.goal_build_strength_desc
    FitnessGoal.IMPROVE_FITNESS -> R.string.goal_improve_fitness_desc
    else -> null
}

@StringRes
fun TrainingFrequency.labelRes(): Int = when (this) {
    TrainingFrequency.THREE -> R.string.frequency_3_days
    TrainingFrequency.FOUR -> R.string.frequency_4_days
    TrainingFrequency.FIVE -> R.string.frequency_5_days
    TrainingFrequency.SIX_PLUS -> R.string.frequency_6_plus_days
}

@StringRes
fun WorkoutType.labelRes(): Int = when (this) {
    WorkoutType.STRENGTH -> R.string.workout_strength
    WorkoutType.CARDIO -> R.string.workout_cardio
    WorkoutType.HIIT -> R.string.workout_hiit
    WorkoutType.MOBILITY -> R.string.workout_mobility
    WorkoutType.MIXED -> R.string.workout_mixed
}

@StringRes
fun CoachingStyle.labelRes(): Int = when (this) {
    CoachingStyle.SUPPORTIVE -> R.string.coaching_supportive
    CoachingStyle.BALANCED -> R.string.coaching_balanced
    CoachingStyle.DIRECT -> R.string.coaching_direct
}

@StringRes
fun CoachingStyle.descriptionRes(): Int = when (this) {
    CoachingStyle.SUPPORTIVE -> R.string.coaching_supportive_desc
    CoachingStyle.BALANCED -> R.string.coaching_balanced_desc
    CoachingStyle.DIRECT -> R.string.coaching_direct_desc
}

@StringRes
fun DietType.labelRes(): Int = when (this) {
    DietType.EVERYTHING -> R.string.diet_everything
    DietType.VEGETARIAN -> R.string.diet_vegetarian
    DietType.VEGAN -> R.string.diet_vegan
    DietType.PESCATARIAN -> R.string.diet_pescatarian
    DietType.OTHER -> R.string.diet_other
}

@StringRes
fun FavoriteFood.labelRes(): Int = when (this) {
    FavoriteFood.CHICKEN -> R.string.food_chicken
    FavoriteFood.EGGS -> R.string.food_eggs
    FavoriteFood.FISH -> R.string.food_fish
    FavoriteFood.RICE -> R.string.food_rice
    FavoriteFood.PASTA -> R.string.food_pasta
    FavoriteFood.GREEK_YOGHURT -> R.string.food_greek_yoghurt
    FavoriteFood.VEGETABLES -> R.string.food_vegetables
    FavoriteFood.FRUIT -> R.string.food_fruit
}

@StringRes
fun NutritionPriority.labelRes(): Int = when (this) {
    NutritionPriority.HIT_PROTEIN_TARGETS -> R.string.priority_hit_protein
    NutritionPriority.EAT_MORE_CONSISTENTLY -> R.string.priority_eat_consistently
}

// Resumen de objetivos en una línea: "Build strength & improve fitness"
@Composable
fun goalsSummary(goals: List<FitnessGoal>): String {
    val labels = goals.mapIndexed { index, goal ->
        val label = stringResource(goal.labelRes())
        if (index == 0) label else label.replaceFirstChar(Char::lowercase)
    }
    return when (labels.size) {
        0 -> ""
        1 -> labels.first()
        else -> labels.dropLast(1).joinToString(", ") + " & " + labels.last()
    }
}