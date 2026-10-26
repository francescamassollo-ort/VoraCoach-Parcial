package com.example.voracoach.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.voracoach.data.local.entity.ExerciseEntity
import com.example.voracoach.data.local.entity.WorkoutEntity
import com.example.voracoach.data.local.entity.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

// DAO
@Dao
interface WorkoutDao {

    // Solo los que vinieron en el último catálogo descargado
    @Query("SELECT * FROM workouts WHERE inCatalog = 1 ORDER BY catalogPosition")
    fun observeCatalog(): Flow<List<WorkoutEntity>>

    // @Transaction porque @Relation hace dos consultas (entrenamiento y ejercicios) y tienen que leerse juntas, sin que un guardado se meta en el medio
    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun observeWorkout(workoutId: String): Flow<WorkoutWithExercises?>

    // Guarda el catálogo sin pisar los detalles ya descargados.
    // Primero saca a todos del catálogo y después vuelve a marcar los que llegaron: si la API
    // devuelve una lista vacía, el catálogo queda vacío, pero los detalles siguen guardados.
    // @Transaction: todo se aplica junto (o nada), y la pantalla recibe un solo cambio al final.
    @Transaction
    suspend fun saveCatalog(workouts: List<WorkoutEntity>) {
        removeAllFromCatalog()
        workouts.forEach { workout ->
            insertIfAbsent(workout)
            updateSummary(
                id = workout.id,
                name = workout.name,
                level = workout.level,
                durationMinutes = workout.durationMinutes,
                exerciseCount = workout.exerciseCount,
                catalogPosition = workout.catalogPosition
            )
        }
    }

    // Guarda un detalle completo, actualiza los datos del entrenamiento y reemplaza sus ejercicios.
    // No toca catalogPosition ni inCatalog, que dependen del catálogo y no del detalle.
    @Transaction
    suspend fun saveDetail(workout: WorkoutEntity, exercises: List<ExerciseEntity>) {
        insertIfAbsent(workout)
        updateDetail(
            id = workout.id,
            name = workout.name,
            level = workout.level,
            durationMinutes = workout.durationMinutes,
            exerciseCount = workout.exerciseCount,
            description = workout.description
        )
        deleteExercises(workout.id)
        insertExercises(exercises)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(workout: WorkoutEntity)

    @Query("UPDATE workouts SET inCatalog = 0")
    suspend fun removeAllFromCatalog()

    @Query(
        """
        UPDATE workouts
        SET name = :name, level = :level, durationMinutes = :durationMinutes,
            exerciseCount = :exerciseCount, catalogPosition = :catalogPosition, inCatalog = 1
        WHERE id = :id
        """
    )
    suspend fun updateSummary(
        id: String,
        name: String,
        level: String,
        durationMinutes: Int,
        exerciseCount: Int,
        catalogPosition: Int
    )

    @Query(
        """
        UPDATE workouts
        SET name = :name, level = :level, durationMinutes = :durationMinutes,
            exerciseCount = :exerciseCount, description = :description, hasDetail = 1
        WHERE id = :id
        """
    )
    suspend fun updateDetail(
        id: String,
        name: String,
        level: String,
        durationMinutes: Int,
        exerciseCount: Int,
        description: String?
    )

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercises(workoutId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)
}
