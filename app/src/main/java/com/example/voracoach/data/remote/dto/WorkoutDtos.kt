package com.example.voracoach.data.remote.dto

import com.example.voracoach.model.Exercise
import com.example.voracoach.model.Workout
import com.example.voracoach.model.WorkoutLevel
import com.example.voracoach.model.WorkoutSummary

// DTO (Data Transfer Object)

// Todas las respuestas exitosas de la API vienen envueltas en { "data": ... }
data class DataResponse<T>(
    val data: T?
)

// Respuestas de error: { "error": { "code": "...", "message": "..." } }
data class ErrorResponse(
    val error: ErrorDto?
)

data class ErrorDto(
    val code: String?,
    val message: String?
)

// GET /v1/workouts
data class WorkoutSummaryDto(
    val id: String?,
    val name: String?,
    val level: String?,
    val durationMinutes: Int?,
    val exerciseCount: Int?
)

// GET /v1/workouts/{workoutId}
data class WorkoutDto(
    val id: String?,
    val name: String?,
    val level: String?,
    val durationMinutes: Int?,
    val exerciseCount: Int?,
    val description: String?,
    val exercises: List<ExerciseDto>?
)

data class ExerciseDto(
    val id: String?,
    val name: String?,
    val muscleGroup: String?,
    val sets: Int?,
    val reps: Int?,
    val restSeconds: Int?,
    val instructions: String?,
    val tip: String?
)

// Conversión DTO -> modelo. Sin id no se puede identificar el elemento (ni abrir su detalle,
// ni guardarlo en Room), así que se descarta devolviendo null. Al resto de los campos
// faltantes se les da un valor neutro para poder mostrarlos igual.

fun WorkoutSummaryDto.toModel(): WorkoutSummary? {
    val id = id ?: return null
    return WorkoutSummary(
        id = id,
        name = name.orEmpty(),
        level = level.toWorkoutLevel(),
        durationMinutes = durationMinutes ?: 0,
        exerciseCount = exerciseCount ?: 0
    )
}

fun WorkoutDto.toModel(): Workout? {
    val id = id ?: return null
    return Workout(
        id = id,
        name = name.orEmpty(),
        level = level.toWorkoutLevel(),
        durationMinutes = durationMinutes ?: 0,
        description = description.orEmpty(),
        exercises = exercises.orEmpty().mapNotNull { it.toModel() }
    )
}

fun ExerciseDto.toModel(): Exercise? {
    val id = id ?: return null
    return Exercise(
        id = id,
        name = name.orEmpty(),
        muscleGroup = muscleGroup.orEmpty(),
        sets = sets ?: 0,
        reps = reps ?: 0,
        restSeconds = restSeconds ?: 0,
        instructions = instructions.orEmpty(),
        tip = tip.orEmpty()
    )
}

// La API manda el nivel en minúsculas ("beginner") cualquier otro valor queda como UNKNOWN
fun String?.toWorkoutLevel(): WorkoutLevel = when (this?.lowercase()) {
    "beginner" -> WorkoutLevel.BEGINNER
    "intermediate" -> WorkoutLevel.INTERMEDIATE
    "advanced" -> WorkoutLevel.ADVANCED
    else -> WorkoutLevel.UNKNOWN
}
