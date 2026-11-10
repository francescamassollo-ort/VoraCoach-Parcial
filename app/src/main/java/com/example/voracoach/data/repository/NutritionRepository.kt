package com.example.voracoach.data.repository

import com.example.voracoach.data.local.MealDao
import com.example.voracoach.data.local.SavedMealDao
import com.example.voracoach.data.local.entity.toEntity
import com.example.voracoach.data.local.entity.toModel
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealType
import com.example.voracoach.model.SavedMeal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionRepository @Inject constructor(
    private val mealDao: MealDao,
    private val savedMealDao: SavedMealDao,
) {

    fun getMealsByDate(date: String): Flow<List<Meal>> =
        mealDao.getMealsByDate(date).map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun insertMeal(meal: Meal) {
        mealDao.insertMeal(meal.toEntity())
    }

    suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal.toEntity())
    }

    // La primera vez que se abre la app la tabla está vacía: se cargan comidas de ejemplo
    // para la fecha dada, así el Dashboard no arranca en cero (igual que el catálogo de Workout).
    suspend fun seedIfEmpty(date: String) {
        if (mealDao.countMeals() > 0) return
        demoMeals(date).forEach { mealDao.insertMeal(it.toEntity()) }
    }

    private fun demoMeals(date: String) = listOf(
        Meal(name = "Oatmeal with banana", date = date, type = MealType.BREAKFAST,
            calories = 380, protein = 14f, carbs = 62f, fat = 8f),
        Meal(name = "Grilled chicken salad", date = date, type = MealType.LUNCH,
            calories = 520, protein = 42f, carbs = 30f, fat = 22f),
        Meal(name = "Greek yogurt with nuts", date = date, type = MealType.AFTERNOON_SNACK,
            calories = 260, protein = 18f, carbs = 16f, fat = 14f),
    )

    // --- Comidas guardadas ("Save without logging" y "Search Database") ---

    fun searchSavedMeals(query: String): Flow<List<SavedMeal>> =
        savedMealDao.search(query.trim()).map { entities -> entities.map { it.toModel() } }

    suspend fun saveMeal(meal: SavedMeal) {
        savedMealDao.insert(meal.toEntity())
    }

    suspend fun deleteSavedMeal(meal: SavedMeal) {
        savedMealDao.delete(meal.toEntity())
    }

    // Igual que con las comidas del día: si no hay guardadas, carga algunas de ejemplo
    // para que la búsqueda no arranque vacía.
    suspend fun seedSavedMealsIfEmpty() {
        if (savedMealDao.count() > 0) return
        demoSavedMeals().forEach { savedMealDao.insert(it.toEntity()) }
    }

    private fun demoSavedMeals() = listOf(
        SavedMeal(name = "Chicken, rice and vegetables", calories = 520, protein = 38f, carbs = 54f, fat = 14f),
        SavedMeal(name = "Protein smoothie", calories = 310, protein = 30f, carbs = 35f, fat = 6f),
        SavedMeal(name = "Avocado toast with eggs", calories = 420, protein = 20f, carbs = 32f, fat = 24f),
        SavedMeal(name = "Tuna salad", calories = 350, protein = 32f, carbs = 12f, fat = 18f),
    )
}
