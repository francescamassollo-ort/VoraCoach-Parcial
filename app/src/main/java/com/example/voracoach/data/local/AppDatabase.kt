package com.example.voracoach.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.voracoach.data.local.entity.ExerciseEntity
import com.example.voracoach.data.local.entity.MealEntity
import com.example.voracoach.data.local.entity.MealIngredientEntity
import com.example.voracoach.data.local.entity.SavedMealEntity
import com.example.voracoach.data.local.entity.WorkoutEntity

// Base de datos única de la app, compartida por todas las áreas.
// Hay una sola instancia (la provee Hilt como @Singleton en di/DatabaseModule).
@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        MealEntity::class,
        MealIngredientEntity::class,
        SavedMealEntity::class,
    ],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    abstract fun mealDao(): MealDao

    abstract fun savedMealDao(): SavedMealDao
}
