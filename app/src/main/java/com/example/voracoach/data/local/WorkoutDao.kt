package com.example.voracoach.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.voracoach.data.local.entity.ExerciseEntity
import com.example.voracoach.data.local.entity.WorkoutEntity
import com.example.voracoach.data.local.entity.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

// DAO
@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts ORDER BY catalogPosition")
    fun observeCatalog(): Flow<List<WorkoutEntity>>

    // @Transaction porque @Relation hace dos consultas (entrenamiento y ejercicios) y tienen que leerse juntas, sin que un guardado se meta en el medio
    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun observeWorkout(workoutId: String): Flow<WorkoutWithExercises?>

    @Query("SELECT catalogPosition FROM workouts WHERE id = :workoutId")
    suspend fun getCatalogPosition(workoutId: String): Int?

    // Guarda el catálogo sin pisar los detalles ya descargados
    suspend fun saveCatalog(workouts: List<WorkoutEntity>) {
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

    // Guarda un detalle completo: el entrenamiento y sus ejercicios, reemplazando los anteriores
    @Transaction
    suspend fun saveDetail(workout: WorkoutEntity, exercises: List<ExerciseEntity>) {
        upsertWorkout(workout)
        deleteExercises(workout.id)
        insertExercises(exercises)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(workout: WorkoutEntity)

    @Query(
        """
        UPDATE workouts
        SET name = :name, level = :level, durationMinutes = :durationMinutes,
            exerciseCount = :exerciseCount, catalogPosition = :catalogPosition
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

    @Upsert
    suspend fun upsertWorkout(workout: WorkoutEntity)

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercises(workoutId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)
}
