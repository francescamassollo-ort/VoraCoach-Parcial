package com.example.voracoach.model

// Modelos de la app para entrenamientos. Son lo que usan los ViewModels y las pantallas.
// Están separados de los DTO (el JSON de la API) y de las Entity (las tablas de Room):
// si la API cambia el nombre de un campo, solo cambia el DTO y su conversión, no la UI.

// Nivel de dificultad. La API lo manda como texto ("beginner"...); acá es un enum para que
// la UI no compare strings y el compilador avise si falta contemplar algún caso.
enum class WorkoutLevel {
    BEGINNER, INTERMEDIATE, ADVANCED, UNKNOWN
}

// Un elemento del catálogo: lo necesario para mostrarlo en la lista, sin los ejercicios.
data class WorkoutSummary(
    val id: String,
    val name: String,
    val level: WorkoutLevel,
    val durationMinutes: Int,
    val exerciseCount: Int
)

// El entrenamiento completo, como lo devuelve el detalle.
data class Workout(
    val id: String,
    val name: String,
    val level: WorkoutLevel,
    val durationMinutes: Int,
    val description: String,
    val exercises: List<Exercise>
)

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val instructions: String,
    val tip: String
)
