package com.example.voracoach.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.voracoach.model.Exercise
import com.example.voracoach.model.Workout
import com.example.voracoach.model.WorkoutLevel
import com.example.voracoach.model.WorkoutSummary

// Entities: las tablas de Room para la caché de entrenamientos.
// Se guardan separadas de los modelos de la app para que un cambio en la base
// (agregar una columna, renombrarla) no obligue a tocar las pantallas.

// Un entrenamiento. Se crea al descargar el catálogo y se completa al descargar su detalle.
@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val name: String,
    // El enum se guarda por su nombre ("BEGINNER"): Room no sabe guardar enums por sí solo
    val level: String,
    val durationMinutes: Int,
    val exerciseCount: Int,
    // Orden en que lo devolvió la API, para mostrar el catálogo igual que el servidor
    val catalogPosition: Int,
    // Si está en el último catálogo descargado. Si el servidor deja de devolverlo (o devuelve
    // una lista vacía), sale de la lista pero su detalle sigue en caché por si vuelve.
    val inCatalog: Boolean,
    // Null hasta que se descargue el detalle
    val description: String?,
    // Distingue un detalle en caché de uno nunca descargado (RF02: "no disponible sin conexión")
    val hasDetail: Boolean
)

// Ejercicios de un entrenamiento. La clave es (workoutId, id) porque distintos entrenamientos
// podrían repetir ids de ejercicio. CASCADE: si se borra el entrenamiento, se borran sus ejercicios.
@Entity(
    tableName = "workout_exercises",
    primaryKeys = ["workoutId", "id"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Índice para que buscar los ejercicios de un entrenamiento no recorra toda la tabla
    indices = [Index("workoutId")]
)
data class ExerciseEntity(
    val workoutId: String,
    val id: String,
    // Orden dentro del entrenamiento (la base no garantiza devolverlos en el orden en que se guardaron)
    val position: Int,
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val restSeconds: Int,
    val instructions: String,
    val tip: String
)

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(parentColumn = "id", entityColumn = "workoutId")
    val exercises: List<ExerciseEntity>
)

// --- Conversiones modelo -> Entity (para guardar) ---

fun WorkoutSummary.toEntity(catalogPosition: Int) = WorkoutEntity(
    id = id,
    name = name,
    level = level.name,
    durationMinutes = durationMinutes,
    exerciseCount = exerciseCount,
    catalogPosition = catalogPosition,
    inCatalog = true,
    description = null,
    hasDetail = false
)

// Al guardar un detalle, el DAO solo actualiza los campos del detalle: la posición e inCatalog
// de acá se usan únicamente si el entrenamiento todavía no existía en la base.
fun Workout.toEntity() = WorkoutEntity(
    id = id,
    name = name,
    level = level.name,
    durationMinutes = durationMinutes,
    exerciseCount = exercises.size,
    catalogPosition = Int.MAX_VALUE,
    inCatalog = false,
    description = description,
    hasDetail = true
)

fun Workout.toExerciseEntities(): List<ExerciseEntity> = exercises.mapIndexed { index, exercise ->
    ExerciseEntity(
        workoutId = id,
        id = exercise.id,
        position = index,
        name = exercise.name,
        muscleGroup = exercise.muscleGroup,
        sets = exercise.sets,
        reps = exercise.reps,
        restSeconds = exercise.restSeconds,
        instructions = exercise.instructions,
        tip = exercise.tip
    )
}

// --- Conversiones Entity -> modelo (para mostrar) ---

fun WorkoutEntity.toSummary() = WorkoutSummary(
    id = id,
    name = name,
    level = level.toLevel(),
    durationMinutes = durationMinutes,
    exerciseCount = exerciseCount
)

// Devuelve null si el detalle nunca se descargó: la pantalla lo informa como no disponible sin conexión
fun WorkoutWithExercises.toModel(): Workout? {
    if (!workout.hasDetail) return null
    return Workout(
        id = workout.id,
        name = workout.name,
        level = workout.level.toLevel(),
        durationMinutes = workout.durationMinutes,
        description = workout.description.orEmpty(),
        exercises = exercises.sortedBy { it.position }.map { it.toModel() }
    )
}

private fun ExerciseEntity.toModel() = Exercise(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    sets = sets,
    reps = reps,
    restSeconds = restSeconds,
    instructions = instructions,
    tip = tip
)

private fun String.toLevel(): WorkoutLevel =
    WorkoutLevel.entries.firstOrNull { it.name == this } ?: WorkoutLevel.UNKNOWN
