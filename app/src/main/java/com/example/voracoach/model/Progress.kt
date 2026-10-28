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
