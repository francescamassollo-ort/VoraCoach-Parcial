package com.example.voracoach.model

// Período elegido en las pestañas Week / Month / 3 Months de Progress
enum class ProgressPeriod { WEEK, MONTH, THREE_MONTHS }

// Resumen de un período en Progress / 02 — Dashboard. Los deltas son la mejora contra el período anterior.
data class ProgressSummary(
    val workouts: Int,
    val workoutsDelta: Int,
    val avgSteps: Int,
    val stepsDeltaPercent: Int,
    val avgProteinGrams: Int,
    val proteinDeltaPercent: Int,
    val streakDays: Int,
    val volumeIncreasePercent: Int,
    val volumeKg: Int,
    val proteinTargetPercent: Int,
    val avgDistanceKm: Double,
    val goalCompletionPercent: Int
)

// --- Progress / 03 — Workout progress ---

// gainKg = 0 significa que el peso se mantuvo ("Stable")
data class ExerciseProgress(
    val name: String,
    val weightKg: Double,
    val reps: Int,
    val gainKg: Double
)

data class WorkoutProgressData(
    val sessions: Int,
    val volumeKg: Int,
    val avgMinutes: Int,
    val volumeChangePercent: Int,
    // Volumen de cada barra del gráfico: días (semana), semanas (mes) o meses (3 meses)
    val volumeBars: List<Int>,
    val personalBest: ExerciseProgress,
    val exercises: List<ExerciseProgress>
)

// --- Progress / 01 — Today's Overview ---

data class TodayProgress(
    val completionPercent: Int,
    val workoutPercent: Int,
    val caloriesConsumed: Int,
    val caloriesGoal: Int,
    val steps: Int,
    val stepsGoal: Int,
    val waterLitres: Double,
    val waterGoalLitres: Double,
    val workoutsThisWeek: Int
)

// --- Progress / 05 — Health trends ---

enum class HabitSignal { SLEEP_QUALITY, DAILY_MOVEMENT, WORKOUT_FREQUENCY, PROTEIN_INTAKE }

enum class Trend { IMPROVING, CONSISTENT }

data class HealthTrendsData(
    val avgSteps: Int,
    val avgDistanceKm: Double,
    val activeKcal: Int,
    val stepsGoal: Int,
    // Pasos de cada día de la semana, de lunes a domingo
    val dailySteps: List<Int>,
    val signals: List<Pair<HabitSignal, Trend>>
)

// --- Progress / 04 — Nutrition progress ---

enum class Macro { CALORIES, PROTEIN, CARBS, FAT }

data class NutritionProgressData(
    val avgCalories: Int,
    val avgProteinGrams: Int,
    val daysOnTarget: Int,
    val totalDays: Int,
    val frequentLunch: String,
    val topProteinSource: String,
    val avgBreakfastKcal: Int,
    val avgDinnerKcal: Int
)
