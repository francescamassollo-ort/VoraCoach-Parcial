package com.example.voracoach.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.voracoach.data.local.entity.MealEntity
import com.example.voracoach.data.local.entity.MealIngredientEntity
import kotlinx.coroutines.flow.Flow

// DAO para gestionar la persistencia y consultas de comidas en la base de datos local.
@Dao
interface MealDao {

    // Devuelve el id generado: hace falta para guardar los ingredientes de esa comida.
    // (Sin REPLACE: reemplazar borraría la fila y, por el CASCADE, también sus ingredientes.)
    @Insert
    suspend fun insertMeal(meal: MealEntity): Long

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("SELECT * FROM meals_table WHERE date = :date ORDER BY time ASC, id ASC")
    fun getMealsByDate(date: String): Flow<List<MealEntity>>

    // Una comida por id (para el detalle). Flow<...?>: emite null si se borra.
    @Query("SELECT * FROM meals_table WHERE id = :id")
    fun observeMeal(id: Int): Flow<MealEntity?>

    // Cantidad de comidas guardadas: el repositorio la usa para saber si la tabla está vacía
    @Query("SELECT COUNT(*) FROM meals_table")
    suspend fun countMeals(): Int

    // --- Ingredientes ---

    @Insert
    suspend fun insertIngredients(ingredients: List<MealIngredientEntity>)

    @Query("SELECT * FROM meal_ingredients WHERE mealId = :mealId ORDER BY id ASC")
    fun observeIngredients(mealId: Int): Flow<List<MealIngredientEntity>>

    // Lectura única (sin Flow) para editar o duplicar una comida
    @Query("SELECT * FROM meal_ingredients WHERE mealId = :mealId ORDER BY id ASC")
    suspend fun getIngredients(mealId: Int): List<MealIngredientEntity>

    @Query("SELECT * FROM meals_table WHERE id = :id")
    suspend fun getMeal(id: Int): MealEntity?

    @Query("DELETE FROM meal_ingredients WHERE mealId = :mealId")
    suspend fun deleteIngredients(mealId: Int)

    // @Transaction: la comida y sus ingredientes se guardan juntos (o nada).
    // Primero la comida, para conocer su id; después los ingredientes apuntando a ese id.
    @Transaction
    suspend fun insertMealWithIngredients(meal: MealEntity, ingredients: List<MealIngredientEntity>): Long {
        val mealId = insertMeal(meal)
        insertIngredients(ingredients.map { it.copy(id = 0, mealId = mealId.toInt()) })
        return mealId
    }

    // Editar: actualiza los totales y reemplaza la lista de ingredientes completa
    @Transaction
    suspend fun updateMealWithIngredients(meal: MealEntity, ingredients: List<MealIngredientEntity>) {
        updateMeal(meal)
        deleteIngredients(meal.id)
        insertIngredients(ingredients.map { it.copy(id = 0, mealId = meal.id) })
    }
}
