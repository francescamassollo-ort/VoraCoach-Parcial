package com.example.voracoach.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.voracoach.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

// DAO para gestionar la persistencia y consultas de comidas en la base de datos local.
@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("SELECT * FROM meals_table WHERE date = :date ORDER BY id ASC")
    fun getMealsByDate(date: String): Flow<List<MealEntity>>

    // Cantidad de comidas guardadas: el repositorio la usa para saber si la tabla está vacía
    @Query("SELECT COUNT(*) FROM meals_table")
    suspend fun countMeals(): Int
}
