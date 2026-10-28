package com.example.voracoach.model

enum class FitnessGoal { BUILD_STRENGTH, IMPROVE_FITNESS, LOSE_BODY_FAT, BUILD_HEALTHY_HABITS }

// "6+ days" se guarda como 6 días
enum class TrainingFrequency(val days: Int) { THREE(3), FOUR(4), FIVE(5), SIX_PLUS(6) }

enum class WorkoutType { STRENGTH, CARDIO, HIIT, MOBILITY, MIXED }

// Lo comparte Goals & Preferences con Voice & AI Settings
enum class CoachingStyle { SUPPORTIVE, BALANCED, DIRECT }

// Objetivos y preferencias de entrenamiento. Los objetivos se pueden elegir varios.
data class GoalsPreferences(
    val goals: Set<FitnessGoal>,
    val frequency: TrainingFrequency,
    val workoutType: WorkoutType,
    val coachingStyle: CoachingStyle
)