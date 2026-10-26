package com.example.voracoach.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.voracoach.data.local.entity.ExerciseEntity
import com.example.voracoach.data.local.entity.WorkoutEntity

// Base de datos única de la app, compartida por todas las áreas.
// Hay una sola instancia (la provee Hilt como @Singleton en di/DatabaseModule).
//
// IMPORTANTE NOTA PARA NUTRITION (Nahuel): no crear otra base de datos. Para sumar las comidas:
//   1. Agregar MealEntity a la lista de entities de abajo.
//   2. Agregar "abstract fun mealDao(): MealDao" debajo de workoutDao().
//   3. Subir version en 1 (de 1 a 2).
//   4. En di/DatabaseModule, agregar un @Provides para MealDao igual al de WorkoutDao.
@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
}
